package com.bank.databasehelper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bank.accounts.Account;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.users.User;

public class DatabaseBackUp implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -6186959507466860494L;
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
  private HashMap<Integer, ArrayList<Integer>> map;
  
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
    this.map = new HashMap<>();
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
        // we need to know the list of the userIds associated with any given message so
        // Use a hashmap so we have the key(userId) associated to a bunch of messages which we can 
        // decipher later when we deserialize the data
        // get all the messages associated about our given user using the helper
        ArrayList<Integer> messageIds = (ArrayList<Integer>) DatabaseSelectHelper.getMessageIds(currUserId);
        // add all the values to the key
        this.map.put((Integer) currUserId, messageIds);
        }
        // now that we've entered all the data about the currUser, we can go on to the next user
        currUserId ++;
        currUser = DatabaseSelectHelper.getUserDetails(currUserId);   
      
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
      // now get all the messages
      int currMessageId = 1;
      String message = DatabaseSelectHelper.getSpecificMessage(currMessageId);
      while (message != null) {
        // add the message to our list of messages
        this.messages.add(message);
        currMessageId ++;
        message = DatabaseSelectHelper.getSpecificMessage(currMessageId);
      }
    } catch (ConnectionFailedException e) {
      System.out.println("Failed to connect to Database");
    }
    return false;
  }
  
  /**
   * BELOW ARE ALL THE GETTERS FOR ALL THE VALUES IN THE DATABASE !------------------->
   */
  
  
  public ArrayList<String> getUserNames() {
    return this.userNames;
  }
  
  public ArrayList<String> getUserAddresses() {
    return this.addresses;
  }
  
  public ArrayList<Integer> getUserAges() {
    return this.ages;
  }
  
  public ArrayList<Integer> getUserRoleIds() {
    return this.roleIds;
  }
  
  /**
   * Returns the users hashed password.
   * @return the hashed password of the user.
   */
  public ArrayList<String> getUserPassword() {
    return this.passwords;
  }
  
  public ArrayList<Integer> getAccountTypes() {
    return this.accountTypes;
  }
  
  public ArrayList<String> getAccountTypeNames() {
    return this.accountTypeNames;
  }
  
  public ArrayList<String> getAccountNames() {
    return this.accountNames;
  }
  
  public ArrayList<BigDecimal> getAccountInterestRates() {
    return this.accountInterestRates;
  }
  
  public ArrayList<BigDecimal> getAccountBalances() {
    return this.accountBalances;
  }
  
  public ArrayList<String> getMessages() {
    return this.messages;
  }
  
  public HashMap<Integer, ArrayList<Integer>> getMessageRelationships() {
    return this.map;
  }
}
