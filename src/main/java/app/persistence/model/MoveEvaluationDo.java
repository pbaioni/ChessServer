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
	private String nextShortFen;


	public MoveEvaluationDo() {

	}

	public MoveEvaluationDo(String move, String nextFen) {
		super();
		this.move = move;
		if (!Objects.isNull(nextFen)) {
			this.nextShortFen = FenHelper.getShortFen(nextFen);
		}else {
			this.nextShortFen = null;
		}
	}

	public String getMove() {
		return move;
	}

	public void setMove(String move) {
		this.move = move;
	}

	public String getNextShortFen() {
		return nextShortFen;
	}

	public void setNextShortFen(String nextShortFen) {
		this.nextShortFen = nextShortFen;
	}

	@Override
	public String toString() {
		return "MoveEvaluationDo [move=" + move + ", nextShortFen=" + nextShortFen + "]";
	}

}
