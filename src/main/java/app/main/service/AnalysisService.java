package app.main.service;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	public void init() {
		stockfishService.init();

		// empty database, setting start position
		if (analysisRepository.count() == 0L) {
			String startFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
			AnalysisDo analysis = new AnalysisDo(startFen);
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
	
			nextPosition.setBestMove(engineEvaluation.getBestMove());
			nextPosition.setEvaluation(engineEvaluation.getEvaluation());
			nextPosition.setDepth(depth);
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

		AnalysisDTO analysis = new AnalysisDTO(Do.getFen(), Do.getTurn(), Do.getDepth());
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

	public void updateDepth(String string) {

	}

}
