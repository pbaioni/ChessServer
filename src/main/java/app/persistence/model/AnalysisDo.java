package app.persistence.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import stockfish4j.model.EngineEvaluation;


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
	@Column(name = "COMMENT", length = 2048)
	private String comment;

	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@CollectionTable(name = "moves", joinColumns = @JoinColumn(name = "move"))
	private Set<MoveEvaluationDo> moves;

	@Column
	private String arrows;

	@Column
	private String circles;

	public AnalysisDo() {
		// NOTHING TO DO
	}

	public AnalysisDo(String fen) {
		this.shortFen = FenHelper.getShortFen(fen);
		this.fen = fen;
		this.turn = FenHelper.getTurn(fen);
		moves = new HashSet<MoveEvaluationDo>();
		this.depth = 0;
		this.evaluation = "-";
		this.arrows = "";
		this.circles = "";
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

	public String getArrows() {
		return arrows;
	}

	public void setArrows(String arrows) {
		this.arrows = arrows;
	}

	public String getCircles() {
		return circles;
	}

	public void setCircles(String circles) {
		this.circles = circles;
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

			this.setBestMove(eval.getBestmove());
			this.setEvaluation(String.valueOf(eval.getEvaluation()));
			this.setDepth(eval.getDepth());

	}

	public void updateDrawing(String drawing) {

		Matcher matcher;

		String arrowRegex = "^[A-Z]{1}[a-z]{1}[0-9]{1}[a-z]{1}[0-9]{1}$";
		matcher = Pattern.compile(arrowRegex).matcher(drawing);
		if (matcher.matches()) {
			addArrow(drawing);
		}

		String circleRegex = "^[A-Z]{1}[a-z]{1}[0-9]{1}$";
		matcher = Pattern.compile(circleRegex).matcher(drawing);
		if (matcher.matches()) {
			addCircle(drawing);
		}

		String removeArrowRegex = "^[a-z]{1}[0-9]{1}[a-z]{1}[0-9]{1}$";
		matcher = Pattern.compile(removeArrowRegex).matcher(drawing);
		if (matcher.matches()) {
			removeArrow(drawing);
		}

		String removeCircleRegex = "^[a-z]{1}[0-9]{1}$";
		matcher = Pattern.compile(removeCircleRegex).matcher(drawing);
		if (matcher.matches()) {
			removeCircle(drawing);
		}

	}

	private void addArrow(String arrowToAdd) {
		String newArrows = "";
		boolean update = false;
		for (String arrow : this.arrows.split(",")) {
			String trimmedArrow = arrow.trim();
			if (trimmedArrow.length() == 5) {
				if (arrowToAdd.substring(1, 5).equals(trimmedArrow.substring(1, 5))) {
					update = true;
					newArrows += ", " + arrowToAdd;
				} else {
					newArrows += ", " + trimmedArrow;
				}
			}
		}

		if (!update) {
			newArrows += ", " + arrowToAdd;
		}

		if (newArrows.startsWith(", ")) {
			newArrows = newArrows.substring(2, newArrows.length());
		}

		setArrows(newArrows);
	}

	private void addCircle(String circleToAdd) {
		String newCircles = "";
		boolean update = false;
		for (String circle : this.circles.split(",")) {
			String trimmedCircle = circle.trim();
			if (trimmedCircle.length() == 3) {
				if (circleToAdd.substring(1, 3).equals(trimmedCircle.substring(1, 3))) {
					update = true;
					newCircles += ", " + circleToAdd;
				} else {
					newCircles += ", " + trimmedCircle;
				}
			}
		}

		if (!update) {
			newCircles += ", " + circleToAdd;
		}

		if (newCircles.startsWith(", ")) {
			newCircles = newCircles.substring(2, newCircles.length());
		}
		setCircles(newCircles);
	}

	private void removeArrow(String arrowToRemove) {
		String newArrows = "";
		for (String arrow : this.arrows.split(",")) {
			String trimmedArrow = arrow.trim();
			if (trimmedArrow.length() == 5) {
			if (!arrowToRemove.substring(0, 4).equals(arrow.trim().substring(1, 5))) {
				newArrows += ", " + trimmedArrow;
			}
			}
		}

		if (newArrows.startsWith(", ")) {
			newArrows = newArrows.substring(2, newArrows.length());
		}

		setArrows(newArrows);
	}

	private void removeCircle(String circleToRemove) {
		String newCircles = "";
		for (String circle : this.circles.split(",")) {
			String trimmedCircle = circle.trim();
			if (trimmedCircle.length() == 3) {
				if (!circleToRemove.substring(0, 2).equals(trimmedCircle.substring(1, 3))) {
					newCircles += ", " + trimmedCircle;
				}
			}
		}

		if (newCircles.startsWith(", ")) {
			newCircles = newCircles.substring(2, newCircles.length());
		}

		setCircles(newCircles);
	}

	@Override
	public String toString() {
		return "AnalysisDo [shortFen=" + shortFen + ", fen=" + fen + ", turn=" + turn + ", evaluation=" + evaluation
				+ ", bestMove=" + bestMove + ", depth=" + depth + ", comment=" + comment + ", moves=" + moves + "]";
	}

}
