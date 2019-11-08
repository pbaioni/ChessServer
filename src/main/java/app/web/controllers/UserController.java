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

import app.persistence.model.UserDo;
import app.persistence.services.UserService;
import app.web.api.model.User;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @GetMapping
    public Iterable<UserDo> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userName}")
    public UserDo getUser(@PathVariable String userName) {
        return userService.getUser(userName);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDo createUser(@RequestBody User user) {
    	//TODO: use a DTO object in the request body
        UserDo createdUser = userService.createUser(user);
        return createdUser;
    }

    @PutMapping("/update")
    public UserDo updateUser(@RequestBody User user) {
    	//TODO: use a DTO object in the request body
    	return userService.updateUser(user);
    }
    
    @DeleteMapping("/delete/{userName}")
    public void deleteUser(@PathVariable String userName) {
    	userService.deleteUser(userName);
    }
	
}
