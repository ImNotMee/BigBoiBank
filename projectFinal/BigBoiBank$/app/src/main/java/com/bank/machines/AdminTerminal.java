package com.bank.machines;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseBackUp;
import com.bank.databasehelper.DatabaseDriverAExtender;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.generics.RolesEnumMap;
import com.bank.users.User;

import java.io.File;
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
  private RolesEnumMap enumMap;

  /**
   * Constructor for AdminTerminal.
   * 
   * @param adminId the ID of the admin that will be using this machine
   * @param password the password of the admin
   */
  public AdminTerminal(int adminId, String password, Context context) {
    this.context = context;
    this.enumMap = new RolesEnumMap(this.context);
    this.insertor = new DatabaseInsertHelper(this.context);
    this.selector = new DatabaseSelectHelper(this.context);
    this.updater = new DatabaseUpdateHelper(this.context);
    // create a Customer object from the information in the database
    this.currentUser = selector.getUserDetails(adminId);
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
   */
  public int makeNewUser(String name, int age, String address, String password, String userType) {
    if (this.currentUserAuthenticated) {
      return insertor.insertNewUser(name, age, address, this.enumMap.getRoleId(
          userType.toUpperCase()), password);
    }
    return -1;
  }

  /**
   * A method that returns a list of users given the name of the role.
   * 
   * @param roleName , the name of the role.
   * @return A list of Users , which contains Admin, Teller or Customer.
   */
  public List<User> listUsers(String roleName) {
    List<User> users = new ArrayList<>();
    if (this.currentUserAuthenticated) {
      // Get the role Id of the roleName and capitalize it in case they're a bit slow
      int roleId = this.enumMap.getRoleId(roleName.toUpperCase());
      // Find all the users in the database the given role
      int currId = 1;
      User user = selector.getUserDetails(currId);
      while (user != null) {
        // this means there are still users in the database
        if (user.getRoleId() == roleId) {
          users.add(selector.getUserDetails(currId));
        }
        currId++;
        user = selector.getUserDetails(currId);
      }
    }
    return users;
  }
  
  /**
   * Get the total balance of all the accounts in the bank.
   * @return The total balance of all the accounts in the bank.
   */
  public BigDecimal getTotalBankBalance() {
    BigDecimal totalBalance = BigDecimal.ZERO;
    int currId = 1;
    Account account = selector.getAccountDetails(currId);
    while (account != null) {
      totalBalance = totalBalance.add(account.getBalance());
      currId ++;
      account = selector.getAccountDetails(currId);
    }
    return totalBalance;
  }
  
  /**
   * Promote a teller to an admin.
   * @param id The id of the teller to promote.
   * @return true iff the teller was promoted
   */
  public boolean promoteTellerToAdmin(int id) {
      return updater.updateUserRole(this.enumMap.getRoleId("ADMIN"), id);
    }
  
  /**
   * Creates the serialized  version.
   * @param output where the file will be written to.
   * @return true if it successfully wrote the ser file.
   */
  public boolean backUpDatabase(String output) {
    try {
      DatabaseBackUp db = new DatabaseBackUp(this.context);
      db.update();
      File file = new File(this.context.getFilesDir(), output);
      FileOutputStream outputStream = new FileOutputStream(file);
      ObjectOutputStream serialize = new ObjectOutputStream(outputStream);
      serialize.writeObject(db);
      serialize.close();
      outputStream.close(); 
      return true;
    } catch (IOException e) {
    }
    return false;
    
  }
  
  public boolean loadDatabase(String input) {
		DatabaseBackUp db = new DatabaseBackUp(this.context);
		boolean check = false;
    try {
      File file = new File(this.context.getFilesDir(), input);
      FileInputStream inputStream = new FileInputStream(file);
      ObjectInputStream deserialize = new ObjectInputStream(inputStream);
      db = (DatabaseBackUp) deserialize.readObject();
      deserialize.close();
      inputStream.close();
        check = true;
      } catch(Exception e) {
      }

    try {
      DatabaseDriverAExtender driver = new DatabaseDriverAExtender(this.context);
      driver.reinitialize();
    } catch (Exception e) {
      Log.e("MYAPP", "exception", e);
    }

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
      insertor.insertAccountType(accountTypeNames.get(i), interestRate.get(i));
    }

    for(int i = 0; i < roleNames.size(); i ++) {
      insertor.insertRole(roleNames.get(i));
    }

    for(int i = 1; i < accountsIds.size() + 1; i++) {
      ArrayList<Integer> accountID = accountsIds.get(i);
      for(Integer Id: accountID) {
        insertor.insertUserAccount(i, Id);
      }
    }

    for(int i = 0; i < balance.size(); i++) {
      insertor.insertAccount(accountNames.get(i), balance.get(i),
          accountType.get(i));
    }

    for(int i = 0; i < names.size(); i++) {
      int id = insertor.insertNewUser(names.get(i), age.get(i), addresses.get(i),
          roles.get(i), "");
      updater.updateUserPassword(passwords.get(i), id);
    }

    boolean messageExists = true;
    int currMessageId = 1;
    while (messageExists) {
      messageExists = false;
      for(int i = 1; i < messageRelation.size() + 1; i++ ) {
        ArrayList<Integer> messages1 = messageRelation.get(i);
        if (messages1.contains(currMessageId)) {
          insertor.insertMessage(i, messages.get(currMessageId));
          messageExists = true;
          currMessageId++;
          break;
        }
      }
    }
    System.out.println("The DatabaseBackUp object has been sucessfully loaded");
    return check;
  }
   
  /**
   * Get all the ids of the messages the admin can view.
   * @return The ids of all the messages.
   */
  public List<Integer> getUserMessageIds() {
    List<Integer> messageIds = new ArrayList<>();
    if (this.currentUserAuthenticated) {
      int currId = 1;
      String message = selector.getSpecificMessage(currId);
      while (message != null) {
        messageIds.add(currId);
        currId++;
        message = selector.getSpecificMessage(currId);
      }
    }
    return messageIds;
  }
  
  /**
   * Get all the ids of the messages the admin can view.
   * @return The ids of all the messages.
   */
  public List<Integer> getAdminMessageIds() {
    if (this.currentUserAuthenticated) {
      return selector.getMessageIds(this.currentUser.getId());
    }
    return null;
  }
  
  /**
   * Update a given account type's interest.
   * @param interestRate The new interest Rate.
   * @param accountTypeId The type of account to update.
   * @return True if the udpate was successful.
   */
  public boolean updateInterestRate(BigDecimal interestRate, int accountTypeId) {
    if (this.currentUserAuthenticated && interestRate.compareTo(BigDecimal.ONE) < 0 
        && interestRate.compareTo(BigDecimal.ZERO) >= 0) {
    return updater.updateAccountTypeInterestRate(interestRate, accountTypeId);
    } else {
      return false;
    }
  }  
}
  
