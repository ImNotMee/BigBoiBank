package com.bank.users;

import android.content.Context;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.generics.RolesEnumMap;

import java.util.List;

public class Teller extends User {
  
  /**
   * Initialize a Teller with an id, name, and address.
   * @param id A positive integer Id number. Must be positive or it will not be set. 
   * @param name The name of the Teller. Name must not be null or it will not be set.
   * @param age The age of the Teller. Must be a positive integer.
   * @param address The address of the Teller. Must not be null or it will not be set.
   */
  public Teller(int id, String name, int age, String address, Context context) {
    this.insertor = new DatabaseInsertHelper(context);
    this.selector = new DatabaseSelectHelper(context);
    this.updater = new DatabaseUpdateHelper(context);
    this.enumMap = new RolesEnumMap(context);
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    if (address != null) {
      this.setAddress(address);
    }
    this.setRoleId(this.enumMap.getRoleId("TELLER"));
    List<Integer> accountIds = selector.getAccountIds(this.getId());
    // add each account to the users account
    for (Integer accountId : accountIds) {
      this.addAccount(selector.getAccountDetails(accountId));
    }
  }
  
  /**
   * Initialize a Teller with an id, name, and address.
   * @param id A positive integer Id number. Must be positive or it will not be set. 
   * @param name The name of the Teller. Name must not be null or it will not be set.
   * @param age The age of the Teller. Must be a positive integer.
   * @param address The address of the Teller. Must not be null or it will not be set.
   * @param authenticated Whether the Teller is authenticated.
   */
  public Teller(int id, String name, int age, String address, boolean authenticated, Context context) {
    this.insertor = new DatabaseInsertHelper(context);
    this.selector = new DatabaseSelectHelper(context);
    this.updater = new DatabaseUpdateHelper(context);
    this.enumMap = new RolesEnumMap(context);
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    if (address != null) {
      this.setAddress(address);
    }
    // try to get the role id of the User
    this.setRoleId(this.enumMap.getRoleId("TELLER"));
    this.authenticated = authenticated;
  }
}
