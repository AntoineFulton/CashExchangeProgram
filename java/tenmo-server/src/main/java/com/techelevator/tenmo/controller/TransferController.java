package com.techelevator.tenmo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Transfer;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {
	
	//private List<Transfer> transfers;
	
	private UserDAO userDao;
	public TransferController(UserDAO userDao) {
		this.userDao = userDao;
	}
	
	
	//Sending bucks
	/*@RequestMapping(path = "/transfer/{id}", method = RequestMethod.PUT)
	public BigDecimal transferMoney(@PathVariable int id, @RequestBody BigDecimal moneyAdded) {
		return userDao.transferMoney(id, moneyAdded);
	}
	
	//*Be able to view a list of users to send bucks to*
	
	
	
	//Requesting bucks
	@RequestMapping(path = "/transfer/{id}", method = RequestMethod.GET)
	public BigDecimal requestMoney(@PathVariable int id, @RequestBody BigDecimal amount) {
		return userDao.requestMoney(id, amount);
	}
	
	//Be able to view a list of users to request bucks from*
	
	
	//View pending request bucks
	@RequestMapping(path = "/transfer", method = RequestMethod.GET)
	public BigDecimal viewPendingRequests(@PathVariable int id, @RequestBody BigDecimal amount) {
		return userDao.transferMoney(id, amount);
	} */
	
	
	
	//Create transfer
	@RequestMapping(path = "/transfer/", method = RequestMethod.POST)
	public void createTransfer(@RequestBody Transfer transfer) {
		userDao.createTransfer(transfer);
	}
	
	//View all transfers  (questionable about this and the UserSqlDao version of this)
	@RequestMapping(path = "/transfer", method = RequestMethod.GET)
	public List<Transfer> viewAllTransfers() {
		return userDao.viewAllTransfers();
	}
	

	//Method for viewTransferHistory
	@RequestMapping(path="/transfer/{id}", method = RequestMethod.GET)
		public void getTransfer(@PathVariable int id) {
			userDao.viewTransfer(id);
		}
	


}
