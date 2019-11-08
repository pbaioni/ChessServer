package app.main.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.main.service.helper.BoardHelper;
import app.web.api.model.SimpleResponseWrapper;




@Service
public class BoardService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BoardService.class);
	
    @Autowired
    ObjectMapper mapper;

	public String getOnlyPawnsFen(String fen) {
		LOGGER.info("Cleaning pieces from fen: " + fen);
    	String jsonWrapper = "";
    	try {
    		jsonWrapper = mapper.writeValueAsString(new SimpleResponseWrapper(BoardHelper.cleanPiecesFromFen(fen)));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	LOGGER.info("Only pawns fen wrapper: " + jsonWrapper);
    	return jsonWrapper;
	}
	
	public String welcome() {
		String ready = "Board service is ready to be used";
		LOGGER.info("Welcome message: " + ready);
    	String jsonWrapper = "";
    	try {
    		jsonWrapper = mapper.writeValueAsString(new SimpleResponseWrapper(ready));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	LOGGER.info("Welcome wrapper: " + jsonWrapper);
    	return jsonWrapper;
	}
	


}
