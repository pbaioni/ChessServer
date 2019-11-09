package app.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.main.service.AnalysisService;

@CrossOrigin(origins = "http://localhost:4200")
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
    	return analysisService.getOnlyPawnsFen(fen);
    }
    
    @PostMapping("/analysis")
    public String getAnalysis(@RequestBody String fen) {
    	return analysisService.getAnalysis(fen);
    }
	
}
