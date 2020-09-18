package app.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class DrawingDo {
	
	@Id
	@GeneratedValue
	long id;
	
	@Column
	private String type;
	
	@Column
	private String path;
	
	@Column
	private String color;
	
	public DrawingDo() {
		
	}

	public DrawingDo(String type, String path, String color) {
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
		return "DrawingDo [type=" + type + ", path=" + path + ", color=" + color + "]";
	}

}
