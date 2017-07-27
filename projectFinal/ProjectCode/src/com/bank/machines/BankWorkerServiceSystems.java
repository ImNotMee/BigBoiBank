package com.bank.machines;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.generics.RolesEnumMap;
import com.bank.security.PasswordHelpers;
import com.bank.users.Customer;
import com.bank.users.User;

import java.math.BigDecimal;
import java.util.List;

public abstract class BankWorkerServiceSystems extends BankServiceSystems {
  
  protected User currentUser;
  protected boolean currentUserAuthenticated;
  
  /**
   * Used to Authenticate the currentCustomer 
   * @param password The possible password of the User.
   * @return true if the password was correct and the User is authenticated, false otherwise
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  @Override
  public boolean authenticateCurrentCustomer(String password) throws ConnectionFailedException {
    this.currentCustomerAuthenticated = currentCustomer.authenticate(password);
    if (this.currentCustomerAuthenticated) {
      // print out the details of the customer without the accounts
      System.out.println("Customer successfully authenticated.");
      this.printCustomerName();
      this.printCustomerAddress();
    }
    return this.currentCustomerAuthenticated;
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
      // give the customer the account
      currentCustomer.addAccount(DatabaseSelectHelper.getAccountDetails(accountId));
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
   * Make a new Customer in the database with the given details. Details must be valid or the 
   * Customer will not be made.
   * @param name The name of the User.
   * @param age The age of the User.
   * @param address The address of the User.
   * @param password The password of the user.
   * @throws ConnectionFailedException If the database was not successfully connected to.
   */
  public int makeNewCustomer(String name, int age, String address, String password) 
      throws ConnectionFailedException {
    // check that the Teller is authenticated
    if (this.currentUserAuthenticated) {
      // use the enumMap to find the id of CUSTOMER
      RolesEnumMap map = new RolesEnumMap();
      int roleId = map.getRoleId("CUSTOMER");
      return DatabaseInsertHelper.insertNewUser(name, age, address, roleId, password);
    } else {
      System.out.println("The Teller is not authenticated.");
      return -1;
    }
  }
  
  /**
   * Give interest to an account belonging to the current Customer.
   * @param account The id of the account.
   * @throws ConnectionFailedException If the database was not successfully connected to.
   */
  public boolean giveInterest(int account) throws ConnectionFailedException {
    // ensure the Customer and Teller are authenticated
    if (this.currentCustomerAuthenticated && this.currentUserAuthenticated) {
      // ensure the account belongs to the CurrentCustomer
      if  (DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId()).contains(account)) {
        // create an account from the given id
        Account accountVar = DatabaseSelectHelper.getAccountDetails(account);
        // check what type accountVar is and add interest to the account
        if (accountVar != null) {
          accountVar.addInterest();
        }
        int userId = DatabaseSelectHelper.getUserFromAccount(account);
        this.leaveMessage("Interest has been added to your account with ID " 
            + String.valueOf(account), userId);
        return true;
      } else {
        System.out.println("The Customer does not have access to this account.");      
      }
    } else {
      System.out.println("The Customer or Teller is not authenticated.");
    }
    return false;
  }
  
  /**
   * Set the current Customer as null, and that the current Customer is authenticated as false.
   */
  public void deAuthenticateCustomer() {
    this.currentCustomer = null;
    this.currentCustomerAuthenticated = false;
  }
  
  /**
   * Get the total amount of money in all of a customer's account.
   * @return The total amount in all of the user's accounts.
   * @throws ConnectionFailedException If the database can not be connected to.
   * 
   */
  public BigDecimal getTotalBalance(int userId) throws ConnectionFailedException {
    BigDecimal totalBalance = BigDecimal.ZERO;
    if (this.currentUserAuthenticated) {
      User user = DatabaseSelectHelper.getUserDetails(userId);
      if (user != null) {
        List<Account> userAccounts = user.getAccounts();
        if (userAccounts != null) {
          for (Account currAccount: userAccounts) {     
            totalBalance = totalBalance.add(currAccount.getBalance());
          }
        } else {
          System.out.println("This Customer has no accounts");
        }
        // check if there should be a message left for the customer who's accounts are being viewed
        if (this instanceof AdminTerminal) {
          if (this.currentCustomer == null || !(this.currentCustomer.getId() == userId)) {
            this.leaveMessage("An Admin has viewed the balance on your account while you were not"
                + " logged in.", userId);
          }
        }
      }
      
    } else {
      System.out.println("The teller/admin is not authenticated.");
    }
    return totalBalance;  

  }
  
  /**
   * Update the current user's name in the database.
   * @param name The new name of the User.
   * @return Whether the update was successful.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  public boolean updateUserName(String name) throws ConnectionFailedException {
    if (this.currentCustomer != null && this.currentCustomerAuthenticated && 
        this.currentUserAuthenticated) {
      return DatabaseUpdateHelper.updateUserName(name, this.currentCustomer.getId());
    } else {
      System.out.println("The current customer is not set or authenticated.");
      return false;
    }
  }
  
  /**
   * Update a user's address in the database.
   * @param address The new address of the User.
   * @param id The id of the User.
   * @return Whether the update was successful.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  public boolean updateUserAddress(String address) throws ConnectionFailedException {
    if (this.currentCustomer != null && this.currentCustomerAuthenticated && 
        this.currentUserAuthenticated) {
      return DatabaseUpdateHelper.updateUserAddress(address, this.currentCustomer.getId());
    } else {
      System.out.println("The current customer is not set or authenticated.");
      return false;
    }
  }
  
  /**
   * Update a user's age in the database.
   * @param age The new age of the User.
   * @param id The id of the User.
   * @return Whether the update was successful.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  public boolean updateUserAge(int age) throws ConnectionFailedException {
    if (this.currentCustomer != null && this.currentCustomerAuthenticated && 
        this.currentUserAuthenticated) {
      return DatabaseUpdateHelper.updateUserAge(age, this.currentCustomer.getId());
    } else {
      System.out.println("The current customer is not set or authenticated.");
      return false;
    }
  }
  
  /**
   * Update a user's password, to a new HASHED password.
   * @param password The unhashed version of a password
   * @param id The id of the user who's password is to be update..
   * @return True if the password was successfully updated.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  public boolean updateUserPassword(String password) throws ConnectionFailedException {
    if (this.currentCustomer != null && this.currentCustomerAuthenticated && 
        this.currentUserAuthenticated) {
    return DatabaseUpdateHelper.updateUserPassword(PasswordHelpers.passwordHash(password), 
        this.currentCustomer.getId());  
    } else {
      System.out.println("The current customer is not set or authenticated.");
      return false;
    }
  }
  
  /**
   * Get the ids of messages the user can see.
   * @return The ids of the messages the user can see.
   */
  public abstract List<Integer> getUserMessageIds() throws ConnectionFailedException;
  
  /**
   * Leave a new message for a user.
   * @param message The message for the user.
   * @param userId The id of the user who the message is for.
   * @return The id of the message, or -1 if it was unsuccessful.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  public int leaveMessage(String message, int userId) throws ConnectionFailedException {
    if (this.currentUserAuthenticated) {
      return DatabaseInsertHelper.insertMessage(userId, message);
    } else {
      return -1;
    }
  }
}
