package app.web.api.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

public class User {
	
    public String userName;

    public String password;

    public Date lastLogin;
	
	public User() {
		//NOTHING TO DO
	}

	public User(String userName, String password, Date lastLogin) {
		super();
		this.userName = userName;
		this.password = password;
		this.lastLogin = lastLogin;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	@Override
	public String toString() {
		return "User [userName=" + userName + ", password=" + password + ", lastLogin=" + lastLogin + "]";
	}
	
}

