package app.web.api.model;

public class DrawingDTO {
	
	private String type;
	
	private String path;
	
	private String color;

	public DrawingDTO(String type, String path, String color) {
		super();
		this.type = type;
		this.path = path;
		this.color = color;
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
		return "DrawingDTO [type=" + type + ", path=" + path + ", color=" + color + "]";
	}

}
