package com.bank.database;

import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;



public class InitializeDatabase {
 
  /**
   * Initialize the database with it's first user and all tables setup.
   */
  public static void initialize() {
    
    Connection connection = DatabaseDriver.connectOrCreateDataBase();
    try {
      
      initializeDatabase(connection);
      initializeRoleTable(connection);
      initializeAccountTypes(connection);
      int userId = initializeFirstUser(connection);
      int accountId = initializeFirstAccount(connection);
      associateAccount(userId, accountId, connection);
    } catch (Exception e) {
      //TODO Improve this portion if you'd like
      e.printStackTrace();
    } finally {
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Unable to close connection");
      }
    }
    
  }
  
  /**
   * insert a second user into the database as a test.
   * @throws SQLException thrown if something goes wrong in inserting the user.
   */
  public static void insert() throws SQLException {
    Connection connection = DatabaseDriver.connectOrCreateDataBase();
    int userId = initializeFirstUser(connection);
    int accountId = initializeFirstAccount(connection);
    
    associateAccount(userId, accountId, connection);
    connection.close();
  }
  
  /**
   * Update stuff in the database to validate update commands worked.
   * DO NOT CALL THIS ON YOUR FINAL CODE!
   * @throws SQLException thrown if something goes wrong.
   */
  public static void update() throws SQLException {
    Connection connection = DatabaseDriver.connectOrCreateDataBase();
    
    DatabaseUpdater.updateAccountBalance(new BigDecimal("99.92"), 1, connection);
    DatabaseUpdater.updateAccountName("New John", 1, connection);
    DatabaseUpdater.updateAccountType(2, 1, connection);
    DatabaseUpdater.updateAccountTypeInterestRate(new BigDecimal("0.4"), 1, connection);
    DatabaseUpdater.updateAccountTypeName("THIS IS BAD", 1, connection);
    DatabaseUpdater.updateRoleName("THIS TOO IS BAD", 1, connection);
    DatabaseUpdater.updateUserAddress("123 Four Five Street", 1, connection);
    DatabaseUpdater.updateUserAge(102, 1, connection);
    DatabaseUpdater.updateUserName("Sir Bob Marley",1, connection);
    DatabaseUpdater.updateUserRole(2, 1, connection);
    
    connection.close();
  }

  private static void initializeDatabase(Connection connection) {
    try {
      DatabaseDriver.initialize(connection);
    } catch (Exception exception) {
      //TODO improve this block
      exception.printStackTrace();
    }
  }
  
  private static void initializeRoleTable(Connection connection) {
    String roleStr = "";
    try {
      for (Roles role : Roles.values()) {
        roleStr = role.toString();
        DatabaseInserter.insertRole(roleStr, connection);
      }
    } catch (Exception e) {
      //TODO Improve this block
      e.printStackTrace();
    }
  }
  
  private static void initializeAccountTypes(Connection connection) {
    String accountTypeStr = "";
    String interestRate = "0.2";
    try {
      for (AccountTypes accountTypes : AccountTypes.values()) {
        accountTypeStr = accountTypes.toString();
        DatabaseInserter.insertAccountType(accountTypeStr, new BigDecimal(interestRate), 
            connection);
      }
    } catch (Exception e) {
      //TODO improve this block
      e.printStackTrace();
    }
  }
  
  /**
   * Add the first user to the database.
   * @param connection the connection to the database.
   */
  private static int initializeFirstUser(Connection connection) {
    String name = "John Smith";
    String address = "123 Fake Street";
    int age = 102;
    int roleId = 1;
    String password = "Go hang a salami, I'm a lasagna Hog";
    
    try {
      return DatabaseInserter.insertNewUser(name, age, address, roleId, password, connection);
    } catch (Exception e) {
      //TODO improve this.
      e.printStackTrace();
      return -1;
    }
    
  }
  
  private static int initializeFirstAccount(Connection connection) {
    String name = "John's Checking Account";
    BigDecimal balance = new BigDecimal("1000000.00");
    int typeId = 1;
    
    try {
      return DatabaseInserter.insertAccount(name, balance, typeId, connection);
    } catch (Exception e) { 
      //TODO Improve this
      e.printStackTrace();
      return -1;
    }
  }
  
  private static void associateAccount(int userId, int accountId, Connection connection) {
    try {
      System.out.println(userId + " " + accountId);
      DatabaseInserter.insertUserAccount(userId, accountId, connection);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }



}
