package com.bank.users;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.generics.RolesEnumMap;
import com.bank.security.PasswordHelpers;

import java.util.ArrayList;
import java.util.List;

public abstract class User {
  private int id = -1;
  private String name = "";
  private int age = -1;
  private String address = "";
  private int roleId = -1;
  protected boolean authenticated = false;
  private List<Account> accounts = new ArrayList<>();
  protected RolesEnumMap enumMap;
  protected DatabaseSelectHelper selector;
  protected DatabaseInsertHelper insertor;
  protected DatabaseUpdateHelper updater;
  
  /**
   * Get the id of the User.
   * @return The id of the User.
   */
  public int getId() {
    return this.id;
  }
  
  /**
   * Set the id of the User.
   * @param id The id of the User.
   */
  public void setId(int id) {
    if (id > 0) {
      this.id = id;
    }
  }
  
  /**
   * Get the name of the User.
   * @return The name of the User.
   */
  public String getName() {
    return this.name;
  }
  
  /**
   * Set the name of the User. Name can not be null, and must have at least one character, or it 
   * will not be set.
   * @param name The name of the User.
   */
  public void setName(String name) {
    if (name != null && name.length() > 0) {
      // check if the name is being updated
      if (!this.name.equals("")) {
        // update the name in the database
        updater.updateUserName(name, this.id);
      }
      this.name = name;
    }
  }
  
  /**
   * Get the age of the User.
   * @return The age of the User.
   */
  public int getAge() {
    return this.age;
  }
  
  /**
   * Set the age of the User. Age must be a valid age.
   * @param age The age of the User.
   */
  public void setAge(int age) {
    if (age >= 0) {
      // check if the age is being updated
      if (this.age != -1) {
        // update the age in the database
        updater.updateUserAge(age, this.id);
      }
      this.age = age;
    }
  }
  
  /**
   * Get the RoleId of the User.
   * @return An int representing the RoleId of the User.
   */
  public int getRoleId() {
    return this.roleId;
  }
  
  /**
   * Set the RoleId of the User.
   * @param id An int representing the RoleId of the User.
   */
  public void setRoleId(int id) {
    this.roleId = id;
  }
  
  /**
   * Get the address of the User.
   * @return The address of the User.
   */
  public String getAddress() {
    return this.address;
  }
  
  /**
   * Set the name of the User. Name can not be null, and must have at least one character, or it 
   * will not be set.
   * @param address The address of the User.
   */
  public void setAddress(String address) {
    if (address != null && address.length() > 0) {
      // check if the name is being updated
      if (!this.address.equals("")) {
        // update the name in the database
        updater.updateUserName(name, this.id);
      }
      this.address = address;
    }
  }
  
  /**
   * Get all the Accounts this Customer has.
   * @return The Accounts of the Customer.
   */
  public List<Account> getAccounts() {
    return this.accounts;
  }
  
  /**
   * Add an Account to this customer.
   * @param account Account to be added. Account must not be null, and must not already be 
   *        be associated to this Customer.
   */
  public void addAccount(Account account) {
    // ensure the account is new and not null
    if (account != null && !this.accounts.contains(account)) {
      this.accounts.add(account);
      // add the user account in the database if it is not already there
      if (!selector.getAccountIds(this.getId()).contains(account.getId())) {
        insertor.insertUserAccount(this.getId(), account.getId());
      }
    }
  }
  
  /**
   * Validates if the given password matches the password found in the database for the given user.
   * @param password The password to check if it matches the user.
   * @return True iff the password matches the password in the database for the user, False 
   *         otherwise.
   */
  
  public final boolean authenticate(String password) {
    this.authenticated = PasswordHelpers.comparePassword(selector.getPassword(this.id),
        password);
    // Return whether the password given matches the password of the User
    return this.authenticated;
  }
  
  public String toString() {
    return "ID: " + String.valueOf(this.id) + "\nName: " + this.name + "\nAge: " 
        + String.valueOf(this.age) + "\nAddress: " + this.address;
  }
  
}
