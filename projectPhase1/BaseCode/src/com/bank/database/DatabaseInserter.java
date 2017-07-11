package com.bank.database;

import com.bank.security.PasswordHelpers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class DatabaseInserter {
  /*
   * INSERT STATEMENTS
   */
  /**
   * Use this to insert new roles into the database.
   * @param role the new role to be added.
   * @param connection the database.
   * @return true if successful, false otherwise.
   */
  protected static boolean insertRole(String role, Connection connection) {
    String sql = "INSERT INTO ROLES(NAME) VALUES(?)";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1,role);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * Use this to insert a new user.
   * @param name the user's name.
   * @param age the user's age.
   * @param address the user's address.
   * @param roleId the user's role.
   * @param password the user's password (not hashsed).
   * @param connection the database connection.
   * @return the account id if successful, -1 otherwise
   */
  protected static int insertNewUser(String name, int age, String address, int roleId,
        String password, Connection connection) {
    int id = insertUser(name, age, address, roleId, connection);
    if (id != -1) {
      insertPassword(password, id, connection);
      return id;
    }
    return -1;
  }
  
  /**
   * insert an accountType into the accountType table.
   * @param name the name of the type of account.
   * @param interestRate the interest rate for this type of account.
   * @param connection the database connection.
   * @return true if successful, false otherwise.
   */
  protected static boolean insertAccountType(String name, BigDecimal interestRate, 
      Connection connection) {
    String sql = "INSERT INTO ACCOUNTTYPES(NAME,INTERESTRATE) VALUES(?,?)";;
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, name);
      preparedStatement.setString(2, interestRate.toPlainString());
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return false;
  }
  
  /**
   * Insert a new account into account table.
   * @param name the name of the account.
   * @param balance the balance currently in account.
   * @param typeId the id of the type of the account.
   * @param connection the database connection.
   * @return accountId of inserted account, -1 otherwise
   */
  protected static int insertAccount(String name, BigDecimal balance, int typeId, 
      Connection connection) {
    String sql = "INSERT INTO ACCOUNTS(NAME,BALANCE,TYPE) VALUES(?,?,?)";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql, 
          Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, name);
      preparedStatement.setString(2, balance.toPlainString());
      preparedStatement.setInt(3, typeId);
      int id = preparedStatement.executeUpdate();
      if (id > 0) {
        ResultSet uniqueKey = preparedStatement.getGeneratedKeys();
        if (uniqueKey.next()) {
          return uniqueKey.getInt(1);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }
  
  /**
   * insert a user and account relationship.
   * @param userId the id of the user.
   * @param accountId the id of the account.
   * @param connection the database connection.
   * @return true if successful, false otherwise.
   */
  protected static boolean insertUserAccount(int userId, int accountId, Connection connection)  {
    String sql = "INSERT INTO USERACCOUNT(USERID,ACCOUNTID) VALUES(?,?);";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, userId);
      preparedStatement.setInt(2, accountId);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  private static boolean insertPassword(String password, int userId, Connection connection) {
    String sql = "INSERT INTO USERPW(USERID, PASSWORD) VALUES(?,?);";
    try {
      password = PasswordHelpers.passwordHash(password);
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, userId);
      preparedStatement.setString(2, password);
      preparedStatement.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  private static int insertUser(String name, int age, String address, int roleId,
        Connection connection) {
    String sql = "INSERT INTO USERS(NAME, AGE, ADDRESS, ROLEID) VALUES(?,?,?,?);";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql, 
          Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, name);
      preparedStatement.setInt(2, age);
      preparedStatement.setString(3, address);
      preparedStatement.setInt(4, roleId);
      int id = preparedStatement.executeUpdate();
      if (id > 0) {
        ResultSet uniqueKey = preparedStatement.getGeneratedKeys();
        if (uniqueKey.next()) {
          return uniqueKey.getInt(1);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }
}
