package com.techelevator.tenmo.controller;

import java.math.BigDecimal;
import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserDAO;

// TODO Create AccountDAO and AccountSqlDAO classes or use UserSQLDao

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountController {
	
	private UserDAO userDao;
	public AccountController(UserDAO userDao) {
		// TODO Use dependency injection (Constructor Injection) to set the private DAO members and make sure they are added to the constructor
		this.userDao = userDao;
	}
	
	@RequestMapping(path = "/account/{id}", method = RequestMethod.GET) 
	public BigDecimal getBalance(@PathVariable int id) {
		return userDao.viewBalance(id);
	}
	

}
