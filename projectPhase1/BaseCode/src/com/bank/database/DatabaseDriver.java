package com.bank.database;

import com.bank.exceptions.ConnectionFailedException;

import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.Statement;



public class DatabaseDriver {
  
  /**
   * This will connect to existing database, or create it if it's not there.
   * @return the database connection.
   */
  protected static Connection connectOrCreateDataBase() {
    Connection connection = null;
    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:bank.db");
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return connection;
  }
  
  /**
   * This will initialize the database, or throw a ConnectionFailedException.
   * @param connection the database you'd like to write the tables to.
   * @return the connection you passed in, to allow you to continue.
   * @throws ConnectionFailedException If the tables couldn't be initialized, throw
   */
  protected static Connection initialize(Connection connection) throws ConnectionFailedException {
    if (!initializeDatabase(connection)) {
      throw new ConnectionFailedException();
    }
    return connection;
  }
  

  /*
   * BELOW THIS POINT ARE PRIVATE METHODS. 
   * DO NOT TOUCH THESE METHODS OR YOUR DATABASE SETUP MAY NOT MATCH WHAT IS BEING GRADED
   */
  
  
  private static boolean initializeDatabase(Connection connection) {
    Statement statement = null;
    
    try {
      statement = connection.createStatement();
      
      String sql = "CREATE TABLE ROLES " 
          + "(ID INTEGER PRIMARY KEY NOT NULL," 
          + "NAME TEXT NOT NULL)";
      statement.executeUpdate(sql);
      
      sql = "CREATE TABLE ACCOUNTTYPES "
          + "(ID INTEGER PRIMARY KEY NOT NULL,"
          + "NAME TEXT NOT NULL,"
          + "INTERESTRATE TEXT)";
      statement.executeUpdate(sql);
      
      sql = "CREATE TABLE ACCOUNTS " 
          + "(ID INTEGER PRIMARY KEY NOT NULL," 
          + "NAME TEXT NOT NULL,"
          + "BALANCE TEXT,"
          + "TYPE INTEGER NOT NULL,"
          + "FOREIGN KEY(TYPE) REFERENCES ACCOUNTTYPES(ID))";
      statement.executeUpdate(sql);
      
      sql = "CREATE TABLE USERS " 
          + "(ID INTEGER PRIMARY KEY NOT NULL," 
          + "NAME TEXT NOT NULL," 
          + "AGE INTEGER NOT NULL," 
          + "ADDRESS CHAR(100),"
          + "ROLEID INTEGER,"
          + "FOREIGN KEY(ROLEID) REFERENCES ROLE(ID))";
      statement.executeUpdate(sql);
      
      sql = "CREATE TABLE USERACCOUNT "
          + "(USERID INTEGER NOT NULL,"
          + "ACCOUNTID INTEGER NOT NULL,"
          + "FOREIGN KEY(USERID) REFERENCES USER(ID),"
          + "FOREIGN KEY(ACCOUNTID) REFERENCES ACOUNT(ID),"
          + "PRIMARY KEY(USERID, ACCOUNTID))";
      statement.executeUpdate(sql);
  
      sql = "CREATE TABLE USERPW " 
          + "(USERID INTEGER NOT NULL,"
          + "PASSWORD CHAR(64)," 
          + "FOREIGN KEY(USERID) REFERENCES USER(ID))";
      statement.executeUpdate(sql);
      
      statement.close();
      return true;
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }
  
  
}
