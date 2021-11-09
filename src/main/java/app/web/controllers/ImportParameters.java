package app.web.controllers;

public class ImportParameters {
	
	private Integer openingDepth;
	
	private Integer analysisDepth;

	public ImportParameters(Integer openingDepth, Integer analysisDepth) {
		super();
		this.openingDepth = openingDepth;
		this.analysisDepth = analysisDepth;
	}

	public Integer getOpeningDepth() {
		return openingDepth;
	}

	public void setOpeningDepth(Integer openingDepth) {
		this.openingDepth = openingDepth;
	}

	public Integer getAnalysisDepth() {
		return analysisDepth;
	}

	public void setAnalysisDepth(Integer analysisDepth) {
		this.analysisDepth = analysisDepth;
	}

	@Override
	public String toString() {
		return "ImportParameters [openingDepth=" + openingDepth + ", analysisDepth=" + analysisDepth + "]";
	}

}
