package app.main.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BoardService.class);
	
	public String getOnlyPawnsFEN(String fen) {
		return "8/pppppppp/8/8/8/8/PPPPPPPP/8 w KQkq - 0 1";
	}
	
}
