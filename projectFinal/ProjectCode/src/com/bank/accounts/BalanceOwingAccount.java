package com.bank.accounts;

import java.math.BigDecimal;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;

public class BalanceOwingAccount extends Account {
	
	private  BigDecimal interestRate = BigDecimal.ZERO;
	
	public BalanceOwingAccount(int id, String name, BigDecimal balance) throws ConnectionFailedException {
		    this.setId(id);
		    this.setType(this.enumMap.getAccountId("BALANCEOWING"));;
		    this.setName(name);
		    this.setBalance(balance);
		  }

	 public void findAndSetInterestRate() throws ConnectionFailedException {
		    // tries to set the interest rate of the ChequingAccount
		    interestRate = DatabaseSelectHelper.getInterestRate(this.getType());
		  }
		  
		  /**
		   * Add money to the balance of the account, based on the interest of the account. 
		   * @throws ConnectionFailedException If connection can not be made to the database.
		   */
	public void addInterest() throws ConnectionFailedException {
		    // ensures most recent interest rate is being used
		    this.findAndSetInterestRate();
		    // find the amount of money to be added to the balance
		    BigDecimal toAdd = this.getBalance().multiply(interestRate);
		    // add the amount of money to the balance
		    this.setBalance(this.getBalance().add(toAdd));
		  }
	
		  
	
}
