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
	private String turn;

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
	private List<MoveEvaluationDo> moves;

	public AnalysisDo() {
		// NOTHING TO DO
	}

	public AnalysisDo(String fen) {
		this.shortFen = FenHelper.getShortFen(fen);
		this.fen = fen;
		this.turn = FenHelper.getTurn(fen);
		moves = new ArrayList<MoveEvaluationDo>();
		this.depth = 0;
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
	
	public String getTurn() {
		return turn;
	}

	public void setTurn(String turn) {
		this.turn = turn;
	}

	public List<MoveEvaluationDo> getMoves() {
		return moves;
	}

	public void setMoves(List<MoveEvaluationDo> moves) {
		this.moves = moves;
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

	public void addMove(MoveEvaluationDo move) {

		moves.add(move);

	}

	public void removeMove(String move) {

		MoveEvaluationDo moveToPrune = getEvaluationByMove(move);

		if (!Objects.isNull(move)) {
			moves.remove(moveToPrune);
		}

	}

	public MoveEvaluationDo getEvaluationByMove(String move) {
		MoveEvaluationDo rval = null;
		for (MoveEvaluationDo eval : moves) {
			if (eval.getMove().equals(move)) {
				rval = eval;
			}
		}
		return rval;
	}

	@Override
	public String toString() {
		return "AnalysisDo [shortFen=" + shortFen + ", fen=" + fen + ", turn=" + turn + ", evaluation=" + evaluation
				+ ", bestMove=" + bestMove + ", depth=" + depth + ", comment=" + comment + ", moves=" + moves + "]";
	}

}
