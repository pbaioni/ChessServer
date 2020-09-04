package app.persistence.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import app.main.service.helper.FenHelper;

@Entity
public class MoveEvaluationDo {

	@Id
	@GeneratedValue
	long id;

	@Column
	private String move;

	@Column
	private String fen;

	@Column
	private int evaluation;

	@Column
	private int centipawnLoss;

	@Column
	private int depth;

	public MoveEvaluationDo() {

	}

	public MoveEvaluationDo(String move, String fen, int evaluation, int centipawnLoss, int depth) {
		super();
		this.move = move;
		if (!Objects.isNull(fen)) {
			this.fen = FenHelper.getShortFen(fen);
		}else {
			this.fen = null;
		}
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

	public String getFen() {
		return fen;
	}

	public void setFen(String fen) {
		this.fen = fen;
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
		return "MoveEvaluationDo [id=" + id + ", move=" + move + ", fen=" + fen + ", evaluation=" + evaluation
				+ ", centipawnLoss=" + centipawnLoss + ", depth=" + depth + "]";
	}

}
