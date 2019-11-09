package app.stockfish.engine;

public class EngineEvaluation {
	
	private static final String SEPARATOR = " ";
	
	private String evaluation;
	
	private String bestMove;
	
	public EngineEvaluation() {
		
	}

	public EngineEvaluation(String stringEvaluation) {
		String[] splitEval = stringEvaluation.split(SEPARATOR);
		this.evaluation = splitEval[0].trim();
		this.bestMove = splitEval[1].trim();
	}

	public String getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(String evaluation) {
		this.evaluation = evaluation;
	}

	public String getBestMove() {
		return bestMove;
	}

	public void setBestMove(String bestMove) {
		this.bestMove = bestMove;
	}

}
