package com.techelevator.tenmo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.tenmo.services.UserService;
import com.techelevator.view.ConsoleService;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private TransferService transferService;
    private UserService userService;
    private RestTemplate restTemplate;
    
    

    public static void main(String[] args){
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new AccountService(API_BASE_URL), new TransferService(API_BASE_URL), new UserService(API_BASE_URL), new RestTemplate());
    	app.run();
   
    }

    public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService, TransferService transferService, UserService userService, RestTemplate restTemplate) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.accountService = accountService;
		this.transferService = transferService;
		this.userService = userService;
		this.restTemplate = restTemplate;
	}

	public void run(){
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		AccountService.AUTH_TOKEN = currentUser.getToken();
		TransferService.AUTH_TOKEN = currentUser.getToken();
		UserService.AUTH_TOKEN = currentUser.getToken();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				try {
					viewCurrentBalance();
				} catch (AccountServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	
	private void viewCurrentBalance() throws AccountServiceException {
		System.out.println("Current balance is : " + accountService.getBalance(currentUser));
	}

	private void viewTransferHistory() {
		System.out.println("--- TRANSFER HISTORY ---");
		int transferId = 0;
		Transfer[] allTransfers = transferService.viewAllTransfers(currentUser);
		User[] users = userService.viewAllUsers(currentUser);
		System.out.println("-----------------------------------------------------------------");
		System.out.println("Transfers ID              From/To                Amount");
		System.out.println("-----------------------------------------------------------------");
		for(Transfer transfer : allTransfers) {
			for (User user : users) {
			if(transfer.getAccountFrom() == currentUser.getUser().getId()) {
				if(transfer.getAccountTo() == user.getId()) {
					
					System.out.println(transfer.getTransferId() + "                        To: " + user.getUsername() + " "
							+ "              $" + transfer.getAmount());
				}
			} else if(transfer.getAccountTo() == currentUser.getUser().getId()) {
				if (transfer.getAccountFrom() == user.getId()) {
					
					System.out.println(transfer.getTransferId() + "                        From: " + user.getUsername() + " "
							+ "            $" + transfer.getAmount());
				}
			}
			}
		}
			System.out.println("--- Please enter a transfer Id to see details (Press 0 to cancel) ---");
			
			Scanner scan = new Scanner(System.in);
			if(scan.hasNextInt()) {
				
				transferId = scan.nextInt();
			}
			if(transferId == 0) {
				exitProgram();
			}
			for(Transfer transfer : allTransfers) {
			if(transferId == transfer.getTransferId()) {
				
				transferService.displayTransferDetails(transferId, allTransfers, users);
				break;
			} else {
				System.out.println("Please choose a valid Id");
			}
			}
			
			
			
			
		 
		
		
	}

	private void viewPendingRequests() {
		System.out.println("--- Pending Requests ---");
		Transfer[] transfers = transferService.getTransfer(currentUser.getUser().getId(), currentUser);
		for(Transfer t : transfers) {
			if(t.getTransferStatusId() == 2) {
				System.out.println(t.toString() + " ACCEPTED!");
			} else if(t.getTransferStatusId() == 3) {
				System.out.println(t.toString() + " REJECTED!");
			} else {
				System.out.println(t.toString() + " PENDING");
			}
		}
		System.out.println(transferService.getTransfer(currentUser.getUser().getId(), currentUser));
		
	}

	private void sendBucks() {
		BigDecimal amount = null;
		System.out.println("--- Send Bucks ---");
		User[] user = userService.viewAllUsers(currentUser);
		String[] usernames = new String[user.length];
		for(int i = 0; i < user.length; i++) {
			usernames[i] = user[i].getUsername();
		}
		System.out.println("--- Choose the user you would like to send money to ---");
		
		Scanner scan = new Scanner(System.in);
		Object choice = console.getChoiceFromOptions(usernames);
		if(currentUser.getUser().getUsername().toLowerCase().equals(choice) || currentUser.getUser().getId().toString().equals(choice)) {
			System.out.println("--- You cannot send money to yourself, please choose a different user ---");
			exitProgram();
		}
		
		System.out.print("Enter amount to be sent to recipient: ");
		String thisAmount = scan.nextLine();
		for(User u : user) {
			
				
			try {
			double amountDouble = Double.valueOf(thisAmount);
			amount = BigDecimal.valueOf(amountDouble);
			}catch(Exception ex) {
				System.out.println("An Error Occured");
			}
			if(accountService.getBalance(currentUser).compareTo(amount) == 1 && currentUser.getUser().getId() != u.getId()) {
				Integer[] userId = new Integer[user.length];
				for(int i = 0; i < user.length; i++) {
					userId[i] = user[i].getId();
				}
				System.out.println("Confirm who you would like to send TE bucks to");
				Object newChoice = console.getChoiceFromOptions(userId);
				BigDecimal currentUsersNewBalance = accountService.getBalance(currentUser).subtract(amount);
				String choiceString = newChoice.toString();
				int choiceInt = Integer.parseInt(choiceString);
				BigDecimal newBalance = restTemplate.exchange(API_BASE_URL + "account/" + newChoice, HttpMethod.GET, authHeader(currentUser.getToken()), BigDecimal.class).getBody();
				BigDecimal sentUsersNewBalance = newBalance.add(amount);
				
				restTemplate.put(API_BASE_URL + "account/" + currentUser.getUser().getId(), makeUserEntity(currentUsersNewBalance, currentUser.getToken()));
				restTemplate.put(API_BASE_URL + "account/" + newChoice, makeUserEntity(sentUsersNewBalance, currentUser.getToken()));
				
				Transfer transfer1 = new Transfer(2, 2, currentUser.getUser().getId(), choiceInt, amount);
				restTemplate.postForObject(API_BASE_URL + "transfer/", makeTransferEntity(transfer1, currentUser.getToken()), Transfer.class);
				System.out.println("Approved! " + currentUser.getUser().getUsername() + " Sent $" + amount + " TE bucks to " + choice);
				break;
				//transferService.createTransfer(transfer1, currentUser);
			} else if (accountService.getBalance(currentUser).compareTo(amount) == -1 && currentUser.getUser().getId() != u.getId()){
				System.out.println("Rejected!!! NOT ENOUGH FUNDS");
				break;
				//transferService.createTransfer(transfer2, currentUser);
			}
			}
		
		//System.out.println(transferService.createTransfer(2, 2, currentUser.getUser().getId(), accountTo, amount, currentUser));
		
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
	
	private HttpEntity<BigDecimal> makeUserEntity(BigDecimal amount, String userToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken);
        HttpEntity<BigDecimal> entity = new HttpEntity<>(amount, headers);
        return entity;
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
