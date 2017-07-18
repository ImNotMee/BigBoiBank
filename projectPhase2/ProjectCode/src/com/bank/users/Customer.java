package com.bank.users;

import com.bank.exceptions.ConnectionFailedException;

public class Customer extends User {
  
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
      this.setAddress(address);
    }
    // try to get the role id of the user
    this.setRoleId(this.enumMap.getRoleId("CUSTOMER"));
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
      this.setAddress(address);
    }
    // try to get the role id of the user
    this.setRoleId(this.enumMap.getRoleId("CUSTOMER"));
    this.authenticated = authenticated;
    this.setRoleId(this.enumMap.getRoleId("CUSTOMER"));

  }

}
