package app.stockfish.service;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.stockfish.StockfishClient;
import app.stockfish.engine.EngineEvaluation;
import app.stockfish.engine.enums.Option;
import app.stockfish.engine.enums.Query;
import app.stockfish.engine.enums.QueryType;
import app.stockfish.engine.enums.Variant;
import app.stockfish.exceptions.StockfishInitException;
import app.stockfish.properties.StockfishProperties;

@Service
public class StockfishService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StockfishService.class);

	@Autowired
	StockfishProperties stockfishProperties;
	
	private StockfishClient client;
	
	private int defaultDepth;
	
	public StockfishService() {

	}

	
	public void init() {
        try {
			client = new StockfishClient.Builder()
					.setPath(stockfishProperties.getEnginePath())
			        .setInstances(Integer.parseInt(stockfishProperties.getInstances()))
			        .setOption(Option.Threads, stockfishProperties.getThreads())
			        .setOption(Option.Skill_Level, stockfishProperties.getSkills())
			        .setOption(Option.Hash, stockfishProperties.getHash())
			        .setOption(Option.MultiPV, stockfishProperties.getMultipv())
			        .setOption(Option.OwnBook, stockfishProperties.getOwnbook())
			        .setVariant(Variant.DEFAULT)
			        .build();
			this.defaultDepth = Integer.parseInt(stockfishProperties.getDepth());
		} catch (StockfishInitException e) {
			LOGGER.error("Problem while creating stockfish client", e);
		}
	}


	public EngineEvaluation getEngineEvaluation(String fen, int targetDepth) {
		if(Objects.isNull(targetDepth)) {
			targetDepth = defaultDepth;
		}
		String stringEngineEvaluation = executeQuery(fen, QueryType.EngineEvaluation, targetDepth);
		EngineEvaluation eval = new EngineEvaluation(stringEngineEvaluation, targetDepth);
		LOGGER.info(eval.toString());
		return eval;
		
	}
	
	private String executeQuery(String fen, QueryType type, int targetDepth) {
		
		AtomicReference<String> atomicRval = new AtomicReference<>();
		Awaitility.setDefaultPollInterval(1000, TimeUnit.MILLISECONDS);
		Awaitility.setDefaultPollDelay(Duration.ZERO);
		Awaitility.setDefaultTimeout(Duration.ofSeconds(300L));
        client.submit(new Query.Builder(type)
                .setFen(fen)
                .setDepth(targetDepth)
                .build(),
                result -> atomicRval.set(result));
        Awaitility.await().atMost(Duration.ofSeconds(300L)).until(new Callable<Boolean>() {
			
			@Override
			public Boolean call() throws Exception {
				return !Objects.isNull(atomicRval.get());
			}
		});;
		
		return atomicRval.get();
	}


	public void stop() {
		client.stop();
		
	}
}
