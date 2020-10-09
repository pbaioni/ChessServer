package app.web.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import app.persistence.model.DrawingDo;
import pbaioni.chesslib.Square;
import pbaioni.chesslib.move.Influence;

public class AnalysisDTO {

	private String fen;

	private String turn;

	private String evaluation;

	private String bestMove;

	private int depth;

	private List<MoveEvaluationDTO> moves = new ArrayList<MoveEvaluationDTO>();

	private List<InfluenceDTO> influences = new ArrayList<InfluenceDTO>();
	
	private List<DrawingDTO> drawings = new ArrayList<DrawingDTO>();

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
			this.influences.add(new InfluenceDTO(square.name().toLowerCase(), Integer.toString(influence.getInfluence().get(square))));
		}
	}
	
	public void setDrawings(Set<DrawingDo> drawingsDo) {
		for(DrawingDo drawingDo : drawingsDo) {
			drawings.add(new DrawingDTO(drawingDo.getType(), drawingDo.getPath(), drawingDo.getColor()));
		}
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

	private void calculateBestMove() {
		// looking for the best analyzed move
		MoveEvaluationDTO bestEval = moves.get(0);
		for (MoveEvaluationDTO eval : moves) {

			if (turn.equals("b")) {
				if (getIntEval(eval.getEvaluation()) < getIntEval(bestEval.getEvaluation())) {
					bestEval = eval;
				}
			} else {
				if (getIntEval(eval.getEvaluation()) > getIntEval(bestEval.getEvaluation())) {
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
			
			int centipawnLoss = (factor * getIntEval(bestEval.getEvaluation()) - (factor * getIntEval(eval.getEvaluation())));
			if(centipawnLoss > 10000) {
				centipawnLoss = 10000;
			}
			if(centipawnLoss < -10000) {
				centipawnLoss = -10000;
			}
			
			eval.setCentipawnLoss(centipawnLoss);
		}

	}
	
	private int getIntEval(String evaluation) {
		
		int intEval = 0;
		if(evaluation.contains("#")) {
			int movesToMate = Integer.parseInt(evaluation.substring(evaluation.lastIndexOf("#")+1, evaluation.length()));
			intEval = -1000000 + movesToMate;
			if(evaluation.contains("-")) {
				intEval = intEval * (-1);
			}
			if(turn.equals("b")) {
				intEval = intEval * (-1);
			}
		}else {
			intEval = Integer.parseInt(evaluation);
		}
		
		
		return intEval;
	}

	@Override
	public String toString() {
		return "AnalysisDTO [fen=" + fen + ", turn=" + turn + ", evaluation=" + evaluation + ", bestMove=" + bestMove
				+ ", depth=" + depth + ", moves=" + moves + ", influences=" + influences + ", drawings=" + drawings
				+ ", comment=" + comment + "]";
	}

}
