package app.web.api.model;

public class SimpleResponseWrapper {
	
	private String content;
	
	public SimpleResponseWrapper() {
		
	}
	
	public SimpleResponseWrapper(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
