package app.web.controllers;

public class DeleteParameters {
	
	private String fen;
	
	private String move;

	public DeleteParameters(String fen, String move) {
		super();
		this.fen = fen;
		this.move = move;
	}

	public String getFen() {
		return fen;
	}

	public void setFen(String fen) {
		this.fen = fen;
	}

	public String getMove() {
		return move;
	}

	public void setMove(String move) {
		this.move = move;
	}

	@Override
	public String toString() {
		return "DeleteParameters [fen=" + fen + ", move=" + move + "]";
	}

}
