package com.bank.accounts;

import java.math.BigDecimal;

import com.bank.exceptions.ConnectionFailedException;

public class BalanceOwingAccount extends Account {
		
	public BalanceOwingAccount(int id, String name, BigDecimal balance) 
	    throws ConnectionFailedException {
		    this.setId(id);
		    this.setType(this.enumMap.getAccountId("BALANCEOWING"));;
		    this.setName(name);
		    this.setBalance(balance);
		  }		  
	
}
