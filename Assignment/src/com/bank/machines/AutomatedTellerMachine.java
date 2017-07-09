package com.bank.machines;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.users.Customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AutomatedTellerMachine {
  private Customer currentCustomer = null;
  private boolean authenticated = false;
  
  /**
   * Instantiate an AutomatedTellerMachine with a Customer and their possible password.
   * @param customerId The id of the Customer.
   * @param password The possible password of the Customer.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public AutomatedTellerMachine(int customerId, String password) throws ConnectionFailedException {
    // create a Customer object from the information in the database
    this.currentCustomer = (Customer) DatabaseSelectHelper.getUserDetails(customerId);
    // ensure the customer has the correct password if the customer exists
    if (this.currentCustomer != null) {
      this.authenticated = currentCustomer.authenticate(password);
    }
  }
  
  /**
   * Instantiate an AutomatedTellerMachine with a Customer.
   * @param customerId The id of the Customer.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public AutomatedTellerMachine(int customerId) throws ConnectionFailedException {
    // create a Customer object from the information in the database
    this.currentCustomer = (Customer) DatabaseSelectHelper.getUserDetails(customerId);
  }
  
  /**
   * Used to Authenticate a User. Can 
   * @param userId The id of the user.
   * @param password The possible password of the User.
   * @return true if the password was correct and the User is authenticated, false otherwise
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public boolean authenticate(int userId, String password) throws ConnectionFailedException {
    // ensure the id belongs to the current customer
    if (userId == this.currentCustomer.getId()) {
      // check if the password is correct
      this.authenticated = currentCustomer.authenticate(password);
    }
    // return if it worked
    return this.authenticated;
  }
  
  /**
   * Get the accounts for the currentUser. 
   * @return A List of Account of the currentUser. 
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public List<Account> listAccounts() throws ConnectionFailedException {
    // ensure the user is authenticated
    if (!this.authenticated) {
      System.out.println("You are not authenticated and can not use the machine.");
      return null;
    } else {
      // create a list to hold the accounts
      List<Account> accounts = new ArrayList<Account>();
      // get the id of the accounts for the current customer
      List<Integer> ids = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId());
      // loop through each id, creating a new account for it
      for (int id: ids) {
        // create a new user and add it
        accounts.add(DatabaseSelectHelper.getAccountDetails(id));
      }
      // return the list of accounts
      return accounts;
    }
  }
  
  /**
   * Make a deposit of the given amount to the given account if it is an account that the user has 
   * access to. 
   * @param amount Amount to be added to the account.
   * @param accountId The id of the account.
   * @return true if the amount was added successfully, false otherwise
   * @throws ConnectionFailedException If database was not successfully connected to.
   * @throws IllegalAmountException If the amount to be deposited is not valid.
   */
  public boolean makeDeposit(BigDecimal amount, int accountId) throws ConnectionFailedException, 
      IllegalAmountException {
    // check if the user is authenticated
    if (!this.authenticated) {
      System.out.println("You are not authenticated and can not use the machine.");
      return false;
    // check if the user has access to the account
    } else if (!DatabaseSelectHelper.getAccountIds(
        this.currentCustomer.getId()).contains(accountId)) {
      System.out.println("You do not have access to this account.");
      return false;
    // check if the amount given is valid 
    } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalAmountException("Input given is an illegal amount.");
    } else {
      // add the deposit
      return DatabaseUpdateHelper.updateAccountBalance(
          DatabaseSelectHelper.getBalance(accountId).add(amount), accountId);
    }
  }
  
  /**
   * Get the balance of the given account id.
   * @param accountId The id of the account to look for.
   * @return The balance of the account.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public BigDecimal checkBalance(int accountId) throws ConnectionFailedException {
    // check if the user is authenticated
    if (!this.authenticated) {
      System.out.println("You are not authenticated and can not use the machine.");
      return null;
    // check if the user has access to the account
    } else if (!DatabaseSelectHelper.getAccountIds(
        this.currentCustomer.getId()).contains(accountId)) { 
      System.out.println("You do not have access to this account.");
      return null;
    } else {
      // get the balance of the account
      return DatabaseSelectHelper.getBalance(accountId);
    }   
  }
  
  /**
   * Withdraw money from a given account.
   * @param amount The amount of money to be withdrawn.
   * @param accountId The id of the account.
   * @return true if the money was successfully withdrawn, false otherwise
   * @throws ConnectionFailedException If database was not successfully connected to.
   * @throws IllegalAmountException If the amount to be deposited is not valid.
   * @throws InsufficientFundsException If the account does not have enough funds to be withdrawn.
   */
  public boolean makeWithdrawal(BigDecimal amount, int accountId) throws ConnectionFailedException, 
        IllegalAmountException, InsufficientFundsException {
    // check if the user is authenticated
    if (!this.authenticated) {
      System.out.println("You are not authenticated and can not use the machine.");
      return false;
    } else if (!DatabaseSelectHelper.getAccountIds(
        this.currentCustomer.getId()).contains(accountId)) {
      System.out.println("You do not have access to this account.");
      return false;
    // check if the amount given is valid
    } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalAmountException("The amount given is invalid");
    // check if the account has enough money to be withdrawn
    } else if (
        DatabaseSelectHelper.getBalance(accountId).subtract(amount).compareTo(BigDecimal.ZERO) 
        < 0) {
      throw new InsufficientFundsException("You do not have enough funds to withdraw that amount.");
    } else {
      return DatabaseUpdateHelper.updateAccountBalance(
          DatabaseSelectHelper.getBalance(accountId).subtract(amount), accountId);
    }
  }
}
