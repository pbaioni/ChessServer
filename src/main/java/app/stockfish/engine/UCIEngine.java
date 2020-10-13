package app.stockfish.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.stockfish.engine.enums.Option;
import app.stockfish.engine.enums.Variant;
import app.stockfish.exceptions.StockfishEngineException;
import app.stockfish.exceptions.StockfishInitException;
import app.stockfish.service.StockfishService;

abstract class UCIEngine {

	private static final Logger LOGGER = LoggerFactory.getLogger(UCIEngine.class);
	final BufferedReader input;
	final BufferedWriter output;
	final Process process;

	UCIEngine(String path, Variant variant, Boolean ownBook, Option... options) throws StockfishInitException {

		try {
			process = Runtime.getRuntime().exec(getPath(variant, path));
			input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

			for (Option option : options)
				passOption(option);
		} catch (IOException e) {
			throw new StockfishInitException("Unable to start and bind Stockfish process @" + getPath(variant, path),
					e);
		}
	}

	void waitForReady() {
		sendCommand("isready");
		readResponse("readyok");
	}

	void sendCommand(String command) {
		try {
			LOGGER.debug("Sending command: " + command);
			output.write(command + "\n");
			output.flush();
		} catch (IOException e) {
			throw new StockfishEngineException(e);
		}
	}

	String readLine(String expected) {
		try {
			String line;

			while ((line = input.readLine()) != null) {
				LOGGER.debug("Read line: " + line);
				if (line.startsWith(expected))
					return line;
			}

			return null;
		} catch (IOException e) {
			throw new StockfishEngineException(e);
		}
	}

	List<String> readResponse(String expected) {
		try {
			List<String> lines = new ArrayList<>();
			String line;

			while ((line = input.readLine()) != null) {
				lines.add(line);
				if (line.startsWith(expected)) {
					break;
				}
			}

			return lines;
		} catch (IOException e) {
			throw new StockfishEngineException(e);
		}
	}

	public String readEvaluation(String turn) {
		try {

			String eval = "";
			String bestmove = "";
			String mate = "";

			String line;
			while ((line = input.readLine()) != null) {

				eval = extractInfo(line, "score cp", eval);

				mate = extractInfo(line, "score mate", mate);

				bestmove = extractInfo(line, "bestmove", bestmove);

				if (!bestmove.equals("")) {
					break;
				}
			}

			//managing mate evaluations
			//stockfish outputs examples: "mate 1" for winning in 1 move , "mate -2" for losing in 2 moves
			if (!mate.equals("")) {
				if (mate.contains("-")) {
					eval = "-#" + mate.replace("-", "");
				} else {
					eval = "+#" + mate;
				}
				if(mate.equals("0")) {
					//when a player is mated, stockfish evaluation is "mate 0" 
					//here we add a "-" to keep it clear that it is a lost position
					eval = "-#0";
					bestmove = "-";
				}
			} else {
				eval = calculateAbsoluteEvaluation(turn, eval);
			}

			return eval + " " + bestmove;

		} catch (IOException e) {
			throw new StockfishEngineException(e);
		}
	}

	private String extractInfo(String line, String infoName, String currentInfo) {

		if (line.contains(infoName)) {
			currentInfo = line.split(infoName)[1].trim().split(" ")[0];
		}

		return currentInfo;
	}

	private String calculateAbsoluteEvaluation(String turn, String cpEval) {

		int intEval = Integer.parseInt(cpEval);

		//stockfish calculates evaluation from player's point of view(+ for player's good positions, - for bad ones)
		//if we want black advantage as a negative value, we must correct the stockfish output
		if (turn.equals("b")) {
			intEval = intEval * (-1);
		}

		return Integer.toString(intEval);
	}

	private void passOption(Option option) {
		sendCommand(option.toString());
	}

	private String getPath(Variant variant, String override) {
		StringBuilder path = new StringBuilder(
				override == null ? "assets/engines/stockfish_10_x64" : override + "stockfish");

		if (System.getProperty("os.name").toLowerCase().contains("win"))
			switch (variant) {
			case DEFAULT:
				path.append(".exe");
				break;
			case BMI2:
				path.append("_bmi2.exe");
				break;
			case POPCNT:
				path.append("_popcnt.exe");
				break;
			default:
				throw new StockfishEngineException("Illegal variant provided.");
			}
		else
			switch (variant) {
			case DEFAULT:
				break;
			case BMI2:
				path.append("_bmi2");
				break;
			case MODERN:
				path.append("_modern");
				break;
			default:
				throw new StockfishEngineException("Illegal variant provided.");
			}

		return path.toString();
	}
}
