package app.stockfish.service;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import app.stockfish.StockfishClient;
import app.stockfish.engine.EngineEvaluation;
import app.stockfish.engine.enums.Option;
import app.stockfish.engine.enums.Query;
import app.stockfish.engine.enums.QueryType;
import app.stockfish.engine.enums.Variant;
import app.stockfish.exceptions.StockfishInitException;

@Service
public class StockfishService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StockfishService.class);
	
	private StockfishClient client;
	
	private int stockfishThreads = 3;
	
	private int stockfishInstances = 3;
	
	private int depth = 25;
	
	private int skills = 20;
	
	private String enginePath = "src/main/resources/stockfish/";
	
	public StockfishService() {
		init();
	}

	
	public void init() {
        try {
			client = new StockfishClient.Builder()
					.setPath(enginePath)
			        .setInstances(stockfishInstances)
			        .setOption(Option.Threads, stockfishThreads)
			        .setOption(Option.Skill_Level, skills)
			        .setVariant(Variant.DEFAULT)
			        .build();
		} catch (StockfishInitException e) {
			LOGGER.error("Problem while creating stockfish client", e);
		}
	}


	public EngineEvaluation getEngineEvaluation(String fen) {
		String stringEngineEvaluation = executeQuery(fen, QueryType.EngineEvaluation);
		return new EngineEvaluation(stringEngineEvaluation);
		
	}
	
	private String executeQuery(String fen, QueryType type) {
		
		AtomicReference<String> atomicRval = new AtomicReference<>();
		Awaitility.setDefaultPollInterval(1000, TimeUnit.MILLISECONDS);
		Awaitility.setDefaultPollDelay(Duration.ZERO);
		Awaitility.setDefaultTimeout(Duration.ofSeconds(60L));
        client.submit(new Query.Builder(type)
                .setFen(fen)
                .setDepth(depth)
                .build(),
                result -> atomicRval.set(result));
        Awaitility.await().atMost(Duration.ofSeconds(60L)).until(new Callable<Boolean>() {
			
			@Override
			public Boolean call() throws Exception {
				return !Objects.isNull(atomicRval.get());
			}
		});;
		
		return atomicRval.get();
	}
}
