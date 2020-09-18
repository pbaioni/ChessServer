package app.web.controllers;

public class DrawingParameters {
	
	private String fen;
	
	private String type;
	
	private String path;
	
	private String color;

	public DrawingParameters(String fen, String type, String path, String color) {
		super();
		this.fen = fen;
		this.type = type;
		this.path = path;
		this.color = color;
	}

	public String getFen() {
		return fen;
	}

	public void setFen(String fen) {
		this.fen = fen;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "DrawingParameters [fen=" + fen + ", type=" + type + ", path=" + path + ", color=" + color + "]";
	}

}
