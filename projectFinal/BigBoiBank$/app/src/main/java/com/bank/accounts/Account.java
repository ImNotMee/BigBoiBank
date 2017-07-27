package com.bank.accounts;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.generics.AccountTypesEnumMap;

import java.math.BigDecimal;

public abstract class Account {
  private int id = -1;
  private String name = "";
  private BigDecimal balance = null;
  private int type = -1;
  protected AccountTypesEnumMap enumMap = new AccountTypesEnumMap();
  private BigDecimal interestRate = BigDecimal.ZERO;

  
  /**
   * Get the id of the Account.
   * @return The id of the Account.
   */
  public int getId() {
    return this.id;
  }
  
  /**
   * Set the id of the Account. 
   * @param id The id of the Account.
   */
  public void setId(int id) {
    if (id > 0) {
      this.id = id;
    }
  }
  
  /**
   * Get the name of the Account.
   * @return The name of the Account.
   */
  public String getName() {
    return this.name;
  }
  
  /**
   * Set the name of the Account.
   * @param name The name of the Account.
   * @throws ConnectionFailedException If connection can not be made to the database.
   */
  public void setName(String name) throws ConnectionFailedException {
    if (name != null && name.length() > 0) {
      // check if the name is being updated
      if (!this.name.equals("")) {
        // update the name in the database
        DatabaseUpdateHelper.updateUserName(name, this.id);
      }
      this.name = name;
    }
  }
  
  /**
   * Get the balance of the Account.
   * @return The balance of the Account.
   */
  public BigDecimal getBalance() {
    return this.balance;
  }
  
  /**
   * Set the balance of the Account. Balance must be a non negative number or it will not be set.
   * @param balance The balance of the Account.
   * @throws ConnectionFailedException If connection can not be made to the database.
   */
  public void setBalance(BigDecimal balance) throws ConnectionFailedException {
    // if this is the first time the object is being instantiated
    if (this.balance == null) {
      // only set the balance in the account
      this.balance = balance;
      // ensures that the balance to be set is non negative
    } else if (balance != null) {
      this.balance = balance.setScale(2, BigDecimal.ROUND_HALF_UP);
      // set the balance of the account in the database
      DatabaseUpdateHelper.updateAccountBalance(balance.setScale(2, BigDecimal.ROUND_HALF_UP), 
          this.id);
    }
  }
  
  /**
   * Get the type of the Account.
   * @return An int representing the type of the Account.
   */
  public int getType() {
    return this.type;
  }
  
  public void setType(int accountType) {
    this.type = accountType;
  }
  
  /**
   * Finds the interestRate of the Account from the database, and set it if the Account exists in 
   * the database. Otherwise the interest Rate will not be set.
   * @throws ConnectionFailedException If connection can not be made to the database.
   */
  public void findAndSetInterestRate() throws ConnectionFailedException {
    // tries to set the interest rate of the ChequingAccount
    this.interestRate = DatabaseSelectHelper.getInterestRate(this.getType());
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
    BigDecimal newBalance = this.getBalance().add(toAdd);
    // add the amount of money to the balance
    this.setBalance(newBalance);
  }
  
  /**
   * Return a string of the Account with the type, name and balance.
   */
  public String toString() {
    String info = "";
    try {
      info = "Id: " + String.valueOf(this.id) + "\nAccount Type: " 
          + DatabaseSelectHelper.getAccountTypeName(this.type) + "\nName: " + this.name 
          + "\nBalance: " + this.balance;
    } catch (ConnectionFailedException e) {
      System.out.println("Cannot connection to database.");
    }
    return info;
  }
}
