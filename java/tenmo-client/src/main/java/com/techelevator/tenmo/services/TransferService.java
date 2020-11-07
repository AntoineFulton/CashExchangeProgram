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
	private Transfer transfer;
	private RestTemplate restTemplate = new RestTemplate();
	
	public TransferService(String baseURL, AuthenticatedUser currentUser, Transfer transfer) {
		this.BASE_URL = baseURL;
		this.currentUser = currentUser;
		this.transfer = transfer;
	}
	public TransferService(String baseURL) {
		this.BASE_URL = baseURL;
	}
	
	public boolean createTransfer(Transfer transfer, AuthenticatedUser currentUser) {
		boolean balanceResponse = restTemplate.exchange(BASE_URL + "transfer", HttpMethod.POST, authHeader(currentUser.getToken()), boolean.class).getBody();
		return balanceResponse;
	}
	
	public Transfer[] viewAllTransfers(AuthenticatedUser currentUser) {
		Transfer[] transfers = null;
		transfers = restTemplate.exchange(BASE_URL + "transfer", HttpMethod.GET, authHeader(currentUser.getToken()), Transfer[].class).getBody();
		return transfers;
	}
	
	public Transfer[] getTransfer(int id, AuthenticatedUser currentUser) {
		Transfer[] transfer = null;
		transfer = restTemplate.exchange(BASE_URL + "transfer/" + currentUser.getUser().getId(), HttpMethod.GET, authHeader(currentUser.getToken()), Transfer[].class).getBody();
		return transfer;
	}
	
	
	private HttpEntity authHeader(String userToken) {
		HttpHeaders myHeader = new HttpHeaders();
		myHeader.setBearerAuth(userToken);
		HttpEntity entity = new HttpEntity<>(myHeader);
		return entity;
	}
}
