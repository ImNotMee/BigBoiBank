package com.bank.databasehelper;

import com.bank.database.DatabaseUpdater;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.generics.AccountTypesContains;
import com.bank.generics.Roles;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUpdateHelper extends DatabaseUpdater {

  /**
   * Update the role name of a given role in the role table.
   * @param name The new name of the role.
   * @param id The current ID of the role.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static boolean updateRoleName(String name, int id) throws ConnectionFailedException {
    // set the the role was updated as false for default
    Boolean complete = false;
    // ensure the name is valid
    if (name != null) {
      // loop through possible role names
      for (Roles roleName : Roles.values()) {
        // check if the name given matches the current AccountType
        if (roleName.name().equals(name.toUpperCase())) {
          // connect to the database
          Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
          // ensure the connection was successful
          if (connection != null) {
            // try to update the accountRole in the database, seeing if it
            // worked
            complete = DatabaseUpdater.updateRoleName(name.toUpperCase(), id, connection);
            try {
              // ensure the connection is closed
              connection.close();
            } catch (SQLException e) {
              System.out.println("Looks like it was closed already!");
            }
            // return whether it was updated successfully
            return complete;
          } else {
            // throw Connection FailedException if the database was not
            // connected to
            throw new ConnectionFailedException("Unable to connect to the database.");
          }
        }
      }
    }
    // will return false if it was not updated successfully
    return complete;
  }

  /**
   * Use this to update the user's name.
   * @param name The new name.
   * @param id The current id.
   * @return true if it works, false otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static boolean updateUserName(String name, int id) throws ConnectionFailedException {
    // set the user name as was updated as false for default
    boolean complete = false;
    // ensure the name and id are valid
    if (name != null && id > 0) {
      // connect to the database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // ensure the connection was successful
      if (connection != null) {
        // try to update the user name in the database, seeing if it worked
        complete = DatabaseUpdater.updateUserName(name, id, connection);
        // ensure the connection is closed
        try {
          // ensure the connection is closed
          connection.close();
        } catch (SQLException e) {
          System.out.println("Looks like it was closed already!");
        }
      } else {
        // throw Connection FailedException if the database was not connected to
        throw new ConnectionFailedException("Unable to connect to the database.");
      }
    }
    // return whether the user name was updated successfully
    return complete;
  }

  /**
   * Use this to update the user's age.
   * @param age The new age.
   * @param id The current id
   * @return true if it succeeds, false otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static boolean updateUserAge(int age, int id) throws ConnectionFailedException {
    // set the user age was updated as false for default
    boolean complete = false;
    // ensure the age and id are valid
    if (age > 0 && id > 0) {
      // connect to the database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // ensure the connection was successful
      if (connection != null) {
        // try to update the user name in the database, seeing if it worked
        complete = DatabaseUpdater.updateUserAge(age, id, connection);
        // ensure the connection is closed
        try {
          // ensure the connection is closed
          connection.close();
        } catch (SQLException e) {
          System.out.println("Looks like it was closed already!");
        }
      } else {
        // throw Connection FailedException if the database was not connected to
        throw new ConnectionFailedException("Unable to connect to the database.");
      }
    }
    // return whether the user age was updated successfully
    return complete;
  }

  /**
   * Update the role of the user.
   * @param roleId The new role.
   * @param id The current id.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static boolean updateUserRole(int roleId, int id) throws ConnectionFailedException {
    // set that the user role was updated as false for default
    boolean complete = false;
    // ensure the role id and id are valid
    if (DatabaseSelectHelper.getRoles().contains(roleId) && id > 0) {
      // connect to the database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // ensure the connection was successful
      if (connection != null) {
        // try to update the user name in the database, seeing if it worked
        complete = DatabaseUpdater.updateUserRole(roleId, id, connection);
        // ensure the connection is closed
        try {
          // ensure the connection is closed
          connection.close();
        } catch (SQLException e) {
          System.out.println("Looks like it was closed already!");
        }
      } else {
        // throw Connection FailedException if the database was not connected to
        throw new ConnectionFailedException("Unable to connect to the database.");
      }
    }
    // return whether the user role was updated successfully
    return complete;
  }

  /**
   * Use this to update user's address.
   * @param address The new address.
   * @param id The current id.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static boolean updateUserAddress(String address, int id) throws ConnectionFailedException {
    // set that the user Address was updated as false for default
    boolean complete = false;
    // ensure the Address and id are valid
    if (address != null && address.length() <= 100 && id > 0) {
      // connect to the database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // ensure the connection was successful
      if (connection != null) {
        // try to update the user address in the database, seeing if it worked
        complete = DatabaseUpdater.updateUserAddress(address, id, connection);
        try {
          // ensure the connection is closed
          connection.close();
        } catch (SQLException e) {
          System.out.println("Looks like it was closed already!");
        }
      } else {
        // throw Connection FailedException if the database was not connected to
        throw new ConnectionFailedException("Unable to connect to the database.");
      }
    }
    // return whether the address was updated successfully
    return complete;
  }

  /**
   * Update the name of the account.
   * @param name The new name for the account.
   * @param id The id of the account to be changed.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static boolean updateAccountName(String name, int id) throws ConnectionFailedException {
    // set that the account name was updated as false for default
    boolean complete = false;
    // ensure the account name and id are valid
    if (name != null && name.length() > 0 && id > 0) {
      // connect to the database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // ensure the connection was successful
      if (connection != null) {
        // try to update the account name for the user, seeing if it worked
        complete = DatabaseUpdater.updateAccountName(name, id, connection);
        try {
          // ensure the connection is closed
          connection.close();
        } catch (SQLException e) {
          System.out.println("Looks like it was closed already!");
        }
      }
    } else {
      // throw Connection FailedException if the database was not connected to
      throw new ConnectionFailedException("Unable to connect to the database.");
    }
    // return whether the account name was updated successfully
    return complete;
  }

  /**
   * Update the account balance.
   * @param balance The new balance for the account.
   * @param id The id of the account.
   * @return true if successful, false otherwise.
   */
  public static boolean updateAccountBalance(BigDecimal balance, int id)
      throws ConnectionFailedException {
    // set that the account name was updated as false for default
    boolean complete = false;
    // ensure the balance and id are valid
    if (balance != null && id > 0) {
      // connect to the database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // ensure the connection was successful
      if (connection != null) {
        // try to update the balance for the user, seeing if it worked
        complete = DatabaseUpdater.updateAccountBalance(balance, id, connection);
        try {
          // ensure the connection is closed
          connection.close();
        } catch (SQLException e) {
          System.out.println("Looks like it was closed already!");
        }
      } else {
        // throw Connection FailedException if the database was not connected to
        throw new ConnectionFailedException("Unable to connect to the database.");
      }
    }
    // return whether the account name was updated successfully
    return complete;
  }

  /**
   * Update the type of the account.
   * @param typeId The new type for the account.
   * @param id The id of the account to be updated.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static boolean updateAccountType(int typeId, int id) throws ConnectionFailedException {
    // set that the account Type was updated as false for default
    boolean complete = false;
    // ensure the typeId and id are valid
    if (DatabaseSelectHelper.getAccountTypesIds().contains(typeId) && id > 0) {
      // connect to the database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // ensure the connection was successful
      if (connection != null) {
        // try to update the account type id for the given user
        complete = DatabaseUpdater.updateAccountType(typeId, id, connection);
        try {
          // ensure the connection is closed
          connection.close();
        } catch (SQLException e) {
          System.out.println("Looks like it was closed already!");
        }
      } else {
        // throw Connection FailedException if the database was not connected to
        throw new ConnectionFailedException("Unable to connect to the database.");
      }
    }
    // return whether the account type was updated successfully
    return complete;
  }

  /**
   * Update the name of an accountType.
   * @param name The new name to be given.
   * @param id The id of the accountType.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static boolean updateAccountTypeName(String name, int id)
      throws ConnectionFailedException {
    // set that the account type name was updated as false for default
    boolean complete = false;
    // ensure the name is valid and id are valid
    if (AccountTypesContains.contains(name) && id > 0) {
      // connect to the database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // ensure the connection was successful
      if (connection != null) {
        // try to update the account type id for the given user
        complete = DatabaseUpdater.updateAccountTypeName(name, id, connection);
        try {
          // ensure the connection is closed
          connection.close();
        } catch (SQLException e) {
          System.out.println("Looks like it was closed already!");
        }
      } else {
        // throw Connection FailedException if the database was not connected to
        throw new ConnectionFailedException("Unable to connect to the database.");
      }
    }
    // return whether the accountTypeName was updated successfully
    return complete;
  }

  /**
   * Update the interest rate for this account type.
   * @param interestRate The interest rate to be updated to.
   * @param id The id of the accountType.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public static boolean updateAccountTypeInterestRate(BigDecimal interestRate, int id)
      throws ConnectionFailedException {
    // set that the account type interest was updated as false for default
    boolean complete = false;
    // ensure the interest rate and id are valid
    if (interestRate.compareTo(BigDecimal.ONE) < 0 && interestRate.compareTo(BigDecimal.ZERO) >= 0
        && id > 0) {
      // connect to the database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // ensure the connection was successful
      if (connection != null) {
        // try to update the account type interest rate for the given account
        // type
        complete = DatabaseUpdater.updateAccountTypeInterestRate(
            interestRate.setScale(2, BigDecimal.ROUND_HALF_UP), id, connection);
        try {
          // ensure the connection is closed
          connection.close();
        } catch (SQLException e) {
          System.out.println("Looks like it was closed already!");
        }
      } else {
        // throw Connection FailedException if the database was not connected to
        throw new ConnectionFailedException("Unable to connect to the database.");
      }
    }
    // return whether the account type interest rate was updated successfully
    return complete;
  }
}
