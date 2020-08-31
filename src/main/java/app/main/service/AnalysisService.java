package app.main.service;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.main.service.helper.FenHelper;
import app.persistence.model.AnalysisDo;
import app.persistence.model.MoveEvaluation;
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
	}

	public String welcome() {
		String ready = "Board service is ready";
		LOGGER.info("Welcome message: " + ready);
		String jsonWrapper = "";
		try {
			jsonWrapper = mapper.writeValueAsString(new SimpleResponseWrapper(ready));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		LOGGER.info("Welcome wrapper: " + jsonWrapper);
		return jsonWrapper;
	}

	public String getOnlyPawnsFen(String fen) {
		LOGGER.info("Cleaning pieces from fen: " + fen);
		String jsonWrapper = "";
		try {
			jsonWrapper = mapper.writeValueAsString(new SimpleResponseWrapper(FenHelper.cleanPiecesFromFen(fen)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		LOGGER.info("Only pawns fen wrapper: " + jsonWrapper);
		return jsonWrapper;
	}

	public String getAnalysis(String previousFen, String move, String fen) {

		Optional<AnalysisDo> databaseAnalysis;
		AnalysisDo analysis;
		LOGGER.info("Searching for analysis in DB");
		databaseAnalysis = analysisRepository.findById(FenHelper.getShortFen(fen));
		if (databaseAnalysis.isPresent()) {
			analysis = databaseAnalysis.get();
			LOGGER.info("Analysis from DB: " + analysis.toString());
		} else {
			LOGGER.info("No result from database, computing analysis for fen: " + fen);
			EngineEvaluation engineEvaluation = stockfishService.getEngineEvaluation(fen);
			analysis = new AnalysisDo(fen);
			analysis.setEngineEvaluation(engineEvaluation);
			Optional<AnalysisDo> previousDatabaseAnalysis;
			previousDatabaseAnalysis = analysisRepository.findById(FenHelper.getShortFen(previousFen));
			if (previousDatabaseAnalysis.isPresent()) {
				previousDatabaseAnalysis.get().mergeMove(move, analysis);
				analysisRepository.save(analysis);
				LOGGER.info("New analysis merged to previous position and saved: " + analysis.toString());
			}
			
		}

		// returning analysis as json string
		String jsonWrapper = "";
		try {
			jsonWrapper = mapper.writeValueAsString(analysis);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		LOGGER.info("Analysis wrapper: " + jsonWrapper);
		return jsonWrapper;

	}

	public void stop() {
		stockfishService.stop();
	}

}
