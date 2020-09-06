package app.web.controllers;

public class UpdateParameters {
	
	private String fen;
	
	private String depth;

	public UpdateParameters(String fen, String depth) {
		super();
		this.fen = fen;
		this.depth = depth;
	}

	public String getDepth() {
		return depth;
	}

	public void setDepth(String depth) {
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
