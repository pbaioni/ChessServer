package app.stockfish.engine;

public class EngineEvaluation {
	
	private static final String SEPARATOR = " ";
	
	private int evaluation;
	
	private String bestMove;
	
	private int depth;
	
	public EngineEvaluation() {
		
	}

	public EngineEvaluation(String stringEvaluation, int depth) {
		String[] splitEval = stringEvaluation.split(SEPARATOR);
		this.evaluation = Integer.parseInt(splitEval[0].trim());
		this.bestMove = splitEval[1].trim();
		this.depth = depth;
	}

	public int getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}

	public String getBestMove() {
		return bestMove;
	}

	public void setBestMove(String bestMove) {
		this.bestMove = bestMove;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public String toString() {
		return "EngineEvaluation [evaluation=" + evaluation + ", bestMove=" + bestMove + ", depth=" + depth + "]";
	}	

}
