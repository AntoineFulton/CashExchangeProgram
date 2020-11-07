package com.techelevator.tenmo;

import java.math.BigDecimal;
import java.util.Scanner;

import com.techelevator.tenmo.models.AuthenticatedUser;
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
    

    public static void main(String[] args){
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new AccountService(API_BASE_URL), new TransferService(API_BASE_URL), new UserService(API_BASE_URL));
    	app.run();
   
    }

    public App(ConsoleService console, AuthenticationService authenticationService, AccountService accountService, TransferService transferService, UserService userService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.accountService = accountService;
		this.transferService = transferService;
		this.userService = userService;
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

	//TODO: It recognizes user per debug but once it reaches AccountService class it forgets who user is
	private void viewCurrentBalance() throws AccountServiceException {
		System.out.println("Current balance is : " + accountService.getBalance(currentUser));
	}

	private void viewTransferHistory() {
		System.out.println("--- TRANSFER HISTORY ---");
		System.out.println(transferService.viewAllTransfers(currentUser).toString());
		
	}

	private void viewPendingRequests() {
		System.out.println("--- Pending Requests ---");
		System.out.println(transferService.getTransfer(currentUser.getUser().getId(), currentUser));
		
	}

	private void sendBucks() {
		System.out.println("--- Send Bucks ---");
		User[] user = userService.viewAllUsers(currentUser);
		String[] usernames = new String[user.length];
		for(int i = 0; i < user.length; i++) {
			usernames[i] = user[i].getUsername();
		}
		System.out.println("--- Choose the user you would like to send money to ---");
		
		String choice = (String)console.getChoiceFromOptions(usernames);
		for(User u : user) {
			
			if(u.getUsername().equals(choice)) {
				System.out.println("User ID" + "   |   " + "Name");
				System.out.println(currentUser.getUser().getId() + "   |   " + currentUser.getUser().getUsername());
				System.out.println(u.getId() + "   |   " + u.getUsername());
			}
			Scanner scan = new Scanner(System.in);
			
			System.out.println("Enter amount to be sent to recipient: ");
			String thisAmount = scan.nextLine();
			BigDecimal amount = new BigDecimal(thisAmount);
			if(accountService.getBalance(currentUser).compareTo(amount) >= 0) {
				transferService.createTransfer(2, 2, currentUser.getUser().getId(), u.getId(), amount, currentUser);
			} else {
				transferService.createTransfer(2, 3, currentUser.getUser().getId(), u.getId(), amount, currentUser);
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
}
