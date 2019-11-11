package app.stockfish.engine;

import java.io.IOException;
import java.util.List;

import app.main.service.helper.FenHelper;
import app.stockfish.engine.enums.Option;
import app.stockfish.engine.enums.Query;
import app.stockfish.engine.enums.Variant;
import app.stockfish.exceptions.StockfishInitException;

public class Stockfish extends UCIEngine {
	public Stockfish(String path, Variant variant, Option... options) throws StockfishInitException {
		super(path, variant, options);
	}

	public String makeMove(Query query) {
		waitForReady();
		sendCommand("position fen " + query.getFen() + " moves " + query.getMove());
		return getFen();
	}

	public String getEngineEvaluation(Query query) {

		String evaluation = "";
		String bestMove = "";
		String separator = " ";
		waitForReady();
		sendCommand("position fen " + query.getFen());

		StringBuilder command = new StringBuilder("go ");

		if (query.getDepth() >= 0)
			command.append("depth ").append(query.getDepth()).append(" ");

		if (query.getMovetime() >= 0)
			command.append("movetime ").append(query.getMovetime());

		waitForReady();
		sendCommand(command.toString());
		List<String> response = readResponse("bestmove");

		evaluation = response.get(response.size() - 2).split("score cp")[1].trim().split(" ")[0];
		bestMove = response.get(response.size() - 1).substring(9).split("\\s+")[0];

		String absoluteEvaluation = calculateAbsoluteEvaluation(FenHelper.getTurn(query.getFen()), evaluation);
		String rval = (absoluteEvaluation + separator + bestMove).trim();
		return rval;
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

	private String getFen() {
		waitForReady();
		sendCommand("d");

		return readLine("Fen: ").substring(5);
	}

	private String calculateAbsoluteEvaluation(String turn, String cpEval) {
		
		int intEval = Integer.parseInt(cpEval);

		if (turn.equals("b")) {
			intEval = intEval*(-1);
		}

		return Integer.toString(intEval);
	}
}
