package com.bank.machines;

import java.util.ArrayList;
import java.util.List;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.users.*;
import com.bank.generics.RolesEnumMap;

public class AdminTerminal extends BankServiceSystems {
  private Admin currentAdmin; 
  private boolean currentAdminAuthenicated;
  private RolesEnumMap enumMap = new RolesEnumMap();
  
  /**
   * Constructor for AdminTerminal.
   * @param admin the admin that will be using this machine
   * @param authenicated if the admin is authenticated.
   */
  public AdminTerminal(Admin admin, boolean authenicated) {
    this.currentAdmin = admin;
    this.currentAdminAuthenicated = authenicated;
  }
  
  /**
   * Makes a new Admin.
   * @param name name of the new admin.
   * @param age the age of the new admin.
   * @param address the address of the new admin.
   * @param password the password of the new admin.
   * @return the id of the admin created.
   * @throws ConnectionFailedException If the connection fails.
   */
  public int makeNewAdmin(String name, int age, String address, String password) throws ConnectionFailedException {
    if (currentAdminAuthenicated) {
      return DatabaseInsertHelper.insertNewUser(name, age, address, this.enumMap.getRoleId("ADMIN"), password);
    }
    return -1;
  }
  
  public List<User> listUsers(String roleName) throws ConnectionFailedException {
    List<User> users = new ArrayList<>();
    // Get the role Id of the roleName and capitalize it in case they're a bit slow
    int roleId = this.enumMap.getRoleId(roleName.toUpperCase());
    // Find all the users in the database of the given role
    int currId = 1;
    int currUserRoleId = DatabaseSelectHelper.getUserRole(currId);
    while (currUserRoleId != -1) {
      // this means there are still users in the database
      if (currUserRoleId == roleId) {
        users.add(DatabaseSelectHelper.getUserDetails(currId));
      }
      currId++;
      currUserRoleId = DatabaseSelectHelper.getUserRole(currId);
    }
    return users;
  }
  
  public boolean setCurrentCustomer(Customer customer) {
    boolean success = false;
    int customerId = this.enumMap.getRoleId("CUSTOMER");
    // Check if the Admin is authenticated and if the customer is a customer
    if (currentAdminAuthenicated && customer.getId() == customerId) {
      currentCustomer = customer;
      success = true;
    }
    return success;
  }  

  public boolean deauthenciateAdmin() {
    boolean success = false;
    if (this.currentAdminAuthenicated) {
      this.currentAdminAuthenicated = false;
      this.currentAdmin = null;
      success = true;
    }
    return success;
  }
}
