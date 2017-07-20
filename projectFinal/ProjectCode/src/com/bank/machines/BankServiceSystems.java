package com.bank.machines;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.users.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public abstract class BankServiceSystems {

  protected User currentCustomer;
  protected boolean currentCustomerAuthenticated;
  
  /**
   * Used to Authenticate the currentCustomer 
   * @param password The possible password of the User.
   * @return true if the password was correct and the User is authenticated, false otherwise
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public boolean authenticateCurrentCustomer(String password) throws ConnectionFailedException {
    this.currentCustomerAuthenticated = currentCustomer.authenticate(password);
    if (this.currentCustomerAuthenticated) {
      // print out the details of the customer and their accounts
      this.printCustomerName();
      this.printCustomerAddress();
      this.printCustomerAccounts();
    }
    return this.currentCustomerAuthenticated;
  }
  
  /**
   * Get the accounts for the currentUser. 
   * @return A List of Account of the currentUser. 
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public List<Account> listCustomerAccounts() throws ConnectionFailedException {
    if (!this.currentCustomerAuthenticated) {
      System.out.println("Customer is not authenticated");
      return null;
    } else {
      // create a list to hold the accounts
      List<Account> accounts = new ArrayList<Account>();
      List<Integer> ids = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId());
      // loop through each id, creating a new account for it
      for (int id: ids) {
        accounts.add(DatabaseSelectHelper.getAccountDetails(id));
      }
      return accounts;
    }
  }
  
  /**
   * Get the balance of the given account id.
   * @param accountId The id of the account to look for.
   * @return The balance of the account. Null if the customer is not authenticated or does not have 
   *         access to the account.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public BigDecimal checkBalance(int accountId) throws ConnectionFailedException {
    // check if the user is authenticated
    if (!this.currentCustomerAuthenticated) {
      System.out.println("The customer is not authenticated.");
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
    if (!this.currentCustomerAuthenticated) {
      System.out.println("The customer is not authenticated.");
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
    if (!this.currentCustomerAuthenticated) {
      System.out.println("The customer is not authenticated.");
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
  
  /**
   * Print the name of the current customer.
   */
  public void printCustomerName() {
    System.out.println("Name: " + this.currentCustomer.getName());
  }
  
  /**
   * Print the address of the current customer.
   */
  public void printCustomerAddress() {
    System.out.println("Address: " + this.currentCustomer.getAddress());  
  }
  
  /**
   * Print the accounts and their details of the current customer.
   */
  public void printCustomerAccounts() {
    List<Account> accounts = this.currentCustomer.getAccounts();
    // ensure there is at least one account
    if (accounts != null) {
      for (Account currAccount : accounts) {
        System.out.println(currAccount.toString());
      }
    } else {
      System.out.println("This Customer has no accounts.");
    }
  }
  
  /**
 * @return 
 * @throws ConnectionFailedException 
   * 
   */
  public BigDecimal getTotalBalance(User user) throws ConnectionFailedException {
	List<Account> userAccounts = this.listCustomerAccounts();
	BigDecimal totalBalance = null;
	totalBalance = BigDecimal.valueOf(0);
	
	
	if (userAccounts != null) {
		for (Account currAccount: userAccounts) {
			totalBalance.add(currAccount.getBalance());
			
	  }
	} else {
		System.out.println("This Customer has no accounts");
	}
   return totalBalance;  
  }
}