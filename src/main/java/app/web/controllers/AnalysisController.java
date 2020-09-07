package app.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import app.main.service.AnalysisService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/board")
public class AnalysisController {

	@Autowired
	private AnalysisService analysisService;

	@GetMapping
	public String welcome() {
		return analysisService.welcome();
	}

	@PostMapping("/onlypawns")
	public String getOnlyPawns(@RequestBody String fen) {
		return analysisService.getOnlyPawnsFen(fen.replace("\"", ""));
	}

	@PostMapping("/analysis")
	public String getAnalysis(@RequestBody String analysisParameters) {
		Gson g = new Gson();
		AnalysisParameters params = g.fromJson(analysisParameters, AnalysisParameters.class);
		return analysisService.performAnalysis(params.getPreviousFen(), params.getMove(), params.getFen(), 24);
	}

	@PostMapping("/delete")
	public String deleteLine(@RequestBody String deleteParameters) {
		Gson g = new Gson();
		DeleteParameters params = g.fromJson(deleteParameters, DeleteParameters.class);
		return analysisService.deleteLine(params.getFen(), params.getMove());

	}

	@PostMapping("/update")
	public void updateDepth(@RequestBody String updateParameters) {
		Gson g = new Gson();
		UpdateParameters params = g.fromJson(updateParameters, UpdateParameters.class);
		analysisService.updateDepth(params.getFen(), Integer.parseInt(params.getDepth()), false);

	}

	@PostMapping("/comment")
	public String setComment(@RequestBody String commentParameters) {
		Gson g = new Gson();
		CommentParameters params = g.fromJson(commentParameters, CommentParameters.class);
		return analysisService.setComment(params.getFen(), params.getComment());

	}

}
