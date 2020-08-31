package app.persistence.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import app.main.service.helper.FenHelper;
import app.stockfish.engine.EngineEvaluation;

@Entity
@Table(name = "analysis")
public class AnalysisDo {

	@Id
	@Column
	private String shortFen;

	@Column
	private String fen;

	@Column
	private String onlyPawnsFen;

	@Column
	@ElementCollection
	private List<MoveEvaluation> moveEvaluations;

	@Column
	private int evaluation;

	@Column
	private String bestMove;

	@Column
	private int depth;

	@Column
	private String comment;

	public AnalysisDo() {
		// NOTHING TO DO
	}

	public AnalysisDo(String fen) {
		this.shortFen = FenHelper.getShortFen(fen);
		this.fen = fen;
		this.onlyPawnsFen = FenHelper.cleanPiecesFromFen(fen);
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

	public String getOnlyPawnsFen() {
		return onlyPawnsFen;
	}

	public void setOnlyPawnsFen(String onlyPawnsFen) {
		this.onlyPawnsFen = onlyPawnsFen;
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
	}

	public void mergeMove(String move, AnalysisDo analysis) {

		MoveEvaluation evaluation = getEvaluationByMove(move);

		if (Objects.isNull(evaluation)) {
			// new move case
			evaluation = new MoveEvaluation(move, analysis.getFen(), getEvaluation(),
					getEvaluation() - analysis.getEvaluation(), analysis.depth);
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
		//looking for the dest analyzed move
		MoveEvaluation bestEval = moveEvaluations.get(0);
		for (MoveEvaluation eval : moveEvaluations) {
			if (eval.getEvaluation() > bestEval.getEvaluation()) {
				bestEval = eval;
			}
		}
		
		//updating analysis properties
		setEvaluation(bestEval.getEvaluation());
		setBestMove(bestEval.getMove());
		setDepth(bestEval.getDepth());
		
		//updating centipawn losses
		for (MoveEvaluation eval : moveEvaluations) {
				eval.setCentipawnLoss(bestEval.getEvaluation() - eval.getEvaluation());
		}

	}

	@Override
	public String toString() {
		return "AnalysisDo [shortFen=" + shortFen + ", fen=" + fen + ", onlyPawnsFen=" + onlyPawnsFen
				+ ", moveEvaluations=" + moveEvaluations + ", evaluation=" + evaluation + ", bestMove=" + bestMove
				+ ", comment=" + comment + "]";
	}

}
