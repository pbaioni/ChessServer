package app.main.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BoardService.class);

	public String getOnlyPawnsFEN(String fen) {
		LOGGER.info("cleaninf fen: " + fen);
		String[] splitFen = fen.split("/");
		String separator = "/";
		String cleanRows = "";
		for (int i = 0; i < 8; i++) {
			String fenRowToProcess;
			if (i == 7) {
				fenRowToProcess = replaceNumbers(splitFen[i].split(" ")[0]);
				separator = "";
			} else {
				fenRowToProcess = replaceNumbers(splitFen[i]);
			}

			LOGGER.info("fen raw row: " + fenRowToProcess);
			String cleanFenRow = "";
			for (int j = 0; j < fenRowToProcess.length(); j++) {
				if (!fenRowToProcess.substring(j, j + 1).equals("p")
						&& !fenRowToProcess.substring(j, j + 1).equals("P")) {
					cleanFenRow += " ";
				} else {
					cleanFenRow += fenRowToProcess.substring(j, j + 1);
				}
			}
			cleanFenRow = replaceBlanks(cleanFenRow);
			cleanRows += cleanFenRow + separator;
		}
		return "{\"fenOnlyPawns\": \"" + cleanRows + " w KQkq - 0 1\"}";
	}

	private String replaceNumbers(String fenRowWithNumbers) {

		fenRowWithNumbers = fenRowWithNumbers.replace("1", " ");
		fenRowWithNumbers = fenRowWithNumbers.replace("2", "  ");
		fenRowWithNumbers = fenRowWithNumbers.replace("3", "   ");
		fenRowWithNumbers = fenRowWithNumbers.replace("4", "    ");
		fenRowWithNumbers = fenRowWithNumbers.replace("5", "     ");
		fenRowWithNumbers = fenRowWithNumbers.replace("6", "      ");
		fenRowWithNumbers = fenRowWithNumbers.replace("7", "       ");
		fenRowWithNumbers = fenRowWithNumbers.replace("8", "        ");

		return fenRowWithNumbers;
	}

	private String replaceBlanks(String fenRowWithBlanks) {

		fenRowWithBlanks = fenRowWithBlanks.replace("        ", "8");
		fenRowWithBlanks = fenRowWithBlanks.replace("       ", "7");
		fenRowWithBlanks = fenRowWithBlanks.replace("      ", "6");
		fenRowWithBlanks = fenRowWithBlanks.replace("     ", "5");
		fenRowWithBlanks = fenRowWithBlanks.replace("    ", "4");
		fenRowWithBlanks = fenRowWithBlanks.replace("   ", "3");
		fenRowWithBlanks = fenRowWithBlanks.replace("  ", "2");
		fenRowWithBlanks = fenRowWithBlanks.replace(" ", "1");

		return fenRowWithBlanks;
	}

}
