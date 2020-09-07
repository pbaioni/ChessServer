package app.main.service;

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
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;

import app.main.service.helper.FenHelper;
import app.persistence.model.AnalysisDo;
import app.persistence.model.MoveEvaluationDo;
import app.persistence.repo.AnalysisRepository;
import app.stockfish.engine.EngineEvaluation;
import app.stockfish.service.StockfishService;
import app.web.api.model.AnalysisDTO;
import app.web.api.model.MoveEvaluationDTO;
import app.web.api.model.SimpleResponseWrapper;

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

	public void init() {
		stockfishService.init();

		// empty database, setting start position
		if (analysisRepository.count() == 0L) {
			AnalysisDo analysis = new AnalysisDo(START_FEN);
			analysis.setBestMove("e2e4");
			analysis.setEvaluation(20);
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

	public String performAnalysis(String currentFen, String move, String nextFen, int depth) {

		AnalysisDo nextPosition = findAnalysisInDb(FenHelper.getShortFen(nextFen));

		if (!Objects.isNull(nextPosition)) {
			// position from DB
			LOGGER.debug("Analysis fetched: " + nextPosition.toString());
		} else {
			// No result from DB, performing analysis
			nextPosition = new AnalysisDo(nextFen);
			LOGGER.info("No result from database, performing analysis for fen: " + nextFen);
			EngineEvaluation engineEvaluation = stockfishService.getEngineEvaluation(nextFen, depth);
			nextPosition.setEngineEvaluation(engineEvaluation);
			LOGGER.debug("NextCalculated: " + nextPosition.toString());
			if (!Objects.isNull(currentFen) && !Objects.isNull(move)) {
				AnalysisDo currentPosition = findAnalysisInDb(FenHelper.getShortFen(currentFen));
				MoveEvaluationDo newMove = new MoveEvaluationDo(move, nextFen);
				LOGGER.debug("CurrentFetched: " + currentPosition.toString());
				currentPosition.addMove(newMove);
				LOGGER.debug("CurrentEnd: " + currentPosition.toString());
				analysisRepository.save(currentPosition);
				analysisRepository.save(nextPosition);
				LOGGER.info("New analysis linked to previous position and saved: " + nextPosition.toString());
			}
		}

		AnalysisDTO analysis = mapToDto(nextPosition);

		return wrapResponse(analysis);

	}

	private AnalysisDTO mapToDto(AnalysisDo Do) {

		AnalysisDTO analysis = new AnalysisDTO(Do.getFen(), Do.getTurn(), Do.getDepth(), Do.getComment());
		for (MoveEvaluationDo move : Do.getMoves()) {
			analysis.addMove(move.getMove(), findAnalysisInDb(move.getNextShortFen()).getEvaluation());
		}

		boolean bestMoveMatch = false;
		for (MoveEvaluationDTO item : analysis.getMoves()) {
			if (item.getMove().equals(Do.getBestMove())) {
				bestMoveMatch = true;
			}
		}

		// case of best move only as a result of stockfish analysis but never browsed
		if (!bestMoveMatch) {
			analysis.addMove(Do.getBestMove(), Do.getEvaluation());
		}
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

	public void updateDepth(String fen, int depth, boolean forceUpdate) {
		updates = 0;
		Instant start = Instant.now();
		long entitiesToUpdate = analysisRepository.count();
		LOGGER.info("Starting analysis update to depth " + depth + " for " + entitiesToUpdate + " positions");

		AnalysisDo startPosition = findAnalysisInDb(FenHelper.getShortFen(fen));
		updatePositionDepth(startPosition, depth, forceUpdate);

		Instant finish = Instant.now();
		long seconds = Duration.between(start, finish).getSeconds();
		if (updates != 0) {
			LOGGER.info(updates + " positions updated in " + seconds + " seconds [average: " + seconds / updates
					+ " seconds per position");
		}else {
			LOGGER.info("No positions to update");
		}
	}

	private void updatePositionDepth(AnalysisDo position, int depth, boolean forceUpdate) {

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
	
	public String setComment(String fen, String comment) {
		
		AnalysisDo analysis = findAnalysisInDb(FenHelper.getShortFen(fen));
		analysis.setComment(comment);
		analysisRepository.save(analysis);
		return "Comment set!";
		
	}
	
	public void fillDatabaseFromPGN() throws Exception {
		
		//import values
		int openingDepth = 12; //gives a 7 depth
		int analysisDepth = 24;
		
		//load pgn games from file
	    PgnHolder pgn = new PgnHolder("./src/main/resources/pgn/all.pgn");
	    pgn.loadPgn();
	    
	    //browse the imported games
	    List<Game> games = pgn.getGame();
	    for (Game game: games) {
	    	LOGGER.info("Game #" + (games.indexOf(game)+1) + ", opening: " + game.getEco());
	        game.loadMoveText();
	        MoveList moves = game.getHalfMoves();
	        Board board = new Board();
	        
	        //searching for unanalized moves in the opening
	        for (int i = 0; i<=openingDepth; i++) {
	        	Move move = moves.get(i);
	        	String previousFen = board.getFen();
	        	String uciMove = (move.getFrom().name() + move.getTo().name()).toLowerCase();
	            board.doMove(move);
	            String nextFen = board.getFen();

	            performAnalysis(previousFen, uciMove, nextFen, analysisDepth);
	        }

	    }
	}

}
