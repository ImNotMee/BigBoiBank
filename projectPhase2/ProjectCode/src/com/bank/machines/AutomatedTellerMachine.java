package com.bank.machines;

import java.math.BigDecimal;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.users.Customer;

import java.math.BigDecimal;

public class AutomatedTellerMachine extends BankServiceSystems {

  /**
   * Instantiate an AutomatedTellerMachine with a Customer and their possible password.
   * 
   * @param customerId The id of the Customer.
   * @param password The possible password of the Customer.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public AutomatedTellerMachine(int customerId, String password) throws ConnectionFailedException {
    // create a Customer object from the information in the database
    this.currentCustomer = (Customer) DatabaseSelectHelper.getUserDetails(customerId);
    // ensure the customer has the correct password if the customer exists
    if (this.currentCustomer != null) {
      this.authenticateCurrentCustomer(password);
    }

  }

  /**
   * Instantiate an AutomatedTellerMachine with a Customer.
   * 
   * @param customerId The id of the Customer.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public AutomatedTellerMachine(int customerId) throws ConnectionFailedException {
    // create a Customer object from the information in the database
    this.currentCustomer = (Customer) DatabaseSelectHelper.getUserDetails(customerId);
  }

  /**
   * Withdraw money from a given account.
   * 
   * @param amount The amount of money to be withdrawn.
   * @param accountId The id of the account.
   * @return true if the money was successfully withdrawn, false otherwise
   * @throws ConnectionFailedException If database was not successfully connected to.
   * @throws IllegalAmountException If the amount to be deposited is not valid.
   * @throws InsufficientFundsException If the account does not have enough funds to be withdrawn.
   */
  public boolean makeWithdrawal(BigDecimal amount, int accountId)
      throws ConnectionFailedException, IllegalAmountException, InsufficientFundsException {
    boolean success = false;
    // check if RestrictedSavingsAccount
    if (DatabaseSelectHelper.getAccountTypeName(DatabaseSelectHelper.getAccountType(accountId))
        .equalsIgnoreCase("RESTRICTEDSAVING")) {
      System.out.println("Please see a teller to withdraw from this account");
      return success;
    // use parent withdrawal method
    } else {
      success = super.makeWithdrawal(amount, accountId);
      return success;
    }
  }
}
