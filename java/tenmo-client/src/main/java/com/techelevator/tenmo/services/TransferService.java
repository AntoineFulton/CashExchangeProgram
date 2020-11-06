package com.techelevator.tenmo.services;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;

public class TransferService {

	public static String AUTH_TOKEN = "";
	private final String BASE_URL;
	private AuthenticatedUser currentUser;
	private RestTemplate restTemplate = new RestTemplate();
	
	public TransferService(String baseURL, AuthenticatedUser currentUser) {
		this.BASE_URL = baseURL;
		this.currentUser = currentUser;
	}
	public TransferService(String baseURL) {
		this.BASE_URL = baseURL;
	}
	
	public boolean createTransfer(AuthenticatedUser currentUser) {
		balanceResponse = restTemplate.exchange(BASE_URL + "transfer", HttpMethod.POST, authHeader(currentUser.getToken())).getBody();
	}
	
	public Transfer[] viewAllTransfers(AuthenticatedUser currentUser) {
		Transfer[] transfer = null;
		transfer = restTemplate.exchange(BASE_URL + "transfer", HttpMethod.GET, authHeader(currentUser.getToken()), Transfer[].class).getBody();
		return transfer;
	}
	
	public void getTransfer(int id) {
		
	}
	
	
	private HttpEntity authHeader(String userToken) {
		HttpHeaders myHeader = new HttpHeaders();
		myHeader.setBearerAuth(userToken);
		HttpEntity entity = new HttpEntity<>(myHeader);
		return entity;
	}
}
