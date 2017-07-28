package com.bank.generics;

import android.content.Context;

import com.bank.databasehelper.DatabaseSelectHelper;

import java.util.EnumMap;
import java.util.List;

public class AccountTypesEnumMap {

  private static EnumMap<AccountTypes, Integer> accountsMap = new EnumMap<>(AccountTypes.class);
  private DatabaseSelectHelper selector;
  
  public AccountTypesEnumMap(Context context) {
    selector = new DatabaseSelectHelper(context);
    this.update();
  }
  
  /**
   * Updates the EnumMap according to whatever is inside of the database.
   * @return true if it has successfully updated and false otherwise.
   */
  public void update() {
    List<Integer> accountIds = selector.getAccountTypesIds();

    // update the enumMap according to the database
    for (Integer accountId : accountIds) {
      // for all the accountIds, there are specific accounts we want to add
      String accountType = selector.getAccountTypeName((int) accountId);

      // add the value of the account Id into the enumMap for the specified account
      accountsMap.put(AccountTypes.valueOf(accountType), (int) accountId);
    }
  }
  
  /**
   * Returns the account id given the string of the account type.
   * @param accountName the name of the account
   * @return an integer representing the id of the type of account
   */
  public int getAccountId(String accountName) {
    return accountsMap.get(AccountTypes.valueOf(accountName));
  }
}
