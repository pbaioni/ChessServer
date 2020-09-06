package app.web.controllers;

public class CommentParameters {
	
	private String fen;
	
	private String comment;

	public CommentParameters(String fen, String comment) {
		super();
		this.fen = fen;
		this.comment = comment;
	}

	public String getFen() {
		return fen;
	}

	public void setFen(String fen) {
		this.fen = fen;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "CommentParameters [fen=" + fen + ", comment=" + comment + "]";
	}

}
