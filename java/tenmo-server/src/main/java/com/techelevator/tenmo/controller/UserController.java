package com.techelevator.tenmo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserDAO;


@RestController
@PreAuthorize("isAuthenticated()")
public class UserController {
		private UserDAO userDao;
		public UserController(UserDAO userDao) {
			this.userDao = userDao;
		}
		
		@RequestMapping(path = "/user/{id}", method = RequestMethod.GET) 
		public double viewBalance(@PathVariable int id) {
			return userDao.viewBalance(id);
		}
	}


