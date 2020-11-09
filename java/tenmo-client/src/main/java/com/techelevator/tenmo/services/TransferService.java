package com.techelevator.tenmo.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

@RestController
public class TransferService {

	public static String AUTH_TOKEN = "";
	private final String BASE_URL;
	private AuthenticatedUser currentUser;
	private Transfer transfer;
	private RestTemplate restTemplate = new RestTemplate();
	private Transfer[] transfers;
	private UserService userService;
	
	
	public TransferService(String baseURL, AuthenticatedUser currentUser, Transfer transfer, UserService userService) {
		this.BASE_URL = baseURL;
		this.currentUser = currentUser;
		this.transfer = transfer;
		this.userService = userService;
	}
	public TransferService(String baseURL) {
		this.BASE_URL = baseURL;
	}
	
//	public void createTransfer(Transfer transfer,  AuthenticatedUser currentUser) {
//		restTemplate.postForObject(BASE_URL + "transfer/", makeTransferEntity(transfer, currentUser.getToken()), Transfer.class);
//		
//	}
//	
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
	
	public void displayTransferDetails(int transferId, Transfer[] allTransfers, User[] users) {
		String fromUser = "";
		String toUser = "";
		String transferType = "";
		String statusType = "";
		BigDecimal amount = null;
		System.out.println("--------------------");
		System.out.println("Transfer Details");
		System.out.println("--------------------");
		System.out.println("Id: " + transferId);
		for(Transfer transfer : allTransfers) {
			if(transfer.getTransferId() == transferId) {
				amount = transfer.getAmount();
				if(transfer.getTransferTypeId() == 1) {
					transferType = "Request";
				}
				if(transfer.getTransferTypeId() == 2) {
					transferType = "Send";
				}
				if(transfer.getTransferStatusId() == 1) {
					statusType = "Pending";
				}
				if(transfer.getTransferStatusId() == 2) {
					statusType = "Approved";
				}
				if(transfer.getTransferStatusId() == 3) {
					statusType = "Rejected";
				}
				for (User u : users) {
					if(transfer.getAccountFrom() == u.getId()) {
						fromUser = u.getUsername();
					} else if (transfer.getAccountTo() == u.getId()) {
						toUser = u.getUsername();
					}
				}
			}
		}
		System.out.println("From: " + fromUser);
		System.out.println("To: " + toUser);
		System.out.println("Type: " + transferType);
		System.out.println("Status: " + statusType);
		System.out.println("Amount: " + amount);
	}
	
	 private HttpEntity<Transfer> makeTransferEntity(Transfer transfer, String userToken) {
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.setBearerAuth(userToken);
	        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
	        return entity;
	    }
	
	
	private HttpEntity authHeader(String userToken) {
		HttpHeaders myHeader = new HttpHeaders();
		myHeader.setBearerAuth(userToken);
		HttpEntity entity = new HttpEntity<>(myHeader);
		return entity;
	}
}
