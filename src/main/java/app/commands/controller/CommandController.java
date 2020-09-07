package app.commands.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import app.commands.properties.CommandProperties;
import app.main.service.AnalysisService;

@Service
public class CommandController implements CommandLineRunner, DisposableBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandController.class);

	@Autowired
	AnalysisService analysisService;

	@Autowired
	CommandProperties properties;

	boolean runCommands;

	public CommandController() {

	}

	@Override
	public void run(String... args) throws Exception {
		LOGGER.info(properties.toString());
		if (properties.isStart()) {
			runCommands = true;
			launchCommands();
		}

	}

	private void launchCommands() {

		BufferedReader br = null;

		try {

			br = new BufferedReader(new InputStreamReader(System.in));
			LOGGER.info("Commands : ");
			LOGGER.info("q : quit");

			runCommands = true;
			while (runCommands) {

				LOGGER.info("Enter command: ");
				String input = br.readLine();

				manageCommands(input);

			}

		} catch (IOException e) {
			LOGGER.error("IO trouble: ", e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LOGGER.error("IO trouble: ", e);
				}
			}
		}
	}

	private void manageCommands(String commandLine) throws Exception {
		Scanner scanner = new Scanner(commandLine);
		scanner.useDelimiter(" ");
		if (scanner.hasNext()) {
			String command = scanner.next();
			List<String> arguments = new ArrayList<String>();
			try {
				while (scanner.hasNext()) {
					arguments.add(scanner.next());
				}
			} catch (NoSuchElementException e) {
				// DO NOTHING
			}

			scanner.close();

			switch (command) {
			case "q":
				LOGGER.info("In line commands stopped!");
				this.destroy();
				break;
			case "move":
				analysisService.performAnalysis("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
						arguments.get(0), "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", 24);
				break;
			case "dropall":
				analysisService.dropAll();
				analysisService.init();
				break;
			case "updateDepth":
				analysisService.updateDepth("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
						Integer.parseInt(arguments.get(0)), Boolean.parseBoolean(arguments.get(1)));
				break;
			case "import":
				analysisService.fillDatabaseFromPGN();
				break;
			default:
				LOGGER.error("Unknown command [" + command + " " + arguments.toString() + "]");
				break;
			}
		}

	}

	@Override
	public void destroy() throws Exception {
		runCommands = false;
	}

}
