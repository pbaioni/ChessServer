package app.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.main.service.BoardService;
import app.web.api.model.StringResponse;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;
    
    @GetMapping
    public String welcome() {
        return "{\"content\":\"Board service is ready to be used\"}";
    }

    @PostMapping("/onlypawns")
    public String getOnlyPawns(@RequestBody String fen) {

    	System.out.println("Receiving get only pawns body; " + fen);
    	String responseToReturn = boardService.getOnlyPawnsFEN(fen);
    	StringResponse response = new StringResponse(responseToReturn);

        return responseToReturn;

    }
	
}
