package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

public class UserService {
	public static String AUTH_TOKEN = "";
	private final String BASE_URL;
	private AuthenticatedUser currentUser;
	private Transfer transfer;
	private RestTemplate restTemplate = new RestTemplate();
	
	public UserService(String baseURL, AuthenticatedUser currentUser, Transfer transfer) {
		this.BASE_URL = baseURL;
		this.currentUser = currentUser;
		this.transfer = transfer;
	}
	public UserService(String baseURL) {
		this.BASE_URL = baseURL;
	}
	
	public User[] viewAllUsers(AuthenticatedUser currentUser) {
		User[] users = null;
		users = restTemplate.exchange(BASE_URL + "user", HttpMethod.GET, authHeader(currentUser.getToken()), User[].class).getBody();
		return users;
	}
	
	private HttpEntity authHeader(String userToken) {
		HttpHeaders myHeader = new HttpHeaders();
		myHeader.setBearerAuth(userToken);
		HttpEntity entity = new HttpEntity<>(myHeader);
		return entity;
	}
}
