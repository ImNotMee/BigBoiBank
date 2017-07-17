package com.bank.generics;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.generics.AccountTypes;
import java.util.EnumMap;
import java.util.List;

public class AccountTypesEnumMap {

  private static EnumMap<AccountTypes, Integer> accountsMap = new EnumMap<>(AccountTypes.class);
  
  public AccountTypesEnumMap(){
    this.update();
  }
  
  /**
   * Updates the EnumMap according to whatever is inside of the database.
   * @return true if it has successfully updated and false otherwise.
   */
  public boolean update() {
    try {
      List<Integer> accountIds = DatabaseSelectHelper.getAccountTypesIds();
      
      // update the enumMap according to the database
      for (Integer accountId : accountIds) {
        // for all the accountIds, there are specific accounts we want to add
        String accountType = DatabaseSelectHelper.getAccountTypeName((int) accountId);
        
        // add the value of the account Id into the enumMap for the specified account
        accountsMap.put(AccountTypes.valueOf(accountType), (int) accountId);
       } 
      return true;
    } catch (ConnectionFailedException e) {
        return false;
      }
    }
  
  /**
   * Returns the account id given the string of the account type
   * @param accountName
   * @return
   */
  public int getAccountId(String accountName) {
    return accountsMap.get(AccountTypes.valueOf(accountName));
  }
}
