package app.persistence.services;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.persistence.exceptions.EntityNotFoundException;
import app.persistence.model.UserDo;
import app.persistence.properties.UsersProperties;
import app.persistence.repo.UserRepository;
import app.web.api.model.User;

@Service
public class UserService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
		
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UsersProperties properties;
	
	public void fillDB() {
		
		LOGGER.info(properties.toString());
		Date now = new Date();
		UserDo paolo = new UserDo("Paolo", "pistache", now);
		UserDo clemence = new UserDo("Clemence", "pommepoire", now);
		UserDo superFan = new UserDo("superFan", "LoveYou", now);
		userRepository.save(paolo);
		userRepository.save(clemence);
		userRepository.save(superFan);
		
		LOGGER.info("User database filled");
	}

	public Iterable<UserDo> getAllUsers() {
		return userRepository.findAll();
	}

	public UserDo getUser(String userName) {
		return userRepository.findByUserName(userName);
	}

	public UserDo createUser(User user) {
		return userRepository.save(new UserDo(user));
	}

	public UserDo updateUser(User user) {
		UserDo userToUpdate = userRepository.findById(user.getUserName()).orElseThrow(EntityNotFoundException::new);
		userToUpdate.setPassword(user.getPassword());
		userToUpdate.setLastLogin(user.getLastLogin());
		return userRepository.save(userToUpdate);
	}

	public void deleteUser(String userName) {
      UserDo userToDelete = userRepository.findById(userName)
      .orElseThrow(EntityNotFoundException::new);
    userRepository.delete(userToDelete);
		
	}
	
}
