package app.stockfish.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

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
			LOGGER.info("Sending command: " + command);
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
				LOGGER.info("Read line: " + line);
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
					LOGGER.info("Read response: " + line);
					break;
				}
			}

			return lines;
		} catch (IOException e) {
			throw new StockfishEngineException(e);
		}
	}

	private void passOption(Option option) {
		sendCommand(option.toString());
	}

	private String getPath(Variant variant, String override) {
		StringBuilder path = new StringBuilder(
				override == null ? "assets/engines/stockfish_10_x64" : override + "stockfish-10-64");

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
