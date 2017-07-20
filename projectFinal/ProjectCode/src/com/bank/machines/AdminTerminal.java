package com.bank.machines;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.generics.RolesEnumMap;
import com.bank.users.Admin;
import com.bank.users.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdminTerminal extends BankWorkerServiceSystems {
  private RolesEnumMap enumMap = new RolesEnumMap();

  /**
   * Constructor for AdminTerminal.
   * 
   * @param adminId the ID of the admin that will be using this machine
   * @param password the password of the admin
   * @throws ConnectionFailedException If the database was not successfully connected to.
   */
  public AdminTerminal(int adminId, String password) throws ConnectionFailedException {
    // create a Customer object from the information in the database
    this.currentUser = (Admin) DatabaseSelectHelper.getUserDetails(adminId);
    // ensure the customer has the correct password
    this.currentUserAuthenticated = currentUser.authenticate(password);
  }
  
  /**
   * Makes a new User.
   * 
   * @param name name of the new user.
   * @param age the age of the new user.
   * @param address the address of the new user.
   * @param password the password of the new user.
   * @return the id of the user created.
   * @throws ConnectionFailedException If the connection fails.
   */
  public int makeNewUser(String name, int age, String address, String password, String userType)
      throws ConnectionFailedException {
    if (this.currentUserAuthenticated) {
      return DatabaseInsertHelper.insertNewUser(name, age, address, this.enumMap.getRoleId(
          userType.toUpperCase()), password);
    }
    return -1;
  }

  /**
   * A method that returns a list of users given the name of the role.
   * 
   * @param roleName , the name of the role.
   * @return A list of Users , which contains Admin, Teller or Customer.
   * @throws ConnectionFailedException , if the method cannot connect to the database.
   */
  public List<User> listUsers(String roleName) throws ConnectionFailedException {
    List<User> users = new ArrayList<>();
    if (this.currentUserAuthenticated) {
      // Get the role Id of the roleName and capitalize it in case they're a bit slow
      int roleId = this.enumMap.getRoleId(roleName.toUpperCase());
      // Find all the users in the database the given role
      int currId = 1;
      User user = DatabaseSelectHelper.getUserDetails(currId);
      while (user != null) {
        // this means there are still users in the database
        if (user.getRoleId() == roleId) {
          users.add(DatabaseSelectHelper.getUserDetails(currId));
        }
        currId++;
        user = DatabaseSelectHelper.getUserDetails(currId);
      }
    }
    return users;
  }
  
  public BigDecimal getTotalBalanceAllUsers() throws ConnectionFailedException {
	  String rolename = "CUSTOMER";
	  
	  List<User> allUsers = this.listUsers(rolename);
	  BigDecimal totalBalance;
	  totalBalance = BigDecimal.valueOf(0);
	  
	  if(allUsers != null) {
		  
	  
		  for(User curruser : allUsers){
			  totalBalance.add(this.getTotalBalance(curruser));
		  }
	  } else {
		  System.out.println("Customer does not have any accounts");
	  }
	  return totalBalance;
  }
}
