package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDAO {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);

	BigDecimal viewBalance(int id);

	List<Transfer> viewTransfer(int id);

	BigDecimal transferMoney(int id, BigDecimal moneyAdded);

	BigDecimal requestMoney(int id, BigDecimal amount);

	BigDecimal viewPendingRequests(int id, BigDecimal amount);
	
	boolean createTransfer(Transfer transfer);
	List<Transfer> viewAllTransfers();
}
