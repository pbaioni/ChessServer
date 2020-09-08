package app.web.controllers;

public class ImportParameters {
	
	private String openingDepth;
	
	private String analysisDepth;

	public ImportParameters(String openingDepth, String analysisDepth) {
		super();
		this.openingDepth = openingDepth;
		this.analysisDepth = analysisDepth;
	}

	public String getOpeningDepth() {
		return openingDepth;
	}

	public void setOpeningDepth(String openingDepth) {
		this.openingDepth = openingDepth;
	}

	public String getAnalysisDepth() {
		return analysisDepth;
	}

	public void setAnalysisDepth(String analysisDepth) {
		this.analysisDepth = analysisDepth;
	}

	@Override
	public String toString() {
		return "ImportParameters [openingDepth=" + openingDepth + ", analysisDepth=" + analysisDepth + "]";
	}

}
