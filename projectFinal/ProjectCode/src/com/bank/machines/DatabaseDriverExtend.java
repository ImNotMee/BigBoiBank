package com.bank.machines;

import java.sql.Connection;

import com.bank.database.DatabaseDriver;
import com.bank.exceptions.ConnectionFailedException;

public class DatabaseDriverExtend extends DatabaseDriver {
	  protected static Connection connectOrCreateDataBase() {
	    return DatabaseDriver.connectOrCreateDataBase();
	  }
	  
	  protected static Connection initialize(Connection connection) throws ConnectionFailedException {
	    return DatabaseDriver.initialize(connection);
	  }
	  
	  protected static Connection reInitialize() throws ConnectionFailedException{
		  return DatabaseDriver.reInitialize();
		  
	  }
	}
