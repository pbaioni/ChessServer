package app.persistence.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
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
	private String turn;

	@Column
	private String evaluation;

	@Column
	private String bestMove;

	@Column
	private int depth;

	@Lob 
	@Column(name="COMMENT", length=1024)
	private String comment;

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@CollectionTable(name = "moves", joinColumns = @JoinColumn(name = "move"))
	private Set<MoveEvaluationDo> moves;

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@CollectionTable(name = "drawings", joinColumns = @JoinColumn(name = "drawing"))
	private Set<DrawingDo> drawings;

	public AnalysisDo() {
		// NOTHING TO DO
	}

	public AnalysisDo(String fen) {
		this.shortFen = FenHelper.getShortFen(fen);
		this.fen = fen;
		this.turn = FenHelper.getTurn(fen);
		moves = new HashSet<MoveEvaluationDo>();
		drawings = new HashSet<DrawingDo>();
		this.depth = 0;
		this.evaluation = "-";
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

	public String getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(String evaluation) {
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

	public Set<MoveEvaluationDo> getMoves() {
		return moves;
	}

	public void setMoves(Set<MoveEvaluationDo> moves) {
		this.moves = moves;
	}

	public Set<DrawingDo> getDrawings() {
		return drawings;
	}

	public void setDrawings(Set<DrawingDo> drawings) {
		this.drawings = drawings;
	}

	public boolean addMove(MoveEvaluationDo moveToAdd) {

		MoveEvaluationDo moveStored = getEvaluationByMove(moveToAdd.getMove());
		if (Objects.isNull(moveStored)) {
			moves.add(moveToAdd);
			return true;
		}

		return false;

	}

	public boolean removeMove(String move) {

		MoveEvaluationDo moveToPrune = getEvaluationByMove(move);
		if (!Objects.isNull(moveToPrune)) {
			moves.remove(moveToPrune);
			return true;
		}

		return false;

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

	public void setEngineEvaluation(EngineEvaluation eval) {
		if (!eval.isCanceled()) {
			this.setBestMove(eval.getBestMove());
			this.setEvaluation(eval.getEvaluation());
			this.setDepth(eval.getDepth());
		} else {
			if (Objects.isNull(bestMove)) {
				this.setBestMove(eval.getBestMove());
				this.setEvaluation(eval.getEvaluation());
				this.setDepth(0);
			}
		}
	}

	public void updateDrawing(String type, String path, String color) {

		// recover drawing to update
		DrawingDo drawingToUpdate = null;
		for (DrawingDo drawing : drawings) {
			if (drawing.getType().equals(type) && drawing.getPath().equals(path)) {
				drawingToUpdate = drawing;
				break;
			}
		}

		if (!Objects.isNull(drawingToUpdate)) {

			// update
			if (Objects.isNull(color)) {
				// remove
				drawings.remove(drawingToUpdate);

			} else {
				// update color
				drawingToUpdate.setColor(color);
			}
		} else {
			// add new drawing
			if (!Objects.isNull(color)) {
				drawings.add(new DrawingDo(type, path, color));
			}
		}
	}

	public void removeDrawing(DrawingDo drawing) {
		drawings.remove(drawing);
	}

	@Override
	public String toString() {
		return "AnalysisDo [shortFen=" + shortFen + ", fen=" + fen + ", turn=" + turn + ", evaluation=" + evaluation
				+ ", bestMove=" + bestMove + ", depth=" + depth + ", comment=" + comment + ", moves=" + moves + "]";
	}

}
