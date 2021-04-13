package app.main.service;

import java.io.File;
import java.io.FilenameFilter;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.google.gson.Gson;

import app.main.service.helper.FenHelper;
import app.persistence.model.AnalysisDo;
import app.persistence.model.DrawingDo;
import app.persistence.model.MoveEvaluationDo;
import app.persistence.repo.AnalysisRepository;
import app.stockfish.engine.EngineEvaluation;
import app.stockfish.service.StockfishService;
import app.web.api.model.AnalysisDTO;
import app.web.api.model.MoveEvaluationDTO;
import app.web.api.model.SimpleResponseWrapper;
import pbaioni.chesslib.Board;
import pbaioni.chesslib.game.Game;
import pbaioni.chesslib.move.Influence;
import pbaioni.chesslib.move.InfluenceGenerator;
import pbaioni.chesslib.move.Move;
import pbaioni.chesslib.move.MoveList;
import pbaioni.chesslib.pgn.PgnHolder;

@Service
public class AnalysisService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisService.class);

	@Autowired
	AnalysisRepository analysisRepository;

	@Autowired
	StockfishService stockfishService;

	@Autowired
	ObjectMapper mapper;

	private static String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

	private int updates;

	private boolean stopTask = false;

	public void init() {
		stockfishService.init();

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
		return wrapResponse(new SimpleResponseWrapper("Board service is ready"));
	}

	public String getOnlyPawnsFen(String fen) {
		LOGGER.debug("Cleaning pieces from fen: " + fen);
		return wrapResponse(new SimpleResponseWrapper(FenHelper.cleanPiecesFromFen(fen)));
	}

	public String performAnalysis(String currentFen, String move, String nextFen, Integer depth, boolean useEngine) {

		// adding move to current position if needed
		if (!Objects.isNull(currentFen) && !Objects.isNull(move)) {
			AnalysisDo currentPosition = findAnalysisInDb(FenHelper.getShortFen(currentFen));
			MoveEvaluationDo newMove = new MoveEvaluationDo(move, nextFen);
			if (!Objects.isNull(currentPosition)) {
				LOGGER.debug("CurrentFetched: " + currentPosition.toString());
				if (currentPosition.addMove(newMove)) {
					LOGGER.debug("Current with move added: " + currentPosition.toString());
					analysisRepository.save(currentPosition);
					LOGGER.info("Move " + move + " added to current position");
				}
			}
		}

		// analysing new position if needed
		AnalysisDo nextPosition = findAnalysisInDb(FenHelper.getShortFen(nextFen));
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
				EngineEvaluation engineEvaluation = stockfishService.getEngineEvaluation(nextFen, depth);
				nextPosition.setEngineEvaluation(engineEvaluation);
			}
			analysisRepository.save(nextPosition);
			LOGGER.info("New analysis saved: " + nextPosition.toString());
		}

		// creating DTO object to return
		AnalysisDTO analysis = mapToDto(nextPosition);
		Board board = new Board();
		board.loadFromFen(nextFen);
		analysis.setInfluences(board.getInfluence());

		Gson g = new Gson();
		return g.toJson(analysis);

	}

	private AnalysisDTO mapToDto(AnalysisDo Do) {

		AnalysisDTO analysis = new AnalysisDTO(Do.getFen(), Do.getTurn(), Do.getDepth(), Do.getComment());
		for (MoveEvaluationDo move : Do.getMoves()) {
			analysis.addMove(move.getMove(), findAnalysisInDb(move.getNextShortFen()).getEvaluation());
		}

		// case of best move only as a result of stockfish analysis but never browsed
		analysis.addStockfishMove(Do.getBestMove(), Do.getEvaluation());

		analysis.setDrawings(Do.getDrawings());

		LOGGER.debug("DTO: " + analysis.toString());

		return analysis;
	}

	public String deleteLine(String fen, String move) {

		String rval = "";

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
		return wrapResponse(new SimpleResponseWrapper(rval));

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

	private String wrapResponse(Object response) {
		String jsonWrapper = "";
		try {
			jsonWrapper = mapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		LOGGER.debug("Response wrapper: " + jsonWrapper);
		return jsonWrapper;
	}

	public void stop() {
		stockfishService.stop();
	}

	// methods for command controller

	public void dropAll() {
		analysisRepository.deleteAll();
	}

	public String updateDepth(String fen, int depth, boolean forceUpdate) {
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
			return wrapResponse(new SimpleResponseWrapper("Update Stopped"));
		} else {
			return wrapResponse(new SimpleResponseWrapper("Update completed"));
		}
	}

	private void updatePositionDepth(AnalysisDo position, int depth, boolean forceUpdate) {

		if (!stopTask) {
			if (position.getDepth() < depth || forceUpdate) {
				EngineEvaluation engineEvaluation = stockfishService.getEngineEvaluation(position.getFen(), depth);
				position.setEngineEvaluation(engineEvaluation);
				analysisRepository.save(position);
				updates++;
			}
			for (MoveEvaluationDo move : position.getMoves()) {
				updatePositionDepth(findAnalysisInDb(move.getNextShortFen()), depth, forceUpdate);
			}
		}

	}

	public String setComment(String fen, String comment) {

		AnalysisDo analysis = findAnalysisInDb(FenHelper.getShortFen(fen));
		analysis.setComment(comment);
		analysisRepository.save(analysis);
		return "Comment set!";

	}

	public String fillDatabaseFromPGN(int openingDepth, int analysisDepth) throws Exception {

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

					LOGGER.info("Game #" + (games.indexOf(game) + 1) + ", opening: " + game.getEco());
					game.loadMoveText();
					MoveList moves = game.getHalfMoves();
					Board board = new Board();
					if (moves.size() < openingDepth) {
						openingDepth = moves.size();
					}

					// searching for unanalized moves in the opening
					for (int i = 0; i < openingDepth; i++) {
						Move move = moves.get(i);
						String previousFen = board.getFen();
						String uciMove = (move.getFrom().name() + move.getTo().name()).toLowerCase();
						board.doMove(move);
						String nextFen = board.getFen();
						if (!stopTask) {
							performAnalysis(previousFen, uciMove, nextFen, analysisDepth, true);
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
			return wrapResponse(new SimpleResponseWrapper("Import Stopped"));
		} else {
			return wrapResponse(new SimpleResponseWrapper("Import completed"));
		}
	}

	public void stopTask() {

		LOGGER.info("Stopping task");
		this.stopTask = true;
		stockfishService.cancel();
	}

	public void updateDrawing(String fen, String type, String path, String color) {

		AnalysisDo analysis = findAnalysisInDb(FenHelper.getShortFen(fen));
		analysis.updateDrawing(type, path, color);
		analysisRepository.save(analysis);
	}

	public void shutdown() {

		System.exit(0);

	}

}
