package com.techelevator.tenmo.services;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;

import com.techelevator.view.ConsoleService;

import com.techelevator.tenmo.models.AuthenticatedUser;


public class AccountService{
	public static String AUTH_TOKEN = "";
	private final String BASE_URL;
	private AuthenticatedUser currentUser;
	private RestTemplate restTemplate = new RestTemplate();
	
	public AccountService(String baseURL, AuthenticatedUser currentUser) {
		this.BASE_URL = baseURL;
		this.currentUser = currentUser;
	}
	public AccountService(String baseURL) {
		this.BASE_URL = baseURL;
	}
	
	//TODO: currentUser is coming up as null for some some
	public BigDecimal getBalance(AuthenticatedUser currentUser) {
		BigDecimal balanceResponse = restTemplate.exchange(BASE_URL + "account/" + 
										currentUser.getUser().getId(), HttpMethod.GET, authHeader(currentUser.getToken()), BigDecimal.class).getBody();
		return balanceResponse;
	}
	
	/*private HttpEntity<String> makeUserEntity(String userToken) {
		HttpHeaders myHeader = new HttpHeaders();
		myHeader.setContentType(MediaType.APPLICATION_JSON);
		myHeader.setBearerAuth(AUTH_TOKEN);
		HttpEntity<String> entity = new HttpEntity<>(userToken, myHeader);
		return entity;
	}*/
	
	private HttpEntity authHeader(String userToken) {
		HttpHeaders myHeader = new HttpHeaders();
		myHeader.setBearerAuth(userToken);
		HttpEntity entity = new HttpEntity<>(myHeader);
		return entity
	}

	

}
