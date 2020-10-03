package app.web.controllers;

public class AnalysisParameters {
	
	private String previousFen;
	private String move;
	private String fen;
	private Integer depth;
	private Boolean useEngine;
	
	public AnalysisParameters(String previousFen, String move, String fen, Integer depth, Boolean useEngine) {
		super();
		this.previousFen = previousFen;
		this.move = move;
		this.fen = fen;
		this.depth = depth;
		this.useEngine = useEngine;
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

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public Boolean getUseEngine() {
		return useEngine;
	}

	public void setUseEngine(Boolean useEngine) {
		this.useEngine = useEngine;
	}

}
