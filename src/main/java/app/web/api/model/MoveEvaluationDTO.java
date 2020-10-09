package app.web.api.model;

public class MoveEvaluationDTO {
	
	private String move;
	
	private String evaluation;
	
	private int centipawnLoss;

	public MoveEvaluationDTO(String move, String evaluation) {
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

	public String getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(String evaluation) {
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
