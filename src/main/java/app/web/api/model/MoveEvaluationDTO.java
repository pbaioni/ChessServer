package app.web.api.model;

public class MoveEvaluationDTO {
	
	private String move;
	
	private int evaluation;
	
	private int centipawnLoss;

	public MoveEvaluationDTO(String move, int evaluation) {
		super();
		this.move = move;
		this.evaluation = evaluation;
	}

	public String getMove() {
		return move;
	}

	public void setMove(String move) {
		this.move = move;
	}

	public int getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}

	public int getCentipawnLoss() {
		return centipawnLoss;
	}

	public void setCentipawnLoss(int centipawnLoss) {
		this.centipawnLoss = centipawnLoss;
	}

	@Override
	public String toString() {
		return "MoveEvaluationDTO [move=" + move + ", evaluation=" + evaluation + "]";
	}

}
