package app.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class MoveEvaluationDo {
	
	@Id
	@GeneratedValue
	long id;
	
	@Column
	private String move;
	
	@Column
	private int evaluation;
	
	@Column
	private int centipawnLoss;
	
	@Column
	private int depth;
	
	public MoveEvaluationDo() {
		
	}

	public MoveEvaluationDo(String move, int evaluation, int centipawnLoss, int depth) {
		super();
		this.move = move;
		this.evaluation = evaluation;
		this.centipawnLoss = centipawnLoss;
		this.depth = depth;
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

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public String toString() {
		return "MoveEvaluation [id=" + id + ", move=" + move + ", evaluation=" + evaluation + ", centipawnLoss="
				+ centipawnLoss + ", depth=" + depth + "]";
	}
	
}
