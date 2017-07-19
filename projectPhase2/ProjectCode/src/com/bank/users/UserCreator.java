package com.bank.users;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.generics.Roles;


public abstract class UserCreator {
  
  /**
   * Create a User with the given info.
   * @param id The id of the User.
   * @param name The name of the User.
   * @param age The age of the User.
   * @param address The address of the User.
   * @return The User created, or null if the given type is invalid.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  public static User makeUser(int id, String name, int age, String address) 
      throws ConnectionFailedException {
    String type = DatabaseSelectHelper.getRole(id);
    if (type.equals(Roles.ADMIN)) {
      return new Admin(id, name, age, address);
    } else if (type.equals(Roles.CUSTOMER)) {
      return new Customer(id, name, age, address);
    } else if (type.equals(Roles.TELLER)) {
      return new Teller(id, name, age, address);
    } else {
      return null;
    }
  }

}
