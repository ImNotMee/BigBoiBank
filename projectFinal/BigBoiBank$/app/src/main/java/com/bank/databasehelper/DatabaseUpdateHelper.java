package com.bank.databasehelper;

import android.content.Context;

import com.bank.generics.AccountTypesContains;
import com.bank.generics.Roles;

import java.math.BigDecimal;

public class DatabaseUpdateHelper {


  private DatabaseDriverAExtender driverExtender;
  private DatabaseSelectHelper selector;


  public DatabaseUpdateHelper(Context context) {
    driverExtender = new DatabaseDriverAExtender(context);
    selector = new DatabaseSelectHelper(context);
  }
  /**
   * Update the role name of a given role in the role table.
   * @param name The new name of the role.
   * @param id The current ID of the role.
   * @return true if successful, false otherwise.
   */
  public boolean updateRoleName(String name, int id) {
    // ensure the name is valid
    if (name != null) {
      // loop through possible role names
      for (Roles roleName : Roles.values()) {
        // check if the name given matches the current AccountType
        if (roleName.name().equals(name.toUpperCase())) {
            return driverExtender.updateRoleName(name.toUpperCase(), id);
        }
      }
    }
    // will return false if it was not updated successfully
    return false;
  }

  /**
   * Use this to update the user's name.
   * @param name The new name.
   * @param id The current id.
   * @return true if it works, false otherwise.
   */
  public boolean updateUserName(String name, int id) {
    // ensure the name and id are valid
    if (name != null && id > 0) {
      // try to update the user name in the database, seeing if it worked
      return driverExtender.updateUserName(name, id);
    }
    // return whether the user name was updated successfully
    return false;
  }

  /**
   * Use this to update the user's age.
   * @param age The new age.
   * @param id The current id
   * @return true if it succeeds, false otherwise.
   */
  public boolean updateUserAge(int age, int id) {
    if (age > 0 && id > 0) {
      return driverExtender.updateUserAge(age, id);
    }
    return false;
  }

  /**
   * Update the role of the user.
   * @param roleId The new role.
   * @param id The current id.
   * @return true if successful, false otherwise.
   */
  public boolean updateUserRole(int roleId, int id) {
    if (selector.getRoles().contains(roleId) && id > 0) {
        return driverExtender.updateUserRole(roleId, id);
    }
    return false;
  }
  

  /**
   * Use this to update user's address.
   * @param address The new address.
   * @param id The current id.
   */
  public boolean updateUserAddress(String address, int id) {
    // ensure the Address and id are valid
    if (address != null && address.length() <= 100 && id > 0) {
      // try to update the user address in the database, seeing if it worked
      return driverExtender.updateUserAddress(address, id);
    }
    return false;
  }
  public boolean updateUserPassword(String password, int id) {
	  if (id > 0) {
      return driverExtender.updateUserPassword(password, id);
	  }
	  return false;
  }
  
  public boolean updateUserMessageState(int id) {
	if (id > 0) {
    return driverExtender.updateUserMessageState(id);
	}
	return false;
	  
  }

  /**
   * Update the name of the account.
   * @param name The new name for the account.
   * @param id The id of the account to be changed.
   * @return true if successful, false otherwise.
   */
  public boolean updateAccountName(String name, int id) {
    // set that the account name was updated as false for default
    boolean complete = false;
    // ensure the account name and id are valid
    if (name != null && name.length() > 0 && id > 0) {
      return driverExtender.updateAccountName(name, id);
    }
    return false;
  }

  /**
   * Update the account balance.
   * @param balance The new balance for the account.
   * @param id The id of the account.
   * @return true if successful, false otherwise.
   */
  public boolean updateAccountBalance(BigDecimal balance, int id) {
    if (balance != null && id > 0) {
      return driverExtender.updateAccountBalance(balance, id);
    }
    return false;
  }

  /**
   * Update the type of the account.
   * @param typeId The new type for the account.
   * @param id The id of the account to be updated.
   * @return true if successful, false otherwise.
   */
  public boolean updateAccountType(int typeId, int id) {
    // ensure the typeId and id are valid
    if (selector.getAccountTypesIds().contains(typeId) && id > 0) {
      // try to update the account type id for the given user
      return driverExtender.updateAccountType(typeId, id);
    }
    return false;
  }

  /**
   * Update the name of an accountType.
   * @param name The new name to be given.
   * @param id The id of the accountType.
   * @return true if successful, false otherwise.
   */
  public boolean updateAccountTypeName(String name, int id)
      {
    if (AccountTypesContains.contains(name) && id > 0) {
      return driverExtender.updateAccountTypeName(name, id);
    }
    return false;
  }

  /**
   * Update the interest rate for this account type.
   * @param interestRate The interest rate to be updated to.
   * @param id The id of the accountType.
   * @return true if successful, false otherwise.
   */
  public boolean updateAccountTypeInterestRate(BigDecimal interestRate, int id)
      {
    // ensure the interest rate and id are valid
    if (interestRate.compareTo(BigDecimal.ONE) < 0 && interestRate.compareTo(BigDecimal.ZERO) >= 0
        && id > 0) {
        return driverExtender.updateAccountTypeInterestRate(
            interestRate.setScale(2, BigDecimal.ROUND_HALF_UP), id);
    }
    return false;
  }
}
