package com.techelevator.tenmo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserDAO;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {
	
	private List<Transfer> transfers;
	
	private UserDAO userDao;
	public TransferController(UserDAO userDao) {
		this.userDao = userDao;
	}
	
	@RequestMapping(path = "/transfer", method = RequestMethod.POST)
	public void transferMoney(@RequestBody BigDecimal moneyAdded) {
		
	}

}
