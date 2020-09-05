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
			MoveEvaluationDo firstEval = new MoveEvaluationDo("e2e4", null, 20, 0, 0);
			analysis.mergeMoveEvaluation(firstEval);
			analysisRepository.save(analysis);
			LOGGER.info("Start position analysis saved");
		}
	}

	public String welcome() {
		return wrapResponse(new SimpleResponseWrapper("Board service is ready"));
	}

	public String getOnlyPawnsFen(String fen) {
		LOGGER.debug("Cleaning pieces from fen: " + fen);
		return wrapResponse(new SimpleResponseWrapper(FenHelper.cleanPiecesFromFen(fen)));
	}

	public String performAnalysis(String previousFen, String move, String fen) {

		AnalysisDo analysis = findAnalysisInDb(FenHelper.getShortFen(fen));

		// no result in database
		if (Objects.isNull(analysis)) {
			LOGGER.info("No result from database, performing analysis for fen: " + fen);
			EngineEvaluation engineEvaluation = stockfishService.getEngineEvaluation(fen);
			analysis = new AnalysisDo(fen);
			MoveEvaluationDo eval = new MoveEvaluationDo(engineEvaluation.getBestMove(), null,
					engineEvaluation.getEvaluation(), 0, engineEvaluation.getDepth());
			analysis.mergeMoveEvaluation(eval);
			if (!Objects.isNull(previousFen) && !Objects.isNull(move)) {
				AnalysisDo previous = findAnalysisInDb(FenHelper.getShortFen(previousFen));
				MoveEvaluationDo evalUpdate = new MoveEvaluationDo(move, fen, engineEvaluation.getEvaluation(), 0,
						engineEvaluation.getDepth());
				previous.mergeMoveEvaluation(evalUpdate);
				analysisRepository.save(previous);
				analysisRepository.save(analysis);
				LOGGER.info("New analysis merged to previous position and saved: " + analysis.toString());
			}
		}

		return wrapResponse(analysis);

	}

	public String deleteLine(String fen, String move) {
		
		LOGGER.info("Deleting move " + move + " for fen " + fen);
		String rval = "";
		
		// removing move evaluation from variant base
		AnalysisDo variantBase = findAnalysisInDb(FenHelper.getShortFen(fen));
		MoveEvaluationDo moveToPrune = variantBase.getEvaluationByMove(move);
		if (!Objects.isNull(moveToPrune)) {
			variantBase.pruneMoveEvaluation(moveToPrune.getMove());
			analysisRepository.save(variantBase);
			
			//removing all the lines linked to this move
			deleteLine(findAnalysisInDb(moveToPrune.getNextShortFen()));
			
			rval = "Line deleted";
		}else {
			rval = "Line not found";
		}

		// returning analysis as json string
		return wrapResponse(new SimpleResponseWrapper(rval));

	}

	private void deleteLine(AnalysisDo analysis) {
		if (!Objects.isNull(analysis)) {
			analysisRepository.delete(analysis);
			LOGGER.info("Analysis deleted for fen: " + analysis.getFen());
			for (MoveEvaluationDo moveEval : analysis.getMoveEvaluations()) {
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
