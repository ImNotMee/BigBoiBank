package com.bank.accounts;

import android.content.Context;

import java.math.BigDecimal;

public abstract class AccountCreator {
   
  /** 
   * Create an account with the given details.
   * @param id the account id linked to the id
   * @param name the name of the account
   * @param balance of the account
   * @return the account created or null if an invalid account type is given
   */
  public static Account createAccount(int id, String name, BigDecimal balance, String type, Context context) {
    if (type.equals("CHEQUING")) {
      return new ChequingAccount(id, name, balance, context);
    } else if (type.equals("SAVING")) {
      return new SavingsAccount(id, name, balance, context);
    } else if (type.equals("TFSA")) {
      return new TaxFreeSavingsAccount(id, name, balance, context);
    } else if (type.equals("BALANCEOWING")){
      return new BalanceOwingAccount(id, name, balance, context);
    } else if (type.equals("RESTRICTEDSAVING")) {
      return new RestrictedSavingsAccount(id, name, balance, context);
    } else {
      return null;
    }
  }

}
