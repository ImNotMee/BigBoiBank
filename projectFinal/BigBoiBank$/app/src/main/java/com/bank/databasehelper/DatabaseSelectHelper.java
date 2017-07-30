package com.bank.databasehelper;

import android.content.Context;
import android.database.Cursor;

import com.bank.accounts.Account;
import com.bank.accounts.AccountCreator;
import com.bank.users.User;
import com.bank.users.UserCreator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



public class DatabaseSelectHelper {


  private DatabaseDriverAExtender driverExtender;
  private Context context;

  public DatabaseSelectHelper(Context context) {
    this.context = context;
    driverExtender = new DatabaseDriverAExtender(this.context);
  }
  /**
   * Get the role with id id.
   * @param id The id of the role
   * @return a String containing the role.
   * @If database was not successfully connected to.
   */
  public String getRole(int id) {
    // if the id given is an invalid number
    if (id < 1) {
      return "Id given is invalid";
    }
    return driverExtender.getRole(id);
  }
   
  /**
   * Get the hashed version of the password.
   * @param userId The user's id.
   * @return The hashed password to be checked against given password.
   * @If database was not successfully connected to.
   */
  public String getPassword(int userId) {
    // if the userId given is an invalid number 
    if (userId < 1) {
      return "UserId given is invalid";
    }
    return driverExtender.getPassword(userId);

  }
  
  /**
   * Find all the details about a given user.
   * @param userId The id of the user.
   * @return a User with the details of the user.
   * @If database was not successfully connected to.
   */
  public User getUserDetails(int userId) {
    // if the userId given is an invalid number 
    if (userId < 1) {
      return null;
    }
    // try to get the User details of the given userId
    Cursor cursor = driverExtender.getUserDetails(userId);
    if (cursor.moveToFirst()) {
      String role = getRole(cursor.getInt(cursor.getColumnIndex("ROLEID")));
      // try to create the new user
      User user = UserCreator.makeUser(userId, cursor.getString(cursor.getColumnIndex("NAME")),
              cursor.getInt(cursor.getColumnIndex("AGE")),
              cursor.getString(cursor.getColumnIndex("ADDRESS")), role, this.context);
      cursor.close();
      return user;
    } else {
      return null;
    }
  }
 
  /**
   * Return the id's of all of a user's accounts.
   * @param userId The id of the user.
   * @return A List of Integer containing all the Id's of the accounts of the user.
   * @If database was not successfully connected to.
   */
  public List<Integer> getAccountIds(int userId) {
    // if the userId given is an invalid number 
    if (userId < 1) {
      return null;
    }
    List<Integer> accountIds = new ArrayList<>();
    // try to get the accounts of the given Id
    Cursor cursor = driverExtender.getAccountIds(userId);
    // loop through each available row in the results
    while (cursor.moveToNext()) {
      // add the accountId to the list of accountIds
      accountIds.add(cursor.getInt(cursor.getColumnIndex("ACCOUNTID")));
    }
    cursor.close();
    return accountIds;
  }
  
  /**
   * Return the full details of an account in an Account.
   * @param accountId The id of the account.
   * @return An Account with the details of the accountId.
   * @If database was not successfully connected to.
   */
  public Account getAccountDetails(int accountId) {
    // if the accountId given is an invalid number 
    if (accountId < 1) {
      return null;
    }
    // try to get the Account details of the given accountId
    Cursor cursor = driverExtender.getAccountDetails(accountId);
    if (cursor.moveToFirst()) {
      String type = getAccountTypeName(cursor.getInt(cursor.getColumnIndex("TYPE")));
      // try to create the new Account
      Account account = AccountCreator.createAccount(accountId,
              cursor.getString(cursor.getColumnIndex("NAME")),
              new BigDecimal(cursor.getString(cursor.getColumnIndex("BALANCE"))), type, this.context);
      cursor.close();
      return account;
    } else {
      return null;
    }

  }
 
  /**
   * Return the balance in the account.
   * @param accountId the account to check.
   * @return the balance
   * @If database was not successfully connected to.
   */
  public BigDecimal getBalance(int accountId) {
    // if the accountId given is an invalid number 
    if (accountId < 1) {
      return null;
    }
    return driverExtender.getBalance(accountId);
  }

  
  /**
   * Get the interest rate for an account.
   * @param accountType The type for the account.
   * @return The interest rate.
   * @If database was not successfully connected to.
   */
  public BigDecimal getInterestRate(int accountType) {
    // if the accountType given is an invalid number 
    if (accountType < 1) {
      return null;
    }
    return driverExtender.getInterestRate(accountType);
  }
  
