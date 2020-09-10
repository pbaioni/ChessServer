package app.stockfish;

import java.io.IOException;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import app.stockfish.engine.Stockfish;
import app.stockfish.engine.enums.Option;
import app.stockfish.engine.enums.Query;
import app.stockfish.engine.enums.Variant;
import app.stockfish.exceptions.StockfishInitException;

public class StockfishClient {
	private ExecutorService executor, callback;
	private Queue<Stockfish> availableEngines;
	private Queue<Stockfish> busyEngines;

	public StockfishClient(String path, int instances, Variant variant, Boolean ownBook, Set<Option> options)
			throws StockfishInitException {
		executor = Executors.newFixedThreadPool(instances);
		callback = Executors.newSingleThreadExecutor();
		availableEngines = new ArrayBlockingQueue<Stockfish>(instances);
		busyEngines = new ArrayBlockingQueue<Stockfish>(instances);

		for (int i = 0; i < instances; i++) {
			availableEngines.add(new Stockfish(path, variant, ownBook, options.toArray(new Option[options.size()])));
		}
	}

	public void submit(Query query) {
		submit(query, null);
	}

	public void submit(Query query, Consumer<String> result) {
		executor.submit(() -> {
			Stockfish engine = availableEngines.remove();
			busyEngines.add(engine);
			String output;

			switch (query.getType()) {
			case EngineEvaluation:
				output = engine.getEngineEvaluation(query);
				break;
			case Make_Move:
				output = engine.makeMove(query);
				break;
			case Legal_Moves:
				output = engine.getLegalMoves(query);
				break;
			default:
				output = null;
				break;
			}

			callback.submit(() -> result.accept(output));
			busyEngines.remove(engine);
			availableEngines.add(engine);
		});
	}
	
	public void stop() {
		int count = 1;
		for(Stockfish s : availableEngines) {
			try {
				System.out.println("Stopping Stockfish engine #" + count);
				s.close();
				count++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		executor.shutdownNow();
		callback.shutdownNow();
		
	}
	
	public void cancel() {

		for(Stockfish s : busyEngines) {
			try {
				s.cancel();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}

	public static class Builder {
		private Set<Option> options = new HashSet<>();
		private Variant variant = Variant.DEFAULT;
		private String path = null;
		private int instances = 1;
		private boolean ownBook = true;

		public final Builder setInstances(int num) {
			instances = num;
			return this;
		}

		public final Builder setVariant(Variant v) {
			variant = v;
			return this;
		}

		public final Builder setOption(Option o, String value) {
			options.add(o.setValue(value));
			return this;
		}

		public final Builder setPath(String path) {
			this.path = path;
			return this;
		}

		public final StockfishClient build() throws StockfishInitException {
			return new StockfishClient(path, instances, variant, ownBook, options);
		}
	}
}
