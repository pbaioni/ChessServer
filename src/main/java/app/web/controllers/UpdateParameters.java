package app.web.controllers;

public class UpdateParameters {
	
	private String fen;
	
	private Integer depth;

	public UpdateParameters(String fen, Integer depth) {
		super();
		this.fen = fen;
		this.depth = depth;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public String getFen() {
		return fen;
	}

	public void setFen(String fen) {
		this.fen = fen;
	}

	@Override
	public String toString() {
		return "UpdateParameters [fen=" + fen + ", depth=" + depth + "]";
	}

}
