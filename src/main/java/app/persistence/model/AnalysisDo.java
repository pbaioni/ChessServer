package app.persistence.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import app.main.service.helper.FenHelper;
import app.stockfish.engine.EngineEvaluation;
import ch.qos.logback.core.pattern.color.BlackCompositeConverter;

@Entity
@Table(name = "analysis")
public class AnalysisDo {

	@Id
	@Column
	private String shortFen;

	@Column
	private String fen;

	@Column
	private int evaluation;

	@Column
	private String bestMove;

	@Column
	private int depth;

	@Column
	private String comment;

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@CollectionTable(name = "moves", joinColumns = @JoinColumn(name = "move"))
	private List<MoveEvaluation> moveEvaluations;

	public AnalysisDo() {
		// NOTHING TO DO
	}

	public AnalysisDo(String fen) {
		this.shortFen = FenHelper.getShortFen(fen);
		this.fen = fen;
		moveEvaluations = new ArrayList<MoveEvaluation>();
	}

	public String getShortFen() {
		return shortFen;
	}

	public void setShortFen(String shortFen) {
		this.shortFen = shortFen;
	}

	public String getFen() {
		return fen;
	}

	public void setFen(String fen) {
		this.fen = fen;
	}

	public List<MoveEvaluation> getMoveEvaluations() {
		return moveEvaluations;
	}

	public void setMoveEvaluations(List<MoveEvaluation> moveEvaluations) {
		this.moveEvaluations = moveEvaluations;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setEngineEvaluation(EngineEvaluation engineEvaluation) {
		setEvaluation(engineEvaluation.getEvaluation());
		setBestMove(engineEvaluation.getBestMove());
		setDepth(engineEvaluation.getDepth());
		MoveEvaluation firstEval = new MoveEvaluation(getBestMove(), getEvaluation(), 0, getDepth());
		moveEvaluations.add(firstEval);
	}

	public void mergeMove(String move, AnalysisDo analysis) {

		MoveEvaluation evaluation = getEvaluationByMove(move);

		if (Objects.isNull(evaluation)) {
			int centipawnLoss = getEvaluation() - analysis.getEvaluation();
			if (FenHelper.getTurn(getFen()).equals("b")) {
				centipawnLoss = centipawnLoss * (-1);
			}
			// new move case
			evaluation = new MoveEvaluation(move, analysis.getEvaluation(), centipawnLoss, analysis.depth);
			moveEvaluations.add(evaluation);
		} else {
			// move update case (better engine depth)
			evaluation.setEvaluation(analysis.getEvaluation());
			evaluation.setDepth(analysis.getDepth());
		}

		recalculateBestMove();
	}

	private MoveEvaluation getEvaluationByMove(String move) {
		MoveEvaluation rval = null;
		for (MoveEvaluation eval : moveEvaluations) {
			if (eval.getMove().equals(move)) {
				rval = eval;
			}
		}
		return rval;
	}

	private void recalculateBestMove() {
		// looking for the best analyzed move
		MoveEvaluation bestEval = moveEvaluations.get(0);
		for (MoveEvaluation eval : moveEvaluations) {
			if (FenHelper.getTurn(getFen()).equals("b")) {
				if (eval.getEvaluation() < bestEval.getEvaluation()) {
					bestEval = eval;
				}
			} else {
				if (eval.getEvaluation() > bestEval.getEvaluation()) {
					bestEval = eval;
				}
			}
		}

		// updating analysis properties
		setEvaluation(bestEval.getEvaluation());
		setBestMove(bestEval.getMove());
		setDepth(bestEval.getDepth());

		int factor = 1;
		if (FenHelper.getTurn(getFen()).equals("b")) {
			factor = -1;
		}
		// updating centipawn losses
		for (
		MoveEvaluation eval : moveEvaluations) {
			eval.setCentipawnLoss(factor*bestEval.getEvaluation() - factor*eval.getEvaluation());
		}

	}

	@Override
	public String toString() {
		return "AnalysisDo [shortFen=" + shortFen + ", fen=" + fen + ", evaluation=" + evaluation + ", bestMove="
				+ bestMove + ", depth=" + depth + ", comment=" + comment + ", moveEvaluations=" + moveEvaluations + "]";
	}

}
