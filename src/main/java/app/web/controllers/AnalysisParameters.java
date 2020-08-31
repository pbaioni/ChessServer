package app.web.controllers;

public class AnalysisParameters {
	
	private String previousFen;
	private String move;
	private String fen;
	
	public AnalysisParameters(String previousFen, String move, String fen) {
		super();
		this.previousFen = previousFen;
		this.move = move;
		this.fen = fen;
	}

	public String getPreviousFen() {
		return previousFen;
	}

	public void setPreviousFen(String previousFen) {
		this.previousFen = previousFen;
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

}
