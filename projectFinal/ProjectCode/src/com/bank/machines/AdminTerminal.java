package com.bank.machines;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseBackUp;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.generics.RolesEnumMap;
import com.bank.users.Admin;
import com.bank.users.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
  
  /**
   * Get the total balance of all the accounts in the bank.
   * @return The total balance of all the accounts in the bank.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  public BigDecimal getTotalBankBalance() throws ConnectionFailedException {	  
    BigDecimal totalBalance = BigDecimal.ZERO;
    int currId = 1;
    Account account = DatabaseSelectHelper.getAccountDetails(currId);
    while (account != null) {
      totalBalance.add(account.getBalance());
      currId ++;
      account = DatabaseSelectHelper.getAccountDetails(currId);
    }
    return totalBalance;
  }
  
  /**
   * Promote a teller to an admin.
   * @param id The id of the teller to promote.
   * @return
   * @throws ConnectionFailedException
   */
  public boolean promoteTellerToAdmin(int id) throws ConnectionFailedException {
      return DatabaseUpdateHelper.updateUserRole(this.enumMap.getRoleId("ADMIN"), id);
    }
  
  /**
   * Creates the serialized  version.
   * @param output where the file will be written to.
   * @return true if it successfully wrote the ser file.
   */
  public boolean backUpDatabase(String output) {
    try {
      System.out.println("CREATING BACKUP BEEP BOOP");
      DatabaseBackUp db = new DatabaseBackUp();
      System.out.println("UPDATING BEEP BOOP");
      db.update();
      System.out.println("FILE OUTPUT BEEP BOOP");
      FileOutputStream outputStream = new FileOutputStream(output);
      System.out.println("D");
      ObjectOutputStream serialize = new ObjectOutputStream(outputStream);
      System.out.println("WRITING BEEP BOOP");
      serialize.writeObject(db);
      System.out.println("The DatabaseBackUp object has been sucessfully written to " + output);
      serialize.close();
      outputStream.close(); 
      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
    
  }
  
  public boolean loadDatabaseObject(String input) throws ConnectionFailedException {
		DatabaseBackUp db = new DatabaseBackUp();
		boolean check = false;
	    try {
		  FileInputStream inputStream = new FileInputStream(input);
		  ObjectInputStream deserialize = new ObjectInputStream(inputStream);
		  db = (DatabaseBackUp) deserialize.readObject();
		  deserialize.close();
		  inputStream.close();
		  System.out.println("The DatabaseBackUp object has been sucesfully loaded");
	      check = true;
		  } catch(Exception e) {
			  e.printStackTrace();
		  }
	      DatabaseDriverExtend.reInitialize();
	      
	      ArrayList<BigDecimal> balance = db.getAccountBalances();
	      ArrayList<BigDecimal> interestRate = db.getAccountInterestRates();
	      ArrayList<String> accountNames = db.getAccountNames();
	      ArrayList<String> accountTypeNames = db.getAccountTypeNames();
	      ArrayList<Integer> accountType = db.getAccountTypes();
	      ArrayList<Integer> roles = db.getUserRoleIds();
	      ArrayList<String> roleNames = db.getRoleNames();
	      HashMap<Integer, ArrayList<Integer>> accountsIds = db.getAccountsIds();
	      HashMap<Integer, ArrayList<Integer>> messageRelation = db.getMessageRelationships();
	      HashMap<Integer, String> messages = db.getMessages();
	      ArrayList<String> names = db.getUserNames();
	      ArrayList<String> addresses = db.getUserAddresses();
	      ArrayList<Integer> age = db.getUserAges();
	      ArrayList<String> passwords = db.getUserPassword();
	      
	      
	      for(int i = 0; i < accountTypeNames.size(); i ++) {
	      DatabaseInsertHelper.insertAccountType(accountTypeNames.get(i), interestRate.get(i));
	      }
	      
	      for(int i = 0; i < roles.size(); i ++) {
		      DatabaseInsertHelper.insertRole(roleNames.get(i));
		      }

	      for(int i = 1; i < messageRelation.size() + 1; i++ ) {
	          ArrayList<Integer> messages1 = messageRelation.get(i);
	          for(Integer messageid: messages1) {
	        	 DatabaseInsertHelper.insertMessage(i, messages.get(messageid));
	          }
	      }
	      
	      for(int i = 1; i < accountsIds.size() + 1; i++) {
	    	  ArrayList<Integer> accountID = accountsIds.get(i);
	    	  for(Integer Id: accountID) {
	    		  DatabaseInsertHelper.insertUserAccount(i, Id);
	    	  }
	      }

	      for(int i = 0; i < balance.size(); i++) {
	    	  DatabaseInsertHelper.insertAccount(accountNames.get(i), balance.get(i), accountType.get(i));
	      }
	 
	      for(int i = 0; i < names.size(); i++) {
	    	  DatabaseInsertHelper.insertNewUser(names.get(i), age.get(i), addresses.get(i), roles.get(i), passwords.get(i));
	      }
	      
	      return check;
	  }
   
  /**
   * Get all the ids of the messages the admin can view.
   * @return The ids of all the messages.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  public List<Integer> getUserMessageIds() throws ConnectionFailedException {
    List<Integer> messageIds = new ArrayList<>();
    if (this.currentUserAuthenticated) {
      int currId = 1;
      String message = DatabaseSelectHelper.getSpecificMessage(currId);
      while (message != null) {
        messageIds.add(currId);
        currId++;
        message = DatabaseSelectHelper.getSpecificMessage(currId);
      }
    }
    return messageIds;
  }
  
  /**
   * Get all the ids of the messages the admin can view.
   * @return The ids of all the messages.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  public List<Integer> getAdminMessageIds() throws ConnectionFailedException {
    if (this.currentUserAuthenticated) {
      return DatabaseSelectHelper.getMessageIds(this.currentUser.getId());
    }
    return null;
  }
}
