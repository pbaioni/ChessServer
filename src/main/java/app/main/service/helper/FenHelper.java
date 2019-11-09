package app.main.service.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FenHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(FenHelper.class);
	
	private static final String ROW_SEPARATOR = "/";
	
	private static final String FEN_SEPARATOR = " ";
	
	public static String cleanPiecesFromFen(String fenWithPieces) {

		String[] splitFen = fenWithPieces.split(ROW_SEPARATOR);
		String rowsWithoutPieces = "";
		for (int i = 0; i < 8; i++) {
			String fenRowWithoutNumbers;
			if (i == 7) {
				fenRowWithoutNumbers = replaceNumbers(splitFen[i].split(" ")[0]);
			} else {
				fenRowWithoutNumbers = replaceNumbers(splitFen[i]);
			}
			String fenRowWithoutPieces = "";
			for (int j = 0; j < fenRowWithoutNumbers.length(); j++) {
				if (!fenRowWithoutNumbers.substring(j, j + 1).equals("p")
						&& !fenRowWithoutNumbers.substring(j, j + 1).equals("P")) {
					fenRowWithoutPieces += " ";
				} else {
					fenRowWithoutPieces += fenRowWithoutNumbers.substring(j, j + 1);
				}
			}
			fenRowWithoutPieces = replaceBlanks(fenRowWithoutPieces);
			rowsWithoutPieces += fenRowWithoutPieces + FEN_SEPARATOR;
		}
		
		return (rowsWithoutPieces + getAdditionalInfos(fenWithPieces)).trim();
	}

	private static String replaceNumbers(String fenRowWithNumbers) {

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

	private static String replaceBlanks(String fenRowWithBlanks) {

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
	
	private static String getAdditionalInfos(String fen) {
		String[] splitFen = fen.split(" ");
		String additionalInfos = " ";
		for (int i = 1; i < splitFen.length; i++) {
			additionalInfos += splitFen[i] + " ";
		}
		
		return additionalInfos;
	}

	public static String getShortFen(String fen) {
		
		String[] splitFen = fen.split(FEN_SEPARATOR);
		String shortFen = "";
		for (int i = 0; i < splitFen.length-2; i++) {
			shortFen += splitFen[i] + FEN_SEPARATOR;
		}
		
		return shortFen.trim();
	}
	
}
