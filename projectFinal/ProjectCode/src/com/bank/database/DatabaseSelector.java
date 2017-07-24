package com.bank.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSelector {

  /*
   * SELECT FUNCTIONS
   */
  
  /**
   * get all the roles.
   * @param connection the connection.
   * @return a ResultSet containing all rows of the roles table.
   * @throws SQLException thrown if an SQLException occurs.
   */
  protected static ResultSet getRoles(Connection connection) throws SQLException {
    Statement statement = connection.createStatement();
    ResultSet results = statement.executeQuery("SELECT * FROM ROLES;");
    return results;
  }
  
  /**
   * get the role with id id.
   * @param id the id of the role
   * @param connection the database connection
   * @return a String containing the role.
   * @throws SQLException thrown when something goes wrong with query.
   */
  protected static String getRole(int id, Connection connection) throws SQLException {
    String sql = "SELECT NAME FROM ROLES WHERE ID = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, id);
    ResultSet results = preparedStatement.executeQuery();
    return results.getString("NAME");
  }
  
  /**
   * get the role of the given user.
   * @param userId the id of the user.
   * @param connection the connection to the database.
   * @return the roleId for the user.
   * @throws SQLException thrown if something goes wrong with the query.
   */
  protected static int getUserRole(int userId, Connection connection) throws SQLException {
    String sql = "SELECT ROLEID FROM USERS WHERE ID = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, userId);
    ResultSet results = preparedStatement.executeQuery();
    return results.getInt("ROLEID");
  }
  
  /**
   * Return all users from the database.
   * @param connection the connection to the database.
   * @return a results set of all rows in the table.
   * @throws SQLException thrown if there is an issue.
   */
  protected static ResultSet getUsersDetails(Connection connection) throws SQLException {
    String sql = "SELECT * FROM USERS";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    return preparedStatement.executeQuery();
  }
  
  /**
   * find all the details about a given user.
   * @param userId the id of the user.
   * @param connection a connection to the database.
   * @return a result set with the details of the user.
   * @throws SQLException thrown when something goes wrong with query.
   */
  protected static ResultSet getUserDetails(int userId, Connection connection) throws SQLException {
    String sql = "SELECT * FROM USERS WHERE ID = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, userId);
    return preparedStatement.executeQuery();
  }
 
  /**
   * get the hashed version of the password.
   * @param userId the user's id.
   * @param connection the database connection.
   * @return the hashed password to be checked against given password.
   * @throws SQLException if a database issue occurs. 
   */
  protected static String getPassword(int userId, Connection connection) throws SQLException {
    String sql = "SELECT PASSWORD FROM USERPW WHERE USERID = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, userId);
    ResultSet results = preparedStatement.executeQuery();
    return results.getString("PASSWORD");
  }
  
  /**
   * return the id's of all of a user's accounts.
   * @param userId the id of the user.
   * @param connection the connection to the database.
   * @return a result set containing all accounts.
   * @throws SQLException thrown when something goes wrong with query.
   */
  protected static ResultSet getAccountIds(int userId, Connection connection) throws SQLException {
    String sql = "SELECT ACCOUNTID FROM USERACCOUNT WHERE USERID = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, userId);
    return preparedStatement.executeQuery();
  }
 
  
  
  /**
   * get the full details of an account.
   * @param accountId the id of the account
   * @param connection the connection to the database.
   * @return the details of the account.
   * @throws SQLException thrown when something goes wrong with query.
   */
  protected static ResultSet getAccountDetails(int accountId, Connection connection) 
       throws SQLException {
    String sql = "SELECT * FROM ACCOUNTS WHERE ID = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, accountId);
    return preparedStatement.executeQuery();
  }
  
  /**
   * return the balance in the account.
   * @param accountId the account to check.
   * @param connection a connection to the database.
   * @return the balance
   * @throws SQLException thrown when something goes wrong with query.
   */
  protected static BigDecimal getBalance(int accountId, Connection connection) throws SQLException {
    String sql = "SELECT BALANCE FROM ACCOUNTS WHERE ID = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, accountId);
    ResultSet results = preparedStatement.executeQuery();
    return new BigDecimal(results.getString("BALANCE"));
  }
  
  /**
   * get the typeId of the account.
   * @param accountId the accounts id
   * @param connection the connection to the database
   * @return the typeId
   * @throws SQLException thrown if something goes wrong.
   */
  protected static int getAccountType(int accountId, Connection connection) throws SQLException {
    String sql = "SELECT TYPE FROM ACCOUNTS WHERE ID = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, accountId);
    ResultSet results = preparedStatement.executeQuery();
    return results.getInt("TYPE");
    
  }
  
  /**
   * Return the accounttype name given an accountTypeId.
   * @param accountTypeId the id of the account type.
   * @param connection the connection to the database.
   * @return The name of the account type.
   * @throws SQLException thrown if something goes wrong.
   */
  protected static String getAccountTypeName(int accountTypeId, Connection connection) 
      throws SQLException {
    String sql = "SELECT NAME FROM ACCOUNTTYPES WHERE ID = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, accountTypeId);
    ResultSet results = preparedStatement.executeQuery();
    return results.getString("NAME");
  }
 
  /**
   * Return all data found within the AccountTypes table.
   * @param connection the connection to the database.
   * @return a result set of all rows in the table.
   * @throws SQLException thrown if there is an issue.
   */
  protected static ResultSet getAccountTypesId(Connection connection) throws SQLException {
    String sql = "SELECT ID FROM ACCOUNTTYPES";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    return preparedStatement.executeQuery();
  }
  
  /**
   * Get the interest rate for an account.
   * @param accountType the type for the account.
   * @param connection the database connection.
   * @return the interest rate.
   * @throws SQLException thrown if something goes wrong with the query.
   */
  protected static BigDecimal getInterestRate(int accountType, Connection connection) 
      throws SQLException {
    String sql = "SELECT INTERESTRATE FROM ACCOUNTTYPES WHERE ID = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, accountType);
    ResultSet results = preparedStatement.executeQuery();
    return new BigDecimal(results.getString("INTERESTRATE"));
  }
  
  /**
   * Get all messages currently available to a user.
   * @param userId the user whose messages are being retrieved.
   * @param connection connection to database.
   * @return a result set containing all messages.
   * @throws SQLException if something goes wrong.
   */
  protected static ResultSet getAllMessages(int userId, Connection connection) throws SQLException {
    String sql = "SELECT * FROM USERMESSAGES WHERE USERID = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, userId);
    return preparedStatement.executeQuery();
  }
  
  /**
   * Get a specific message from the database.
   * @param messageId the id of the message.
   * @param connection connection to the database.
   * @return the message from the database as a string.
   * @throws SQLException if something goes wrong.
   */
  protected static String getSpecificMessage(int messageId, Connection connection) 
      throws SQLException {
    String sql = "SELECT MESSAGE FROM USERMESSAGES WHERE ID = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setInt(1, messageId);
    return preparedStatement.executeQuery().getString("MESSAGE");
  }
  
}
