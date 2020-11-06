package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

@Service
public class UserSqlDAO implements UserDAO {

    private static final double STARTING_BALANCE = 1000;
    private JdbcTemplate jdbcTemplate;

    public UserSqlDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        return jdbcTemplate.queryForObject("select user_id from users where username = ?", int.class, username);
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "select * from users";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }

        return users;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        for (User user : this.findAll()) {
            if( user.getUsername().toLowerCase().equals(username.toLowerCase())) {
                return user;
            }
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public boolean create(String username, String password) {
        boolean userCreated = false;
        boolean accountCreated = false;

        // create user
        String insertUser = "insert into users (username,password_hash) values(?,?)";
        String password_hash = new BCryptPasswordEncoder().encode(password);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String id_column = "user_id";
        userCreated = jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(insertUser, new String[]{id_column});
                    ps.setString(1, username);
                    ps.setString(2,password_hash);
                    return ps;
                }
                , keyHolder) == 1;
        int newUserId = (int) keyHolder.getKeys().get(id_column);

        // create account
        String insertAccount = "insert into accounts (user_id,balance) values(?,?)";
        accountCreated = jdbcTemplate.update(insertAccount,newUserId,STARTING_BALANCE) == 1;

        return userCreated && accountCreated;
    }
    
    @Override
    public BigDecimal viewBalance(int id) {
    	BigDecimal balance = null;
    	String sql = "SELECT balance FROM accounts WHERE user_id = ?";
    	SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
    	if(result.next()) {
    		balance = result.getBigDecimal("balance");
    	}
    	return balance;
    }
    
    @Override
    public BigDecimal transferMoney(int id, BigDecimal moneyAdded) {
    	BigDecimal transferMoney = null;
    	String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) "
    			+ "VALUES (SELECT transfer_type_id FROM transfer WHERE transfer_type_id =(SELECT transfer_type_id FROM transfer_types WHERE transfer_type_desc = 'Send'), "
    			+ "SELECT transfer_status_id FROM transfers WHERE transfer_status_id = (SELECT transfer_status_id FROM transfer_statuses WHERE transfer_status_desc = 'Approved'), "
    			+ "SELECT account_from FROM transfers WHERE account_from = (SELECT account_id FROM accounts WHERE user_id = ?),"
    			+ "SELECT account_to FROM transfers WHERE account_to = (SELECT account_id FROM accounts WHERE user_id = ?), ?";
    	SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id, id, moneyAdded);
    	if(result.next() ) {
    		transferMoney = result.getBigDecimal("amount");
    	}
    	return transferMoney;
    }
    
    @Override
    public BigDecimal requestMoney(int id, BigDecimal amount) {
    	BigDecimal requestMoney = null;
    	String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) "
    			+ "VALUES (SELECT transfer_type_id FROM transfer WHERE transfer_type_id =(SELECT transfer_type_id FROM transfer_types WHERE transfer_type_desc = 'Request'), "
    			+ "SELECT transfer_status_id FROM transfers WHERE transfer_status_id = (SELECT transfer_status_id FROM transfer_statuses WHERE transfer_status_desc = 'Pending'), "
    			+ "SELECT account_from FROM transfers WHERE account_from = (SELECT account_id FROM accounts WHERE user_id = ?),"
    			+ "SELECT account_to FROM transfers WHERE account_to = (SELECT account_id FROM accounts WHERE user_id = ?), ?";
    	SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id, id, amount);
    	if(result.next() ) {
    		requestMoney = result.getBigDecimal("amount");
    	}
    	return requestMoney;
    }
    
    @Override
    public BigDecimal viewPendingRequests(int id, BigDecimal amount) {
    	BigDecimal viewPendingRequests = null;
    	String sql = "SELECT * FROM transfers WHERE transfer_status_id = (SELECT transfer_status_id FROM transfer_statuses WHERE transfer_status_desc = 'Pending') "
    			+ "AND transfer_type_id = (SELECT transfer_type_id FROM transfer_types WHERE trasnfer_type_desc = 'Request'";
    	SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id, id, amount);
    	if(result.next() ) {
    		viewPendingRequests = result.getBigDecimal("amount");
    	}
    	return viewPendingRequests;
    }
    
    @Override
    public List<Transfer> viewTransfer(int id) {
    	List<Transfer> transfers = new ArrayList<>();
    	String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
    	SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
    	
    	while(result.next()) {
    		Transfer transfer = mapRowToTransfer(result);
    		transfers.add(transfer);
    	}
    	return transfers;
    	
    }
    
    @Override
    public boolean createTransfer(Transfer transfer) {
    	String sql = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) "
    			+ "VALUES (?, ?, ?, ?, ?)";
    	return jdbcTemplate.update(sql, transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount()) == 1;
    }
    
    @Override
    public List<Transfer> viewAllTransfers() {
    	String sql = "SELECT * FROM transfers";
    	return viewAllTransfers();
    }
    
    

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("ROLE_USER");
        return user;
    }
    
    private Transfer mapRowToTransfer(SqlRowSet rs) {
    	Transfer transfer = new Transfer();
    	transfer.setTransferId(rs.getInt("transfer_id"));
    	transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
    	transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
    	transfer.setAccountFrom(rs.getInt("account_from"));
    	transfer.setAccountTo(rs.getInt("account_to"));
    	transfer.setAmount(rs.getBigDecimal("amount"));
    	return transfer;
    }
}
