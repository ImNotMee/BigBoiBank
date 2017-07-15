package com.bank.generics;

public class AccountTypesContains {

  /**
   * Check if the Enum AccountTypes contains the given string.
   * @param account The name of the AccountType to be checked.
   * @return true if the Enum contains the string, false otherwise
   */
  public static boolean contains(String account) {
    if (account != null) {
      for (AccountTypes accountType : AccountTypes.values()) {
        if (accountType.name().equals(account.toUpperCase())) {
          return true;
        }
      }
    }
    return false;
  }
}
