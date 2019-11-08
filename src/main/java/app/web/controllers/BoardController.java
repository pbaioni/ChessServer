package app.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import app.main.service.BoardService;
import app.persistence.model.UserDo;
import app.persistence.services.UserService;
import app.web.api.model.User;

@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;
    
    @GetMapping
    public String welcome() {
        return "Board service is ready to be used";
    }

    @PostMapping("/onlypawns")
    @ResponseStatus(HttpStatus.CREATED)
    public String getOnlyPawns(@RequestBody String fen) {

        return boardService.getOnlyPawnsFEN(fen);

    }

	
}
