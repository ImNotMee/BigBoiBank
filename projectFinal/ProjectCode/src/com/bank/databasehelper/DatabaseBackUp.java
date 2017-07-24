package com.bank.databasehelper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.bank.accounts.Account;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.users.User;

public class DatabaseBackUp implements Serializable {

  private ArrayList<String> userNames;
  private ArrayList<String> addresses;
  private ArrayList<Integer> ages;
  private ArrayList<Integer> roleIds;
  private ArrayList<String> passwords;
  private ArrayList<Integer> accountTypes;
  private ArrayList<String> accountTypeNames;
  private ArrayList<String> accountNames;
  private ArrayList<BigDecimal> accountInterestRates;
  private ArrayList<BigDecimal> accountBalances;
  private ArrayList<String> messages;
  private ArrayList<Integer> messageStatuses;
  
  /**
   * Constructor that initializes all the arrayLists.
   */
  public DatabaseBackUp() {
    this.userNames = new ArrayList<>();
    this.addresses = new ArrayList<>();
    this.ages = new ArrayList<>();
    this.roleIds = new ArrayList<>();
    this.passwords = new ArrayList<>();
    this.accountTypes = new ArrayList<>();
    this.accountTypeNames = new ArrayList<>();
    this.accountNames = new ArrayList<>();
    this.accountInterestRates = new ArrayList<>();
    this.accountBalances = new ArrayList<>();
    this.messages = new ArrayList<>();
    this.messageStatuses = new ArrayList<>();
    
  }
  /**
   * Goes through the whole database and makes a copy of it in this object.
   * @return true if we updated and false otherwise.
   */
  public boolean update() {
    try {
      // First we'll get all the data about the users
      int currUserId = 1;
      User currUser = DatabaseSelectHelper.getUserDetails(currUserId);
      while (currUser != null) {
        // add the data to our lists
        this.userNames.add(currUser.getName());
        this.addresses.add(currUser.getAddress());
        this.ages.add((Integer) currUser.getAge());
        this.roleIds.add((Integer) currUser.getRoleId());
        this.passwords.add(DatabaseSelectHelper.getPassword(currUserId));
        // now that we've entered all the data about the currUser, we can go on to the next user
        currUserId ++;
        currUser = DatabaseSelectHelper.getUserDetails(currUserId);   
      }
      
      // we have all the data for users but now we need the data for accounts
      int currAccountId = 1;
      Account currAccount = DatabaseSelectHelper.getAccountDetails(currAccountId);
      while (currAccount != null) {
        // add the data about the accounts to our lists
        this.accountTypes.add((Integer) currAccount.getType());
        this.accountTypeNames.add(DatabaseSelectHelper.getAccountTypeName(currAccount.getType()));
        this.accountNames.add(currAccount.getName());
        this.accountBalances.add(DatabaseSelectHelper.getBalance(currAccountId));
        this.accountInterestRates.add(DatabaseSelectHelper.getInterestRate(currAccount.getType()));
        // we have the data for the specific account, now do that increment it
        currAccountId ++;
        currAccount = DatabaseSelectHelper.getAccountDetails(currAccountId);
      }
      
    } catch (ConnectionFailedException e) {
      System.out.println("Failed to connect to Database");
    }
    return false;
  }
}
