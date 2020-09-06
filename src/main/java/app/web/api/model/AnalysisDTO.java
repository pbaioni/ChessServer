package app.web.api.model;

import java.util.ArrayList;
import java.util.List;

public class AnalysisDTO {

	private String fen;

	private String turn;

	private int evaluation;

	private String bestMove;
	
	private int depth;

	private List<MoveEvaluationDTO> moves = new ArrayList<MoveEvaluationDTO>();

	public AnalysisDTO() {

	}

	public AnalysisDTO(String fen, String turn, int depth) {
		super();
		this.fen = fen;
		this.turn = turn;
		this.depth = depth;
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

	public List<MoveEvaluationDTO> getMoves() {
		return moves;
	}

	public void addMove(String move, int evaluation) {
		if (!isMovePresent(move)) {
			moves.add(new MoveEvaluationDTO(move, evaluation));
		}
		calculateBestMove();
	}
	
	private boolean isMovePresent(String move) {
		boolean rval = false;
		for(MoveEvaluationDTO item : getMoves()) {
			if(item.getMove().equals(move)) {
				rval = true;
				break;
			}
		}
		
		return rval;
	}

	private void calculateBestMove() {
		// looking for the best analyzed move
		MoveEvaluationDTO bestEval = moves.get(0);
		for (MoveEvaluationDTO eval : moves) {
			if (turn.equals("b")) {
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

		int factor = 1;
		if (turn.equals("b")) {
			factor = -1;
		}
		// updating centipawn losses
		for (MoveEvaluationDTO eval : moves) {
			eval.setCentipawnLoss(factor * bestEval.getEvaluation() - factor * eval.getEvaluation());
		}

	}

	@Override
	public String toString() {
		return "AnalysisDTO [fen=" + fen + ", turn=" + turn + ", evaluation=" + evaluation + ", bestMove=" + bestMove
				+ ", moves=" + moves + "]";
	}

}
