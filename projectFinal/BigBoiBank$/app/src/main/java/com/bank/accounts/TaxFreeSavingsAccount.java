package com.bank.accounts;

import android.content.Context;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.generics.AccountTypesEnumMap;

import java.math.BigDecimal;

public class TaxFreeSavingsAccount extends Account {
 
  
  /**
   * Initialize a TFSA with an id, name, and balance.
   * @param id The id of the Account. Must be a positive integer or it will not be set.
   * @param name The name of the Account. Must not be null or it will not be set.
   * @param balance The balance of the Account. Must not be a positive BidDecimal and not null or it
   *        will not be set.
   */
  public TaxFreeSavingsAccount(int id, String name, BigDecimal balance, Context context) {
    selector = new DatabaseSelectHelper(context);
    updater = new DatabaseUpdateHelper(context);
    this.enumMap = new AccountTypesEnumMap(context);
    this.setId(id);
    this.setType(this.enumMap.getAccountId("TFSA"));;
    this.setName(name);
    this.setBalance(balance);
  }
  
}
