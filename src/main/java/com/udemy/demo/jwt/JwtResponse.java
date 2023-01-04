package com.udemy.demo.jwt;

public class JwtResponse {
	
	String login;
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}
	
	public JwtResponse(String login){
		this.login = login;
	}

}
