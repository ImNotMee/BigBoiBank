package com.bank.users;

import android.content.Context;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;

import java.util.List;

public class Admin extends User {
  
  /**
   * Initialize an Admin with an id, name and address.
   * @param id A positive integer Id number. Must be positive or it will not be set. 
   * @param name The name of the Admin. Name must not be null or it will not be set.
   * @param age The age of the Admin. Must be a positive integer.
   * @param address The address of the Admin. Must not be null or it will not be set.
   */
  public Admin(int id, String name, int age, String address, Context context) {
    insertor = new DatabaseInsertHelper(context);
    selector = new DatabaseSelectHelper(context);
    updater = new DatabaseUpdateHelper(context);
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    if (address != null) {
      this.setAddress(address);
    }
    this.setRoleId(this.enumMap.getRoleId("ADMIN"));
    List<Integer> accountIds = selector.getAccountIds(this.getId());
    // add each account to the users account
    for (Integer accountId : accountIds) {
      this.addAccount(selector.getAccountDetails(accountId));
    }
  }
  
  /**
   * Initialize an Admin with an id, name, address, and whether they are authenticated.
   * @param id A positive integer Id number. Must be positive or it will not be set. 
   * @param name The name of the Admin. Name must not be null or it will not be set.
   * @param age The age of the Admin. Must be a positive integer.
   * @param address The address of the Admin. Must not be null or it will not be set.
   * @param authenticated Whether the Admin is authenticated.
   * @throws ConnectionFailedException If connection can not be made to the database.
   */
  public Admin(int id, String name, int age, String address, boolean authenticated, Context context) {
    insertor = new DatabaseInsertHelper(context);
    selector = new DatabaseSelectHelper(context);
    updater = new DatabaseUpdateHelper(context);
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    if (address != null) {
      this.setAddress(address);
    }
    this.authenticated = authenticated;
    // tries to get the role id from the database
    this.setRoleId(this.enumMap.getRoleId("ADMIN"));
  }
}
