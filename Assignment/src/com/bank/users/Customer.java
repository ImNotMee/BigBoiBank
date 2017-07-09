package com.bank.users;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;

import java.util.List;

public class Customer extends User {

  @SuppressWarnings("unused")
  private String address = "";
  @SuppressWarnings("unused")
  private int roleId = -1;
  @SuppressWarnings("unused")
  private boolean authenticated;
  private List<Account> accounts;
  
  /**
   * Initialize an Customer with an id, name, and address.
   * @param id A positive integer Id number. Must be positive or it will not be set. 
   * @param name The name of the Customer. Name must not be null or it will not be set.
   * @param age The age of the Customer. Must be a positive integer.
   * @param address The address of the Customer. Must not be null or it will not be set.
   * @throws ConnectionFailedException If connection can not be made to the database.
   */
  public Customer(int id, String name, int age, String address) throws ConnectionFailedException {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    if (address != null) {
      this.address = address;
    }
    // try to get the role id of the user
    this.roleId = DatabaseSelectHelper.getUserRole(this.getId());
  }
  
  /**
   * Initialize an Customer with an id, name, and address.
   * @param id A positive integer Id number. Must be positive or it will not be set. 
   * @param name The name of the Customer. Name must not be null or it will not be set.
   * @param age The age of the Customer. Must be a positive integer.
   * @param address The address of the Customer. Must not be null or it will not be set.
   * @param authenticated Whether the Customer is authenticated.
   * @throws ConnectionFailedException If connection can not be made to the database.
   */
  public Customer(int id, String name, int age, String address, boolean authenticated) 
      throws ConnectionFailedException {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    if (address != null) {
      this.address = address;
    }
    // try to get the role id of the user
    this.roleId = DatabaseSelectHelper.getUserRole(this.getId());
    this.authenticated = authenticated;
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
   * @throws ConnectionFailedException If connection can not be made to the database.
   */
  public void addAccount(Account account) throws ConnectionFailedException {
    // ensure the account is new and not null
    if (account != null && !this.accounts.contains(account)) {
      this.accounts.add(account);
      // add the user account in the database
      DatabaseInsertHelper.insertUserAccount(this.getId(), account.getId());
    }
  }
}
