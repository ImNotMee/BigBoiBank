package com.bank.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DatabaseUpdater {
  /* 
   * UPDATE FUNCTIONS
   */
  /**
   * Update the role name of a given role in the role table.
   * @param name the new name of the role.
   * @param id the current ID of the role.
   * @param connection the database connection.
   * @return true if successful, false otherwise.
   */
  protected static boolean updateRoleName(String name, int id, Connection connection) {
    String sql = "UPDATE ROLES SET NAME = ? WHERE ID = ?;";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, name);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
      return true;
        
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * Use this to update the user's name.
   * @param name the new name
   * @param id the current id
   * @param connection the database
   * @return true if it works, false otherwise.
   */
  protected static boolean updateUserName(String name, int id, Connection connection) {
    String sql = "UPDATE USERS SET NAME = ? WHERE ID = ?;";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, name);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * Use this to update the user's age.
   * @param age the new age.
   * @param id the current id
   * @param connection the connection.
   * @return true if it succeeds, false otherwise.
   */
  protected static boolean updateUserAge(int age, int id, Connection connection) {
    String sql = "UPDATE USERS SET AGE = ? WHERE ID = ?;";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, age);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * update the role of the user.
   * @param roleId the new role.
   * @param id the current id.
   * @param connection the database connection.
   * @return true if successful, false otherwise.
   */
  protected static boolean updateUserRole(int roleId, int id, Connection connection) {
    String sql = "UPDATE USERS SET ROLEID = ? WHERE ID = ?;";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, roleId);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * Use this to update user's address.
   * @param address the new address.
   * @param id the current id.
   * @param connection the database connection.
   * @return true if successful, false otherwise.
   */
  protected static boolean updateUserAddress(String address, int id, Connection connection) {
    String sql = "UPDATE USERS SET ADDRESS = ? WHERE ID = ?;";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, address);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * update the name of the account.
   * @param name the new name for the account.
   * @param id the id of the account to be changed.
   * @param connection connection to the database.
   * @return true if successful, false otherwise.
   */
  protected static boolean updateAccountName(String name, int id, Connection connection) {
    String sql = "UPDATE ACCOUNTS SET NAME = ? WHERE ID = ?;";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, name);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * update the account balance.
   * @param balance the new balance for the account.
   * @param id the id of the account.
   * @param connection the connection to the database.
   * @return true if successful, false otherwise.
   */
  protected static boolean updateAccountBalance(BigDecimal balance, int id, Connection connection) {
    String sql = "UPDATE ACCOUNTS SET BALANCE = ? WHERE ID = ?;";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, balance.toPlainString());
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * update the type of the account.
   * @param typeId the new type for the account. 
   * @param id the id of the account to be updated.
   * @param connection the connection to the database.
   * @return true if successful, false otherwise.
   */
  protected static boolean updateAccountType(int typeId, int id, Connection connection) {
    String sql = "UPDATE ACCOUNTS SET TYPE = ? WHERE ID = ?;";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, typeId);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * update the name of an accountType.
   * @param name the new name to be given.
   * @param id the id of the accountType.
   * @param connection the connection to the database.
   * @return true if successful, false otherwise.
   */
  protected static boolean updateAccountTypeName(String name, int id, Connection connection) {
    String sql = "UPDATE ACCOUNTTYPES SET NAME = ? WHERE ID = ?;";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, name);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * update the interest rate for this account type.
   * @param interestRate the interest rate to be updated to.
   * @param id the id of the accountType.
   * @param connection the connection to the database.
   * @return true if successful, false otherwise.
   */
  protected static boolean updateAccountTypeInterestRate(BigDecimal interestRate, int id, 
      Connection connection) {
    String sql = "UPDATE ACCOUNTTYPES SET INTERESTRATE = ? WHERE ID = ?;";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, interestRate.toPlainString());
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * Updates a users password in the database.
   * @param userId the id of the user.
   * @param password the HASHED password of the user (not plain text!).
   * @param connection the connection to the database.
   * @return true if update succeeded, false otherwise.
   */
  protected static boolean updateUserPassword(String password, int id,
      Connection connection) {
    String sql = "UPDATE USERPW SET PASSWORD = ? WHERE USERID = ?";
    try { 
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setString(1, password);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * Update the state of the user message to viewed.
   * @param userMessageId the id of the message that has been viewed.
   * @param connection connection to the database.
   * @return true if successful, false o/w.
   */
  protected static boolean updateUserMessageState(int id, Connection connection) {
    String sql = "UPDATE USERMESSAGES SET VIEWED = ? WHERE ID = ?";
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sql);
      preparedStatement.setInt(1, 1);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
}
