package app.web.controllers;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.main.service.AnalysisService;
import app.web.api.model.AnalysisDTO;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/board")
public class AnalysisController {

	@Autowired
	private AnalysisService analysisService;
	
	private Timer shutdownTimer = new Timer();

	@GetMapping
	public ResponseEntity<String> welcome() {
		
		return ResponseEntity.ok(analysisService.welcome());
	}

	@PostMapping("/onlypawns")
	public ResponseEntity<String> getOnlyPawns(@RequestBody String fen) {
		return ResponseEntity.ok(analysisService.getOnlyPawnsFen(fen.replace("\"", "")));
	}

	@PostMapping("/analysis")
	public ResponseEntity<AnalysisDTO> getAnalysis(@RequestBody AnalysisParameters params) throws InterruptedException, ExecutionException {
		return ResponseEntity.ok(analysisService.performAnalysis(params));
	}
	
	@PostMapping("/drawing")
	public void updateDrawings(@RequestBody DrawingParameters drawingParameters) {
		analysisService.updateDrawing(drawingParameters);
	}

	@PostMapping("/delete")
	public ResponseEntity<String> deleteLine(@RequestBody DeleteParameters deleteParameters) {
		return ResponseEntity.ok(analysisService.deleteLine(deleteParameters));
	}

	@PostMapping("/update")
	public ResponseEntity<String>  updateDepth(@RequestBody UpdateParameters updateParameters) throws InterruptedException, ExecutionException {

		return ResponseEntity.ok(analysisService.updateDepth(updateParameters, false));
	}

	@PostMapping("/comment")
	public ResponseEntity<String>  setComment(@RequestBody CommentParameters commentParameters) {
		return ResponseEntity.ok(analysisService.setComment(commentParameters));
	}
	
	@PostMapping("/import")
	public ResponseEntity<String>  importGames(@RequestBody ImportParameters importParameters) throws Exception {
		return ResponseEntity.ok(analysisService.fillDatabaseFromPGN(importParameters));
	}
	
	@GetMapping("/stop")
	public void stopTask() throws Exception {
		analysisService.stopTask();
	}

}
