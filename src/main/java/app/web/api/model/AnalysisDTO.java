package app.web.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pbaioni.chesslib.Square;
import pbaioni.chesslib.move.Influence;

public class AnalysisDTO {

	private String fen;

	private String turn;

	private String evaluation;

	private String bestMove;

	private int depth;

	private List<MoveEvaluationDTO> moves = new ArrayList<MoveEvaluationDTO>();

	private MoveEvaluationDTO randomMove;

	private List<InfluenceDTO> influences = new ArrayList<InfluenceDTO>();

	private String arrows;

	private String circles;

	private String comment;

	public AnalysisDTO() {

	}

	public AnalysisDTO(String fen, String turn, int depth, String comment) {
		super();
		this.fen = fen;
		this.turn = turn;
		this.depth = depth;
		this.comment = comment;
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

	public List<MoveEvaluationDTO> getMoves() {
		return moves;
	}

	public void setInfluences(Influence influence) {
		for (Square square : influence.getInfluence().keySet()) {
			this.influences.add(new InfluenceDTO(square.name().toLowerCase(),
					Integer.toString(influence.getInfluence().get(square))));
		}
	}

	public MoveEvaluationDTO getRandomMove() {
		return randomMove;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void addMove(String move, String evaluation) {
		if (!isMovePresent(move)) {
			moves.add(new MoveEvaluationDTO(move, evaluation));
		}
		calculateBestMove();
	}

	// must shift of 1 move the mating evaluations when just calculated to be
	// coherent with database storing logic
	public void addStockfishMove(String bestMove, String evaluation) {
		if (!isMovePresent(bestMove)) {
			moves.add(new MoveEvaluationDTO(bestMove, mapMoveEvalToPositionEval(evaluation, true)));
		}
		calculateBestMove();

	}

	public void calculateRandomMove() {

		int randomIndex = (int) ((Math.random() * (moves.size() - 0)) + 0);
		randomMove = moves.get(randomIndex);

	}

	private boolean isMovePresent(String move) {
		boolean rval = false;
		for (MoveEvaluationDTO item : getMoves()) {
			if (item.getMove().equals(move)) {
				rval = true;
				break;
			}
		}

		return rval;
	}

	@SuppressWarnings("unused")
	private void calculateBestMove() {
		// looking for the best analyzed move
		MoveEvaluationDTO bestEval = moves.get(0);
		int factor = 1;
		for (MoveEvaluationDTO eval : moves) {

			// do not take in account unanalyzed moves as bestmove candidates
			if (!eval.getEvaluation().equals("-")) {
				if (bestEval.getEvaluation().equals("-")) {
					bestEval = eval;
				} else {
					if (turn.equals("b")) {
						factor = -1;
						if (getIntEval(eval.getEvaluation()) < getIntEval(bestEval.getEvaluation())) {
							bestEval = eval;
						}
					} else {
						if (getIntEval(eval.getEvaluation()) > getIntEval(bestEval.getEvaluation())) {
							bestEval = eval;
						}
					}
				}
			}
		}
		// updating analysis properties
		setEvaluation(mapMoveEvalToPositionEval(bestEval.getEvaluation(), false));
		setBestMove(bestEval.getMove());

		// updating centipawn losses
		for (MoveEvaluationDTO eval : moves) {

			int centipawnLoss = (factor * getIntEval(bestEval.getEvaluation())
					- (factor * getIntEval(eval.getEvaluation())));
			if (centipawnLoss > 10000) {
				centipawnLoss = 10000;
			}
			if (centipawnLoss < -10000) {
				centipawnLoss = -10000;
			}

			eval.setCentipawnLoss(centipawnLoss);
		}

	}

	private String mapMoveEvalToPositionEval(String moveEvaluation, boolean inverted) {

		String positionEval = moveEvaluation;

		int moveShift = 0;
		if (inverted) {
			moveShift = -1;
		}

		if (moveEvaluation.contains("#")) {
			int moves = Integer.parseInt(
					moveEvaluation.substring(moveEvaluation.lastIndexOf("#") + 1, moveEvaluation.length())) + moveShift;
			if (moveEvaluation.contains("+#")) {
				positionEval = "-#" + moves;
			} else {
				moves++;
				positionEval = "+#" + moves;
			}
		}
		return positionEval;
	}

	private int getIntEval(String evaluation) {

		int intEval = 0;
		if (evaluation.contains("#")) {
			int movesToMate = Integer
					.parseInt(evaluation.substring(evaluation.lastIndexOf("#") + 1, evaluation.length()));
			intEval = 1000000 - movesToMate;
			if (this.turn.equals("w")) {
				intEval = -1000000 + movesToMate;
			}
			if (evaluation.contains("-")) {
				intEval = intEval * (-1);
			}
		} else {
			intEval = Integer.parseInt(evaluation);
		}

		return intEval;
	}

	@Override
	public String toString() {
		return "AnalysisDTO [fen=" + fen + ", turn=" + turn + ", evaluation=" + evaluation + ", bestMove=" + bestMove
				+ ", depth=" + depth + ", moves=" + moves + ", influences=" + influences + ", arrows=" + arrows
				+ ", circles=" + circles + ", comment=" + comment + "]";
	}
}
