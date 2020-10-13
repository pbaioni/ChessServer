package app.stockfish.engine;

import java.io.IOException;
import java.util.List;

import app.main.service.helper.FenHelper;
import app.stockfish.engine.enums.Option;
import app.stockfish.engine.enums.Query;
import app.stockfish.engine.enums.Variant;
import app.stockfish.exceptions.StockfishInitException;

public class Stockfish extends UCIEngine {
	
	boolean stop = false;
	
	public Stockfish(String path, Variant variant, Boolean ownBook, Option... options) throws StockfishInitException {
		super(path, variant, ownBook, options);
	}

	public String makeMove(Query query) {
		waitForReady();
		sendCommand("position fen " + query.getFen() + " moves " + query.getMove());
		return getFen();
	}

	public String getEngineEvaluation(Query query) {

		waitForReady();
		sendCommand("ucinewgame");
		waitForReady();
		sendCommand("position fen " + query.getFen());

		StringBuilder command = new StringBuilder("go ");

		if (query.getDepth() >= 0)
			command.append("depth ").append(query.getDepth()).append(" ");

		if (query.getMovetime() >= 0)
			command.append("movetime ").append(query.getMovetime());

		waitForReady();
		sendCommand(command.toString());
		
		String evaluation = readEvaluation(FenHelper.getTurn(query.getFen()));

		if(stop) {
			evaluation += " canceled";
			stop = false;
		}

		return evaluation;
	}

	public String getLegalMoves(Query query) {
		waitForReady();
		sendCommand("position fen " + query.getFen());

		waitForReady();
		sendCommand("go perft 1");

		StringBuilder legal = new StringBuilder();
		List<String> response = readResponse("Nodes");

		for (String line : response)
			if (!line.isEmpty() && !line.contains("Nodes") && line.contains(":"))
				legal.append(line.split(":")[0]).append(" ");

		return legal.toString();
	}

	public void close() throws IOException {
		try {
			sendCommand("quit");
		} finally {
			process.destroy();
			input.close();
			output.close();
		}
	}
	
	public void cancel() throws IOException {
		
		stop = true;
		sendCommand("stop");
	}

	private String getFen() {
		waitForReady();
		sendCommand("d");

		return readLine("Fen: ").substring(5);
	}

}
