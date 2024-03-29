package app.main.service;

import java.io.File;
import java.io.FilenameFilter;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;

import app.main.service.helper.FenHelper;
import app.persistence.model.AnalysisDo;
import app.persistence.model.MoveEvaluationDo;
import app.persistence.repo.AnalysisRepository;
import app.web.api.model.AnalysisDTO;
import app.web.controllers.AnalysisParameters;
import app.web.controllers.CommentParameters;
import app.web.controllers.DeleteParameters;
import app.web.controllers.DrawingParameters;
import app.web.controllers.ImportParameters;
import app.web.controllers.UpdateParameters;
import pbaioni.chesslib.Board;
import pbaioni.chesslib.game.Game;
import pbaioni.chesslib.game.GamePosition;
import pbaioni.chesslib.pgn.PgnHolder;
import stockfish4j.model.EngineEvaluation;
import stockfish4j.service.StockfishService;
import stockfish4j.service.Stockfish4jProperties;

@Service
public class AnalysisService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisService.class);

	@Autowired
	AnalysisRepository analysisRepository;

	StockfishService stockfishService = new StockfishService(new Stockfish4jProperties());

	@Autowired
	ObjectMapper mapper;

	private static String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

	private int updates;

	private boolean stopTask = false;

	public void init() {
		//stockfishService.init();

		// empty database, setting start position
		if (analysisRepository.count() == 0L) {
			AnalysisDo analysis = new AnalysisDo(START_FEN);
			analysis.setBestMove("e2e4");
			analysis.setEvaluation("20");
			analysis.setDepth(24);
			analysisRepository.save(analysis);
			LOGGER.info("Start position analysis saved: " + analysis.toString());
		}
	}

	public String welcome() {
		return "Board service is ready";
	}

	public String getOnlyPawnsFen(String fen) {
		LOGGER.debug("Cleaning pieces from fen: " + fen);
		return FenHelper.cleanPiecesFromFen(fen);
	}

	public AnalysisDTO performAnalysis(AnalysisParameters params) throws InterruptedException, ExecutionException {

		String currentFen = params.getPreviousFen();
		String nextFen = params.getFen();
		String move = params.getMove();
		int depth = params.getDepth();
		boolean useEngine = params.getUseEngine();
		AnalysisDo currentPosition = null;
		AnalysisDo nextPosition = null;
		int loss;

		// adding move to current position if needed
		if (!Objects.isNull(currentFen) && !Objects.isNull(move)) {
			currentPosition = findAnalysisInDb(FenHelper.getShortFen(currentFen));
			MoveEvaluationDo newMove = new MoveEvaluationDo(move, nextFen);
			if (!Objects.isNull(currentPosition)) {
				LOGGER.debug("CurrentFetched: " + currentPosition.toString());
				if (currentPosition.addMove(newMove)) {
					LOGGER.debug("Current move added: " + currentPosition.toString());
					analysisRepository.save(currentPosition);
					LOGGER.info("Move " + move + " added to current position");
				}
			}
		}

		// analysing new position if needed
		nextPosition = findAnalysisInDb(FenHelper.getShortFen(nextFen));
		if (!Objects.isNull(nextPosition) && depth <= nextPosition.getDepth()) {
			// position found in DB
			LOGGER.debug("Analysis fetched: " + nextPosition.toString());
		} else {
			// No result from DB, creating new analysis
			if (Objects.isNull(nextPosition)) {
				nextPosition = new AnalysisDo(nextFen);
			}
			if (useEngine) {
				LOGGER.info("No result from database, performing analysis for fen: " + nextFen);
				EngineEvaluation engineEvaluation = stockfishService.submitDepthTask(nextFen, depth).get();
				nextPosition.setEngineEvaluation(engineEvaluation);
			}
			analysisRepository.save(nextPosition);
			LOGGER.info("New analysis saved: " + nextPosition.toString());
		}

		// creating DTO object to return
		AnalysisDTO analysis = mapToDto(nextPosition);
		if (Objects.nonNull(currentPosition) && Objects.nonNull(move)) {
			AnalysisDTO prevAnalysis = mapToDto(currentPosition);
			analysis.setCentipawnLoss(String.valueOf(prevAnalysis.getMove(move).getCentipawnLoss()));
		}

		Board board = new Board();
		board.loadFromFen(nextFen);

		return analysis;

	}

	private AnalysisDTO mapToDto(AnalysisDo Do) {

		AnalysisDTO Dto = new AnalysisDTO(Do.getFen(), Do.getTurn(), Do.getDepth(), Do.getComment());
		for (MoveEvaluationDo move : Do.getMoves()) {
			Dto.addMove(move.getMove(), findAnalysisInDb(move.getNextShortFen()).getEvaluation());
		}

		// case of best move only as a result of stockfish analysis but never browsed
		Dto.addStockfishMove(Do.getBestMove(), Do.getEvaluation());

		Dto.calculateRandomMove();

		Dto.setArrows(Do.getArrows());

		Dto.setCircles(Do.getCircles());

		LOGGER.debug("DTO: " + Dto.toString());

		return Dto;
	}

	public String deleteLine(DeleteParameters params) {

		String rval = "";
		String fen = params.getFen();
		String move = params.getMove();

		// removing move evaluation from variant base
		AnalysisDo variantBase = findAnalysisInDb(FenHelper.getShortFen(fen));
		MoveEvaluationDo moveToPrune = variantBase.getEvaluationByMove(move);
		if (!Objects.isNull(moveToPrune)) {
			variantBase.removeMove(moveToPrune.getMove());
			analysisRepository.save(variantBase);

			// removing all the lines linked to this move
			deleteLine(findAnalysisInDb(moveToPrune.getNextShortFen()));

			rval = "Line " + move + " deleted from position ";
			LOGGER.info(rval + fen);
		} else {
			rval = "Line not found";
		}

		// returning analysis as json string
		return rval;

	}

	private void deleteLine(AnalysisDo analysis) {
		if (!Objects.isNull(analysis)) {
			analysisRepository.delete(analysis);
			LOGGER.info("Analysis deleted for fen: " + analysis.getFen());
			for (MoveEvaluationDo moveEval : analysis.getMoves()) {
				if (!Objects.isNull(moveEval.getNextShortFen())) {
					deleteLine(findAnalysisInDb(moveEval.getNextShortFen()));
				}
			}
		}
	}

	private AnalysisDo findAnalysisInDb(String shortFen) {
		Optional<AnalysisDo> databaseAnalysis;
		AnalysisDo analysis = null;
		databaseAnalysis = analysisRepository.findById(shortFen);
		if (databaseAnalysis.isPresent()) {
			analysis = databaseAnalysis.get();
		}
		return analysis;
	}

	public void stop() throws InterruptedException {
		stockfishService.cancelAll();;
	}

	// methods for command controller

	public void dropAll() {
		analysisRepository.deleteAll();
	}

	public String updateDepth(UpdateParameters params, boolean forceUpdate) throws InterruptedException, ExecutionException {
		String fen = params.getFen();
		Integer depth = params.getDepth();
		updates = 0;
		Instant start = Instant.now();
		LOGGER.info("Starting line update to depth " + depth);

		AnalysisDo startPosition = findAnalysisInDb(FenHelper.getShortFen(fen));
		updatePositionDepth(startPosition, depth, forceUpdate);

		Instant finish = Instant.now();
		long seconds = Duration.between(start, finish).getSeconds();
		if (updates != 0) {
			LOGGER.info(updates + " positions updated in " + seconds + " seconds [average: " + seconds / updates
					+ " seconds per position");
		} else {
			LOGGER.info("No positions to update");
		}

		if (stopTask) {
			stopTask = false;
			return "Update Stopped";
		} else {
			return "Update completed";
		}
	}

	private void updatePositionDepth(AnalysisDo position, int depth, boolean forceUpdate) throws InterruptedException, ExecutionException {

		if (!stopTask) {
			if (position.getDepth() < depth || forceUpdate) {
				EngineEvaluation engineEvaluation = stockfishService.submitDepthTask(position.getFen(), depth).get();
				position.setEngineEvaluation(engineEvaluation);
				analysisRepository.save(position);
				updates++;
			}
			for (MoveEvaluationDo move : position.getMoves()) {
				updatePositionDepth(findAnalysisInDb(move.getNextShortFen()), depth, forceUpdate);
			}
		}

	}

	public String setComment(CommentParameters params) {

		AnalysisDo analysis = findAnalysisInDb(FenHelper.getShortFen(params.getFen()));
		analysis.setComment(params.getComment());
		analysisRepository.save(analysis);
		return "Comment set!";

	}

	public String fillDatabaseFromPGN(ImportParameters params) throws Exception {

		File dir = new File("./import/");
		File[] pgnFiles = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".pgn");
			}
		});

		File imported = new File("./import/imported");
		if (!imported.exists()) {
			imported.mkdirs();
		}

		for (File pgnToLoad : pgnFiles) {

			LOGGER.info("Loading pgn file: " + pgnToLoad.getName());

			// load pgn games from file
			PgnHolder pgn = new PgnHolder(pgnToLoad.getAbsolutePath());
			pgn.loadPgn();

			// browse the imported games
			List<Game> games = pgn.getGames();

			for (Game game : games) {
				if (!stopTask) {

					LOGGER.info("Game #" + (games.indexOf(game) + 1) + "\n" + game.toString());

					Board board = new Board();

					// searching for unanalyzed moves in the opening
					for (GamePosition pos : game.getAllPositions()) {
						if (pos.getPly() <= params.getOpeningDepth() && !stopTask) {
							String previousFen = pos.getFen();
							board.loadFromFen(previousFen);
							board.doMove(pos.getMove());
							String nextFen = board.getFen();

							// analyzing move
							performAnalysis(new AnalysisParameters(previousFen, pos.getUciMove(), nextFen,
									params.getAnalysisDepth(), true));

							// Setting pgn comment
							String pgnComment = pos.getComment();
							if (!Objects.isNull(pgnComment)) {
								String oldComment = findAnalysisInDb(FenHelper.getShortFen(nextFen)).getComment();
								String newComment = "";
								if (Objects.isNull(oldComment)) {
									newComment = "[Theory]: " + pgnComment;
									setComment(new CommentParameters(nextFen, newComment));
								} else {
									if (!oldComment.contains(pgnComment)) {
										newComment = oldComment + "\n\n[Theory]: " + pgnComment;
										setComment(new CommentParameters(nextFen, newComment));
									}
								}

							}

							// Setting arrows
							String arrows = pos.getArrows();
							if (!Objects.isNull(arrows)) {
								for (String arrow : arrows.split(",")) {
									arrow = arrow.trim();
									String regex = "^[A-Z]{1}[a-z]{1}[0-9]{1}[a-z]{1}[0-9]{1}$";
									Matcher m = Pattern.compile(regex).matcher(arrow);
									if (m.matches()) {
										updateDrawing(new DrawingParameters(nextFen, arrow));
									}
								}

							}

							// Setting circles
							String circles = pos.getCircles();
							if (!Objects.isNull(circles)) {
								for (String circle : circles.split(",")) {
									circle = circle.trim();
									String regex = "^[A-Z]{1}[a-z]{1}[0-9]{1}$";
									Matcher m = Pattern.compile(regex).matcher(circle);
									if (m.matches()) {
										updateDrawing(new DrawingParameters(nextFen, circle));
									}
								}
							}
						}

					}

				}

			}

			if (!stopTask) {
				Files.move(pgnToLoad, new File(imported.getAbsolutePath() + "/" + pgnToLoad.getName()));
				LOGGER.info("File " + pgnToLoad.getName() + " imported and archived");
			}
		}

		if (stopTask) {
			stopTask = false;
			return "Import Stopped";
		} else {
			String rval = "";
			if (pgnFiles.length == 0) {
				rval = "No file to import";
			} else {
				rval = "Import completed";
			}
			return rval;
		}
	}

	public void stopTask() throws InterruptedException {

		LOGGER.info("Stopping task");
		this.stopTask = true;
		stockfishService.cancelAll();
	}

	public void updateDrawing(DrawingParameters params) {
		AnalysisDo analysis = findAnalysisInDb(FenHelper.getShortFen(params.getFen()));
		analysis.updateDrawing(params.getDrawing());
		analysisRepository.save(analysis);
	}

	public void shutdown() {

		System.exit(0);

	}

}
