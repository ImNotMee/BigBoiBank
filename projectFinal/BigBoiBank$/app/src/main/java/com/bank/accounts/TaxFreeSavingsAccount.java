package com.bank.accounts;

import com.bank.exceptions.ConnectionFailedException;

import java.math.BigDecimal;

public class TaxFreeSavingsAccount extends Account {
 
  
  /**
   * Initialize a TFSA with an id, name, and balance.
   * @param id The id of the Account. Must be a positive integer or it will not be set.
   * @param name The name of the Account. Must not be null or it will not be set.
   * @param balance The balance of the Account. Must not be a positive BidDecimal and not null or it
   *        will not be set.
   * @throws ConnectionFailedException If connection can not be made to the database.
   */
  public TaxFreeSavingsAccount(int id, String name, BigDecimal balance) 
      throws ConnectionFailedException {
    this.setId(id);
    this.setType(this.enumMap.getAccountId("TFSA"));;
    this.setName(name);
    this.setBalance(balance);
  }
  
}
