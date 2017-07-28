package com.bank.users;

import android.content.Context;


public abstract class UserCreator {
  
  /**
   * Create a User with the given info.
   * @param id The id of the User.
   * @param name The name of the User.
   * @param age The age of the User.
   * @param address The address of the User.
   * @param role The role of the User.
   * @return The User created, or null if the given type is invalid.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  public static User makeUser(int id, String name, int age, String address, String role, Context context) {
    if (role.equals("ADMIN")) {
      return new Admin(id, name, age, address, context);
    } else if (role.equals("CUSTOMER")) {
      return new Customer(id, name, age, address, context);
    } else if (role.equals("TELLER")) {
      return new Teller(id, name, age, address, context);
    } else {
      return null;
    }
  }

}
