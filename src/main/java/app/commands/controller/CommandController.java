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
		if (properties.isStart()) {
			runCommands = true;
			launchCommands();
		}

	}

	private void launchCommands() {

		BufferedReader br = null;
		try {

			br = new BufferedReader(new InputStreamReader(System.in));
			LOGGER.info("Implemented inline commands : ");
			LOGGER.info("dropall : erase database");
			LOGGER.info("shutdown : stop server");
			LOGGER.info("q : quit inline commands");

			runCommands = true;
			while (runCommands) {
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
				LOGGER.info("Inline commands stopped!");
				this.destroy();
				break;
			case "dropall":
				analysisService.dropAll();
				LOGGER.info("Database clean");
				analysisService.init();
				break;
			case "shutdown":
				LOGGER.info("Shutting down");
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
