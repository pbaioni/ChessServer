package app.web.controllers;

public class DrawingParameters {
	
	private String fen;
	
	private String drawing;

	public DrawingParameters(String fen, String drawing) {
		super();
		this.fen = fen;
		this.drawing = drawing;
	}

	public String getFen() {
		return fen;
	}

	public void setFen(String fen) {
		this.fen = fen;
	}

	public String getDrawing() {
		return drawing;
	}

	public void setDrawing(String drawing) {
		this.drawing = drawing;
	}

	@Override
	public String toString() {
		return "DrawingParameters [drawing=" + drawing + "]";
	}
}
