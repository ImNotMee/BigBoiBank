package com.bank.accounts;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;

import java.math.BigDecimal;

public abstract class AccountCreator {
   
  /** 
   * Create an account with the given details.
   * @param id the account id linked to the id
   * @param name the name of the account
   * @param balance of the account
   * @return the account created or null if an invalid account type is given
   * @throws ConnectionFailedException if the database can not be connected to
   */
  public static Account createAccount(int id, String name, BigDecimal balance) 
      throws ConnectionFailedException {
    // finds the account type associated with the id
    String type = DatabaseSelectHelper.getAccountTypeName(id);
    if (type.equals("CHEQUING")) {
      return new ChequingAccount(id, name, balance);
    } else if (type.equals("SAVING")) {
      return new SavingsAccount(id, name, balance);
    } else if (type.equals("TFSA")) {
      return new TaxFreeSavingsAccount(id, name, balance);
    } else if (type.equals("BALANCEOWING")){
      return new BalanceOwingAccount(id, name, balance);
    } else {
      return null;
    }
  }

}
