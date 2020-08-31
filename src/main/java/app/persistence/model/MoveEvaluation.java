package app.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "moves")
public class MoveEvaluation {
	
	@Id
	@Column
	private String move;
	
	@Column
	private String newFen;
	
	@Column
	private int evaluation;
	
	@Column
	private int centipawnLoss;
	
	@Column
	private int depth;

	public MoveEvaluation(String move, String newFen, int evaluation, int centipawnLoss, int depth) {
		super();
		this.move = move;
		this.newFen = newFen;
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
	
	
	public String getNewFen() {
		return newFen;
	}

	public void setNewFen(String newFen) {
		this.newFen = newFen;
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

}
