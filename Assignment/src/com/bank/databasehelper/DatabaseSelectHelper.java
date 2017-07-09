package com.bank.databasehelper;

import com.bank.accounts.Account;
import com.bank.accounts.ChequingAccount;
import com.bank.accounts.SavingsAccount;
import com.bank.accounts.TaxFreeSavingsAccount;
import com.bank.database.DatabaseSelector;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.users.Admin;
import com.bank.users.Customer;
import com.bank.users.Teller;
import com.bank.users.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class DatabaseSelectHelper extends DatabaseSelector {
    
  /**
   * Get the role with id id.
   * @param id The id of the role
   * @return a String containing the role.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static String getRole(int id) throws ConnectionFailedException {
    // if the id given is an invalid number
    if (id < 1) {
      return "Id given is invalid";
    }
    // set that id was not found by default
    String role = "Id does not exist in Roles Table.";
    // connect to a database 
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    // ensure the connection was successful
    if (connection != null) {
      try {
        // try to get the role of the given id
        role = DatabaseSelector.getRole(id, connection);
      // catch if the id is not in the database
      } catch (SQLException e) {
        e.printStackTrace();
      } 
      // ensure the connection closes
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Looks like it was closed already!");
      }
      // return the role of the id if it was found
      return role;
    } else {
      // throw Connection FailedException if the database was not connected to
      throw new ConnectionFailedException("Unable to connect to the database.");
    }
  }
   
  /**
   * Get the hashed version of the password.
   * @param userId The user's id.
   * @return The hashed password to be checked against given password.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static String getPassword(int userId) throws ConnectionFailedException {
    // if the userId given is an invalid number 
    if (userId < 1) {
      return "UserId given is invalid";
    }
    // set that the userId was not found by default
    String hashPassword = "UserId does not exist in UserPassword Table.";
    // connect to the database
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    // ensure the connection was successful
    if (connection != null) {
      try {
        // try to get the hashed password of the given userId
        hashPassword = DatabaseSelector.getPassword(userId, connection);
      // catch if the id is not in the database
      } catch (SQLException e) {
        // show what the error stack was
        e.printStackTrace();
      }
      // ensure the connection closes
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Looks like it was closed already!");
      }
      // return the password if it was found
      return hashPassword;
    } else {
      // throw Connection FailedException if the database was not connected to
      throw new ConnectionFailedException("Unable to connect to the database.");
    }    
  }
  
  /**
   * Find all the details about a given user.
   * @param userId The id of the user.
   * @return a User with the details of the user.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static User getUserDetails(int userId) throws ConnectionFailedException {
    // if the userId given is an invalid number 
    if (userId < 1) {
      return null;
    }
    // set the User as null by default
    User user = null;
    // connect to the database
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    // ensure the connection was successful
    if (connection != null) {
      try {
        // try to get the User details of the given userId
        ResultSet results = DatabaseSelector.getUserDetails(userId, connection);
        // try to get the given role of the id
        String role = DatabaseSelectHelper.getRole(results.getInt("ROLEID"));
        // try to create the new user
        user = makeUser(userId, results.getString("NAME"), results.getInt("AGE"), 
            results.getString("ADDRESS"), role);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      // ensure the connection closes
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Looks like it was closed already!");
      }
    } else {
      // throw Connection FailedException if the database was not connected to
      throw new ConnectionFailedException("Unable to connect to the database.");
    }
    // return the user created, or null if something given was invalid
    return user;
  }
  
  /**
   * Create a User with the given information.
   * @param id The id of the User.
   * @param name The name of the User.
   * @param age The age of the User.
   * @param address The address of the User.
   * @param role An int representing the role of the User.
   * @return The user created if successful, null otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  private static User makeUser(int id, String name, int age, String address, String role) 
      throws ConnectionFailedException {
    // if role is an Admin create and return one
    if (role.equals("ADMIN")) {
      return new Admin(id, name, age, address);
    // else if the role is a Customer create and return one
    } else if (role.equals("CUSTOMER")) {
      return new Customer(id, name, age, address);
    // otherwise create and return a Teller
    } else {
      return new Teller(id, name, age, address);
    }
  }
 
  /**
   * Return the id's of all of a user's accounts.
   * @param userId The id of the user.
   * @return A List of Integer containing all the Id's of the accounts of the user.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static List<Integer> getAccountIds(int userId) throws ConnectionFailedException {
    // if the userId given is an invalid number 
    if (userId < 1) {
      return null;
    }
    List<Integer> accountIds = new ArrayList<Integer>();
    // try to connect to the database
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    // ensure the connection is successful
    if (connection != null) {
      try {
        // try to get the accounts of the given Id
        ResultSet results = DatabaseSelector.getAccountIds(userId, connection);
        // loop through each available row in the results
        while (results.next()) {
          // add the accountId to the list of accountIds
          accountIds.add(results.getInt("ACCOUNTID"));
        }
      } catch (SQLException e) {
        // show what the error stack was
        e.printStackTrace();
      }
      // ensure the connection closes
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Looks like it was closed already!");
      }
      // return the list of accountIds corresponding to the User with the given userId
      return accountIds;
    } else {
      // throw Connection FailedException if the database was not connected to
      throw new ConnectionFailedException("Unable to connect to the database.");
    }
  }
  
  /**
   * Return the full details of an account in an Account.
   * @param accountId The id of the account.
   * @return An Account with the details of the accountId.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static Account getAccountDetails(int accountId) throws ConnectionFailedException {
    // if the accountId given is an invalid number 
    if (accountId < 1) {
      return null;
    }
    // set the Account as null by default
    Account account = null;
    // connect to the database
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    // ensure the connection is successful
    if (connection != null) {
      try {
        // try to get the Account details of the given accountId
        ResultSet results = DatabaseSelector.getAccountDetails(accountId, connection);
        // try to get the type of the Account
        String type = DatabaseSelectHelper.getAccountTypeName(results.getInt("TYPE"));
        // try to create the new Account
        account = makeAccount(accountId, results.getString("NAME"), results.getString("BALANCE"), 
            type);
      } catch (SQLException e) {
        // show what the error stack was
        e.printStackTrace();
      } 
      // ensure the connection closes
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Looks like it was closed already!");
      }
      return account; 
    } else {
      // throw Connection FailedException if the database was not connected to
      throw new ConnectionFailedException("Unable to connect to the database.");
    }
  }
  
  /**
   * Create and return an Account with the given information.
   * @param id The id of the account.
   * @param name The name of the account.
   * @param balance The balance of the account.
   * @param type An integer representing the type of account.
   * @return An Account with the given information.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  private static Account makeAccount(int id, String name, String balance, String type) 
      throws ConnectionFailedException {
    // if type is Chequing create and return a ChequingAccount
    if (type.equals("CHEQUING")) {
      return new ChequingAccount(id, name, new BigDecimal(balance));
    // else if the role is Savings create and return a SavingsAccount
    } else if (type.equals("SAVINGS")) {
      return new SavingsAccount(id, name, new BigDecimal(balance));
    // otherwise create and return a TFSA Account
    } else {
      return new TaxFreeSavingsAccount(id, name, new BigDecimal(balance));
    }
  }
 
  /**
   * Return the balance in the account.
   * @param accountId the account to check.
   * @return the balance
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static BigDecimal getBalance(int accountId) throws ConnectionFailedException {
    // if the accountId given is an invalid number 
    if (accountId < 1) {
      return null;
    }
    // set the balance as null for default
    BigDecimal balance = null;
    // connect to the database
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();    
    // ensure the connection is successful
    if (connection != null) {
      // try to get the balance
      try {
        balance = DatabaseSelector.getBalance(accountId, connection);
      } catch (SQLException e) {
        // show what the error stack was
        e.printStackTrace();
      }
      // ensure the connection closes
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Looks like it was closed already!");
      }
    } else {
      // throw Connection FailedException if the database was not connected to
      throw new ConnectionFailedException("Unable to connect to the database.");
    }
    return balance;
  }
 
  
  /**
   * Get the interest rate for an account.
   * @param accountType The type for the account.
   * @return The interest rate.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static BigDecimal getInterestRate(int accountType) throws ConnectionFailedException {
    // if the accountType given is an invalid number 
    if (accountType < 1) {
      return null;
    }
    // set the interestRate as null by default
    BigDecimal interestRate = null;
    // connect to the database
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    // ensure the connection is successful
    if (connection != null) {
      // try to get the interestRate
      try {
        interestRate = DatabaseSelector.getInterestRate(accountType, connection);
      } catch (SQLException e) {
        // show what the error stack was
        e.printStackTrace();
      }
      // ensure the connection closes
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Looks like it was closed already!");
      }
    } else {
      // throw Connection FailedException if the database was not connected to
      throw new ConnectionFailedException("Unable to connect to the database.");
    }
    return interestRate;
  }
  
  /**
   * Return a List of Integer representing the type of Account in the AccountTypes table.
   * @return a List of Integer representing the type of Account in the AccountTypes table.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static List<Integer> getAccountTypesIds() throws ConnectionFailedException {
    // create an empty array of AccountTypeIds
    List<Integer> accountTypeIds = new ArrayList<Integer>();
    // connect to the database
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    // ensure the connection is successful
    if (connection != null) {
      try {
        // try to get all the accountTypeIds
        ResultSet results = DatabaseSelector.getAccountTypesId(connection);
        // add each id to the list of ids
        while (results.next()) {
          accountTypeIds.add(results.getInt("ID"));
        }
      } catch (SQLException e) {
        // show what the error stack was
        e.printStackTrace();
      } 
    }
    // ensure the connection closes
    try {
      connection.close();
    } catch (SQLException e) {
      System.out.println("Looks like it was closed already!");
    }
    return accountTypeIds;
  }
  
  /**
   * Return the accounttype name given an accountTypeId.
   * @param accountTypeId The id of the account type.
   * @return The name of the account type.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static String getAccountTypeName(int accountTypeId) throws ConnectionFailedException {
    // if the accountType given is an invalid number 
    if (accountTypeId < 1) {
      return "accountTypeId given is invalid";
    }
    // set that the accountTypeId was not found by default
    String accountTypeName = "Account Type Id does not exist in AccountType Table.";
    // connect to the database
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    // ensure the connection is successful
    if (connection != null) {
      // try to get the name of the accountType
      try {
        accountTypeName = DatabaseSelector.getAccountTypeName(accountTypeId, connection);
      } catch (SQLException e) {
        // show what the error stack was
        e.printStackTrace();
      }
      // ensure the connection closes
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Looks like it was closed already!");
      }
    }
    return accountTypeName;
    
  }
  
  /**
   * Get a List of Integer representing all the roles.
   * @return a List of Integer representing all the roles.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static List<Integer> getRoles() {
    // create an empty array of roleIds
    List<Integer> roleIds = new ArrayList<Integer>();
    // connect to the database
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    // ensure the connection is successful
    if (connection != null) {
      try {
        // try to get the Roles
        ResultSet results = DatabaseSelector.getRoles(connection);
        // add each role id to the roleIds
        while (results.next()) {
          roleIds.add(results.getInt("ID"));
        }
      } catch (SQLException e) {
        // show what the error stack was
        e.printStackTrace();
      }
      // ensure the connection closes
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Looks like it was closed already!");
      }
    }
    return roleIds;
  }

  /**
   * Get the typeId of the account.
   * @param accountId The accounts id.
   * @return the typeId  of the Account.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static int getAccountType(int accountId) {
    // ensure the accountId given is a valid number 
    if (accountId < 1) {
      return -1;
    } 
    // set the default account type as -1
    int accountType = -1;
    // connect to the database
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    // ensure the connection is successful
    if (connection != null) {
      try {
        // try to get the AccountType of the accountId
        accountType = DatabaseSelector.getAccountType(accountId, connection);
      } catch (SQLException e) {
        e.printStackTrace();
      }
      // ensure the connection closes
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Looks like it was closed already!");
      }
    }
    return accountType;
  }
  
  /**
   * Get the role of the given user.
   * @param userId The id of the user.
   * @return the roleId for the user.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static int getUserRole(int userId) {
    // ensure the userId given is a valid number 
    if (userId < 1) {
      return -1;
    } 
    // set the default user role as -1
    int userRole = -1;
    // connect to the database
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    // ensure the connection is successful
    if (connection != null) {
      try {
        // try to get the userRole
        userRole = DatabaseSelector.getUserRole(userId, connection);
      } catch (SQLException e) {
        // show what the error stack was
        e.printStackTrace();
      }
      // ensure the connection closes
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Looks like it was closed already!");
      }
    }
    return userRole;
  }
  
}
