package com.bank.machines;

import com.bank.accounts.Account;
import com.bank.accounts.ChequingAccount;
import com.bank.accounts.SavingsAccount;
import com.bank.accounts.TaxFreeSavingsAccount;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.users.Customer;
import com.bank.users.Teller;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

public class TellerTerminal extends AutomatedTellerMachine {
  private Teller currentUser = null;
  private boolean currentUserAuthenticated = false;
  private Customer currentCustomer = null;
  private boolean currentCustomerAuthenticated = false;
  
  /**
   * Initialize a TellerTerminal with a teller. Must pass authentication on the first try.
   * @param tellerId The id of the teller.
   * @param password The password of the teller. 
   * @throws ConnectionFailedException If the database was not successfully connected to.
   */
  public TellerTerminal(int tellerId, String password) throws ConnectionFailedException {
    // call the parent constructor and create a null customer
    super(-1, null);
    // create a Customer object from the information in the database
    this.currentUser = (Teller) DatabaseSelectHelper.getUserDetails(tellerId);
    // ensure the customer has the correct password
    this.currentUserAuthenticated = currentUser.authenticate(password);
  }
  
  @Override
  public boolean authenticate(int userId, String password) {
    // ensures this method does nothing anymore
    return false;
  }
  
  @Override
  public List<Account> listAccounts() throws ConnectionFailedException {
    // ensure the current customer is authenticated
    if (!this.currentCustomerAuthenticated || !this.currentUserAuthenticated) {
      System.out.println("The current Customer or Teller is not authenticated.");
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
  
  @Override
  public boolean makeDeposit(BigDecimal amount, int accountId) throws ConnectionFailedException, 
      IllegalAmountException {
    // check if the user is authenticated
    if (!this.currentUserAuthenticated || !this.currentCustomerAuthenticated) {
      System.out.println("The current Customer or Teller is not authenticated.");
      return false;
    // check if the user has access to the account
    } else if (!DatabaseSelectHelper.getAccountIds(
        this.currentCustomer.getId()).contains(accountId)) {
      System.out.println("The customer does not have access to this account.");
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
  
  @Override
  public BigDecimal checkBalance(int accountId) throws ConnectionFailedException {
    // check if the user is authenticated
    if (!this.currentUserAuthenticated || !this.currentCustomerAuthenticated) {
      System.out.println("The Customer or Teller is not Authenticated.");
      return null;
    // check if the user has access to the account
    } else if (!DatabaseSelectHelper.getAccountIds(
        this.currentCustomer.getId()).contains(accountId)) { 
      System.out.println("The customer does not have access to this account.");
      return null;
    } else {
      // get the balance of the account
      return DatabaseSelectHelper.getBalance(accountId);
    }   
  }
  
  @Override
  public boolean makeWithdrawal(BigDecimal amount, int accountId) throws ConnectionFailedException, 
      IllegalAmountException, InsufficientFundsException {
    // check if the user is authenticated
    if (!this.currentUserAuthenticated || !this.currentCustomerAuthenticated) {
      System.out.println("The Cutomer or Teller is not Authenticated.");
      return false;
    } else if (!DatabaseSelectHelper.getAccountIds(
        this.currentCustomer.getId()).contains(accountId)) {
      System.out.println("The customer does not have access to this account.");
      return false;
    // check if the amount given is valid
    } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalAmountException("The amount given is invalid");
    // check if the account has enough money to be withdrawn
    } else if (
        DatabaseSelectHelper.getBalance(accountId).subtract(amount).compareTo(BigDecimal.ZERO) 
        < 1) {
      throw new InsufficientFundsException("You do not have enough funds to withdraw that amount.");
    } else {
      return DatabaseUpdateHelper.updateAccountBalance(
        DatabaseSelectHelper.getBalance(accountId).subtract(amount), accountId);
    }
  }
  
  /**
   * Make a new Account with the given details, put it in the database, and register it to the 
   * current Customer. Parameters must be valid 
   * or the Account will not be made.
   * @param name Name of the Account.
   * @param balance Balance of the Account.
   * @param type Integer representation of the type of Account.
   * @return true if the Account was successfully made and put in the database, false otherwise
   * @throws ConnectionFailedException If the database was not successfully connected to.
   */
  public boolean makeNewAccount(String name, BigDecimal balance, int type) throws 
        ConnectionFailedException {
    // set that the account was made and registered as false by default
    boolean createdAndRegistered = false;
    // ensure the user and customer are authenticated
    if (this.currentUserAuthenticated && this.currentCustomerAuthenticated 
        && currentCustomer != null) {
      // try to make an account with the given info
      int accountId = DatabaseInsertHelper.insertAccount(name, balance, type);
      // ensure the account was made
      if (accountId != -1) {
        // register the Account to the current Customer
        createdAndRegistered = DatabaseInsertHelper.insertUserAccount(this.currentCustomer.getId(), 
            accountId);
      }
    }
    // return whether the account was created and registered to the current Customer
    return createdAndRegistered;
  }
  
  /**
   * Set the current Customer. Sets that the current Customer is authenticated as false.
   * @param customer The customer to be set as the current Customer.
   */
  public void setCurrentCustomer(Customer customer) {
    this.currentCustomer = customer;
    this.currentCustomerAuthenticated = false;
  }
  
  /**
   * Try to Authenticate the current Customer.
   * @param password The possible password of the Customer.
   * @throws ConnectionFailedException If the database was not successfully connected to.
   */
  public void authenticateCurrentCustomer(String password) throws ConnectionFailedException {
    if (this.currentCustomer != null) {
      // try to authenticated the current customer
      this.currentCustomerAuthenticated = currentCustomer.authenticate(password);
      if (this.currentCustomerAuthenticated) {
        System.out.println("Customer was succesfully authenticated.");
      } else {
        System.out.println("Customer was not successfully authenticated.");
      }
    }
  }
  
  /**
   * Make a new Customer in the database with the given details. Details must be valid or the 
   * Customer will not be made.
   * @param name The name of the User.
   * @param age The age of the User.
   * @param address The address of the User.
   * @param password The password of the user.
   * @throws ConnectionFailedException If the database was not successfully connected to.
   */
  public void makeNewUser(String name, int age, String address, String password) 
      throws ConnectionFailedException {
    // check that the Teller is authenticated
    if (this.currentUserAuthenticated) {
      // get the role Ids in the database
      List<Integer> roleIds = DatabaseSelectHelper.getAccountTypesIds();
      // loop through each id, and if it corresponds to customer, create a new customer with the
      // given parameters, and add it to the database
      for (Integer roleId : roleIds) {
        // check if the current id is that of the Customer Account Type
        if (DatabaseSelectHelper.getRole(roleId).equals("CUSTOMER")) {
          // insert the account into the database
          int id = DatabaseInsertHelper.insertNewUser(name, age, address, roleId, password);
          // check if it was successful 
          if (id != -1) {
            System.out.println("Customer was successfully added with ID: " + String.valueOf(id));
          } else {
            System.out.println("Customer was not successfully added.");
          }
          break;
        }
      }
    } else {
      System.out.println("The Teller is not authenticated.");
    }
  }
  
  /**
   * Give interest to an account belonging to the current Customer.
   * @param account The id of the account.
   * @throws ConnectionFailedException If the database was not successfully connected to.
   */
  public void giveInterest(int account) throws ConnectionFailedException {
    // ensure the Customer and Teller are authenticated
    if (this.currentCustomerAuthenticated && this.currentUserAuthenticated) {
      // ensure the account belongs to the CurrentCustomer
      if  (DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId()).contains(account)) {
        // create an account from the given id
        Account accountVar = DatabaseSelectHelper.getAccountDetails(account);
        // check what type accountVar is and add interest to the account
        if (accountVar instanceof ChequingAccount) {
          ((ChequingAccount) accountVar).addInterest();
        } else if (accountVar instanceof SavingsAccount) {
          ((SavingsAccount) accountVar).addInterest();
        } else if (accountVar instanceof TaxFreeSavingsAccount) {
          ((TaxFreeSavingsAccount) accountVar).addInterest();
        }
        System.out.println("Interest was added. New balance: " 
            + DatabaseSelectHelper.getBalance(account).toString());
      } else {
        System.out.println("The Customer does not have access to this account.");
      }
    } else {
      System.out.println("The Customer or Teller is not authenticated.");
    }
  }
  
  /**
   * Set the current Customer as null, and that the current Customer is authenticated as false.
   */
  public void deAuthenticateCustomer() {
    this.currentCustomer = null;
    this.currentCustomerAuthenticated = false;
  }
  
  
}
