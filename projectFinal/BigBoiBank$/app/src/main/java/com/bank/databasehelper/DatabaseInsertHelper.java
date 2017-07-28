package com.bank.databasehelper;

import android.content.Context;


import com.bank.generics.AccountTypesContains;
import com.bank.generics.RolesContains;

import java.math.BigDecimal;
import java.util.List;


public class DatabaseInsertHelper {

  private DatabaseDriverAExtender driverExtender;
  private DatabaseSelectHelper selector;

  public DatabaseInsertHelper(Context context) {
    driverExtender = new DatabaseDriverAExtender(context);
    selector = new DatabaseSelectHelper(context);
  }

  /**
   * Insert a new account into the Account table.
   * @param name the name of the account.
   * @param balance the balance currently in account.
   * @param typeId the id of the type of the account.
   * @return accountId of inserted account, -1 otherwise
   */
  public int insertAccount(String name, BigDecimal balance, int typeId) {
    // gets the the id's of the types of accounts in the database
    List<Integer> accountTypeIds = selector.getAccountTypesIds();
    // ensure the name, balance, and typeId are valid
    if (name != null && name.length() > 0 && balance != null
            && accountTypeIds.contains(typeId)) {
      // set the balance to have two decimal places
      balance = balance.setScale(2, BigDecimal.ROUND_HALF_UP);
      return (int) driverExtender.insertAccount(name, balance, typeId);
      // return -1 if the given parameters were not valid
    } else {
      return -1;
    }
  }
  
  /**
   * Insert an accountType into the accountType table.
   * @param name The name of the type of account. name must be either chequing, saving, or tfsa 
   *        (case insensitive) or it will not be added.
   * @param interestRate The interest rate for this type of account. 0 <= interestRate < 1 or it 
   *        will not be added.
   * @return True if successful, false otherwise.
   */
  public int insertAccountType(String name, BigDecimal interestRate) {
    if (interestRate.compareTo(BigDecimal.ONE) < 0 && interestRate.compareTo(BigDecimal.ZERO) >= 0
            && AccountTypesContains.contains(name)) {
      // check if the account type is already in the database
      List<Integer> accountIds = selector.getAccountTypesIds();
      // variable to check if this account name is unique
      boolean unique = true;
      // loop through each account type and if the Account exists, set that it is not unique
      for (Integer id : accountIds) {
        if (driverExtender.getRole(id).equals(name.toUpperCase())) {
          unique = false;
        }
      }
      // try to add a new account type to the database if its not there, seeing if it worked
      if (unique) {
        return (int) driverExtender.insertAccountType(name.toUpperCase(), interestRate);
      }
    }
    return -1;
  }
  
  /**
   * Insert a new user into the Users table, if all given info is valid.
   * @param name The name of the user.
   * @param age The age of the user.
   * @param address The address of the user. 
   * @param roleId The id of the role of the user.
   * @param password The password of the user.
   * @return The id of the user if added successfully and -1 otherwise.
   */
  public int insertNewUser(String name, int age, String address, int roleId, String password) {
    // get the id's of the available roles in the database
    List<Integer> roleIds = selector.getRoles();
    // ensure the name, age, address, roleId, and password are all valid
    if (name != null && name.length() > 0 && age > 0 && address != null && address.length() <= 100
            && roleIds.contains(roleId) && password != null) {
      return (int) driverExtender.insertNewUser(name, age, address, roleId, password);
    } else {
      return -1;
    }
  }
  
  /**
   * Insert a Role into the Roles table.
   * @param role The name of the type of Role. name must be either admin, teller, or customer (case
   *        insensitive) or it will not be added.
   * @return True if successful, false otherwise.
   */
  public int insertRole(String role) {
    // ensure the role is in the enum
    if (RolesContains.contains(role)) {
      // check if the role is already in the database
      List<Integer> roleIds = selector.getAccountTypesIds();
      // variable to check if this account name is unique
      boolean unique = true;
      // loop through each roleId and if the Account exists, set that it is not unique
      for (Integer id : roleIds) {
        if (driverExtender.getRole(id).equals(role.toUpperCase())) {
          unique = false;
        }
      }
      // try to add a new role to the database if its not there, seeing if it worked
      if (unique) {
        return (int) driverExtender.insertRole(role.toUpperCase());
      }
    }
    return -1;
  }
  
  /**
   * Insert an accountType into the accountType table.
   * @param userId The id of a User in the database.
   * @param accountId The id of an Account in the database.
   * @return True if successful, false otherwise.
   */
  public int insertUserAccount(int userId, int accountId) {
    if (!selector.getAccountIds(userId).contains(accountId)) {
      return (int) driverExtender.insertUserAccount(userId, accountId);
    }
    return -1;
  }
  
  /**
   * Insert a message into the database in the USERMESSAGE table.
   * @param userId the id of who the message is meant for.
   * @param message The message.
   * @return The id of the inserted message.
   */
  public int insertMessage(int userId, String message) {
    if (selector.getUserRole(userId) != -1 && message.length() <= 512) {
      // insert the message
      return (int) driverExtender.insertMessage(userId, message);
    }
    return -1;
  }
  
}
