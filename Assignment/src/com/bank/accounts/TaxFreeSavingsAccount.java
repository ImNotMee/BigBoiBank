package com.bank.accounts;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;

import java.math.BigDecimal;

public class TaxFreeSavingsAccount extends Account {
 
  private int type = -1;
  private BigDecimal interestRate = BigDecimal.ZERO;
  
  /**
   * Initialize a TFSA with an id, name, and balance.
   * @param id The id of the Account. Must be a positive integer or it will not be set.
   * @param name The name of the Account. Must not be null or it will not be set.
   * @param balance The balance of the Account. Must not be a positive BidDecimal and not null or it
   *        will not be set.
   * @throws ConnectionFailedException If connection can not be made to the database.
   */
  public TaxFreeSavingsAccount(int id, String name, BigDecimal balance) 
      throws ConnectionFailedException {
    this.setId(id);
    // tries to get the type of account from the database
    this.type = DatabaseSelectHelper.getAccountType(id);
    this.setName(name);
    this.setBalance(balance);
  }
  
  /**
   * Finds the interestRate of the Account from the database, and set it if the Account exists in 
   * the database. Otherwise the interest Rate will not be set.
   * @throws ConnectionFailedException If connection can not be made to the database.
   */
  public void findAndSetInterestRate() throws ConnectionFailedException {
    // tries to set the interest rate of the ChequingAccount
    interestRate = DatabaseSelectHelper.getInterestRate(this.type);
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
