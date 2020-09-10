package app.web.api.model;

public class InfluenceDTO {

	private String square;
	
	private String influence;

	public InfluenceDTO(String square, String influence) {
		super();
		this.square = square;
		this.influence = influence;
	}

	public String getSquare() {
		return square;
	}

	public void setSquare(String square) {
		this.square = square;
	}

	public String getInfluence() {
		return influence;
	}

	public void setInfluence(String influence) {
		this.influence = influence;
	}

	@Override
	public String toString() {
		return "InfluenceDTO [square=" + square + ", influence=" + influence + "]";
	}
}
