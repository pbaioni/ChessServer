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
		String jsonWrapper = "";
		try {
			jsonWrapper = mapper.writeValueAsString(new SimpleResponseWrapper(ready));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
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
			if (Objects.isNull(previousFen) && Objects.isNull(move)) {
				//start position case, empty database
				analysisRepository.save(analysis);
				LOGGER.info("Start position analysis saved");
			} else {
				Optional<AnalysisDo> previousDatabaseAnalysis;
				previousDatabaseAnalysis = analysisRepository.findById(FenHelper.getShortFen(previousFen));
				if (previousDatabaseAnalysis.isPresent()) {
					AnalysisDo previous = previousDatabaseAnalysis.get();
					previous.mergeMove(move, analysis);
					analysisRepository.save(previous);
					analysisRepository.save(analysis);
					LOGGER.info("New analysis merged to previous position and saved: " + analysis.toString());
				}
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

	public void dropAll() {
		analysisRepository.deleteAll();
	}
	
	public void setFirstEval() {
		Optional<AnalysisDo> databaseAnalysis = analysisRepository.findById(FenHelper.getShortFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));
		AnalysisDo first = databaseAnalysis.get();
		first.setEvaluation(20);
		first.getMoveEvaluations().get(0).setEvaluation(20);
		analysisRepository.save(first);
	}

}