  /**
   * Return a List of Integer representing the type of Account in the AccountTypes table.
   * @return a List of Integer representing the type of Account in the AccountTypes table.
   * @If database was not successfully connected to.
   */
  public List<Integer> getAccountTypesIds() {
    // create an empty array of AccountTypeIds
    List<Integer> accountTypeIds = new ArrayList<>();
    Cursor cursor = driverExtender.getAccountTypesId();
    // add each id to the list of ids
    while (cursor.moveToNext()) {
      accountTypeIds.add(cursor.getInt(cursor.getColumnIndex("ID")));
    }
    cursor.close();
    return accountTypeIds;
  }
  
  /**
   * Return the accounttype name given an accountTypeId.
   * @param accountTypeId The id of the account type.
   * @return The name of the account type.
   * @If database was not successfully connected to.
   */
  public String getAccountTypeName(int accountTypeId) {
    // if the accountType given is an invalid number 
    if (accountTypeId < 1) {
      return "accountTypeId given is invalid";
    }
    return driverExtender.getAccountTypeName(accountTypeId);
  }
  
  /**
   * Get a List of Integer representing all the roles.
   * @return a List of Integer representing all the roles.
   * @If database was not successfully connected to.
   */
  public List<Integer> getRoles() {
    // create an empty array of roleIds
    List<Integer> roleIds = new ArrayList<>();
    Cursor cursor = driverExtender.getRoles();
    // add each role id to the roleIds
    while (cursor.moveToNext()) {
      roleIds.add(cursor.getInt(cursor.getColumnIndex("ID")));
    }
    cursor.close();
    return roleIds;
  }

  /**
   * Get the typeId of the account.
   * @param accountId The accounts id.
   * @return the typeId  of the Account.
   * @If database was not successfully connected to.
   */
  public int getAccountType(int accountId) {
    // ensure the accountId given is a valid number 
    if (accountId < 1) {
      return -1;
    } 
    return driverExtender.getAccountType(accountId);
  }
  
  /**
   * Get the role of the given user.
   * @param userId The id of the user.
   * @return the roleId for the user.
   * @If database was not successfully connected to.
   */
  public int getUserRole(int userId) {
    // ensure the userId given is a valid number 
    if (userId < 1) {
      return -1;
    } 
    return driverExtender.getUserRole(userId);
  }
  
  /**
   * Get all messageIds for the user with the given id.
   * @param userId the id of user who's messages we are getting.
   * @return A list of all the messages.
   * @If the database can not be connected to.
   */
  public List<Integer> getMessageIds(int userId) {
    if (userId < 1) {
      return null;
    }
    ArrayList<Integer> messageIds = new ArrayList<>();
    Cursor cursor = driverExtender.getAllMessages(userId);
    while (cursor.moveToNext()) {
      messageIds.add(new Integer(cursor.getInt(cursor.getColumnIndex("ID"))));
    }
    cursor.close();
    return messageIds;
  }
  
  /**
   * Get a message from the database.
   * @param messageId The id of the message to get.
   * @return The message.
   * @If the database can not be connected to.
   */
  public String getSpecificMessage(int messageId) {
    if (messageId < 1) {
      return null;
    }
    String message;
    try {
      message = driverExtender.getSpecificMessage(messageId);
    } catch (Exception e) {
      message = null;
    }
    return message;
  }
  
  /**
   * Gets the user who owns the account given.
   * @param accountId the account of the user we want to find.
   * @return -1 if we could not find it, else we return the userId who owns this account.
   */
  public int getUserFromAccount(int accountId) {
    if (accountId < 1) {
      return -1;
    }
    int userId;
    int currId = 1;
    User user = getUserDetails(currId);
    while (user != null) {
      userId = user.getId();
      List<Integer> accounts = getAccountIds(userId);
      // if we find that this user owns this account then return the id of the user we found
      if (accounts.contains(accountId)){
        return userId;
      }
      // increment to continue to the next user
      currId ++;
      user = getUserDetails(currId);
    }
    return -1;
  }
 }
