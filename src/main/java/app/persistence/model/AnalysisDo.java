package app.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import app.main.service.helper.FenHelper;

@Entity
@Table(name="analysis")
public class AnalysisDo {
	
	@Id
	@Column
	private String shortFen;
	
	@Column
	private String fen;
	
	@Column
	private String onlyPawnsFen;
	
	@Column
	private String evaluation;
	
	public AnalysisDo() {
		//NOTHING TO DO
	}
	
	public AnalysisDo(String fen) {
		this.shortFen = FenHelper.getShortFen(fen);
		this.fen = fen;
		this.onlyPawnsFen = FenHelper.cleanPiecesFromFen(fen);
		this.evaluation = "0,2";
	}
	
	public String getShortFen() {
		return shortFen;
	}

	public void setShortFen(String shortFen) {
		this.shortFen = shortFen;
	}

	public String getFen() {
		return fen;
	}

	public void setFen(String fen) {
		this.fen = fen;
	}

	public String getOnlyPawnsFen() {
		return onlyPawnsFen;
	}

	public void setOnlyPawnsFen(String onlyPawnsFen) {
		this.onlyPawnsFen = onlyPawnsFen;
	}

	public String getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(String evaluation) {
		this.evaluation = evaluation;
	}

	@Override
	public String toString() {
		return "AnalysisDo [shortFen=" + shortFen + ", fen=" + fen + ", onlyPawnsFen=" + onlyPawnsFen + ", evaluation="
				+ evaluation + "]";
	}

}
