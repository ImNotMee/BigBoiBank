package com.bank.databasehelper;

import com.bank.database.DatabaseDriver;

import java.sql.Connection;

public class DatabaseDriverHelper extends DatabaseDriver {
  protected static Connection connectOrCreateDataBase() {
    return DatabaseDriver.connectOrCreateDataBase();
  }
}
