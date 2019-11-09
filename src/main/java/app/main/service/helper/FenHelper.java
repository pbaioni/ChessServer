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
			String endOfRow = ROW_SEPARATOR;
			if (i == 7) {
				fenRowWithoutNumbers = replaceNumbers(splitFen[i].split(FEN_SEPARATOR)[0]);
				endOfRow = "";
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
			rowsWithoutPieces += fenRowWithoutPieces + endOfRow;
		}
		
		return (rowsWithoutPieces + getAdditionalInfos(fenWithPieces)).trim();
	}
	

	public static String getShortFen(String fen) {
		
		String[] splitFen = fen.split(FEN_SEPARATOR);
		String shortFen = "";
		for (int i = 0; i < splitFen.length-2; i++) {
			shortFen += splitFen[i] + FEN_SEPARATOR;
		}
		
		return shortFen.trim();
	}
	
	public static String getTurn(String fen) {
		
		return fen.split(FEN_SEPARATOR)[1];
		
	}

	
	private static String replaceNumbers(String fenRowWithNumbers) {

		String fenRowWithoutNumbers = fenRowWithNumbers;
		fenRowWithoutNumbers = fenRowWithoutNumbers.replace("1", " ");
		fenRowWithoutNumbers = fenRowWithoutNumbers.replace("2", "  ");
		fenRowWithoutNumbers = fenRowWithoutNumbers.replace("3", "   ");
		fenRowWithoutNumbers = fenRowWithoutNumbers.replace("4", "    ");
		fenRowWithoutNumbers = fenRowWithoutNumbers.replace("5", "     ");
		fenRowWithoutNumbers = fenRowWithoutNumbers.replace("6", "      ");
		fenRowWithoutNumbers = fenRowWithoutNumbers.replace("7", "       ");
		fenRowWithoutNumbers = fenRowWithoutNumbers.replace("8", "        ");

		return fenRowWithoutNumbers;
	}

	private static String replaceBlanks(String fenRowWithBlanks) {

		String fenRowWithoutBlanks = fenRowWithBlanks;
		fenRowWithoutBlanks = fenRowWithoutBlanks.replace("        ", "8");
		fenRowWithoutBlanks = fenRowWithoutBlanks.replace("       ", "7");
		fenRowWithoutBlanks = fenRowWithoutBlanks.replace("      ", "6");
		fenRowWithoutBlanks = fenRowWithoutBlanks.replace("     ", "5");
		fenRowWithoutBlanks = fenRowWithoutBlanks.replace("    ", "4");
		fenRowWithoutBlanks = fenRowWithoutBlanks.replace("   ", "3");
		fenRowWithoutBlanks = fenRowWithoutBlanks.replace("  ", "2");
		fenRowWithoutBlanks = fenRowWithoutBlanks.replace(" ", "1");

		return fenRowWithoutBlanks;
	}
	
	private static String getAdditionalInfos(String fen) {
		
		String[] splitFen = fen.split(" ");
		String additionalInfos = " ";
		for (int i = 1; i < splitFen.length; i++) {
			additionalInfos += splitFen[i] + " ";
		}
		
		return additionalInfos;
	}
	
}
