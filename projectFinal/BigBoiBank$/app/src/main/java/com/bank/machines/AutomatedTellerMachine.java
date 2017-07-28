package com.bank.machines;

import android.content.Context;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;

import java.math.BigDecimal;

public class AutomatedTellerMachine extends BankServiceSystems {

  /**
   * Instantiate an AutomatedTellerMachine with a Customer and their possible password.
   * 
   * @param customerId The id of the Customer.
   * @param password The possible password of the Customer.
   */
  public AutomatedTellerMachine(int customerId, String password, Context context) {
    this.context = context;
    this.insertor = new DatabaseInsertHelper(this.context);
    this.selector = new DatabaseSelectHelper(this.context);
    this.updater = new DatabaseUpdateHelper(this.context);
    // create a Customer object from the information in the database
    this.currentCustomer = selector.getUserDetails(customerId);
    // ensure the customer has the correct password if the customer exists
    if (this.currentCustomer != null) {
      this.authenticateCurrentCustomer(password);
    }

  }

  /**
   * Instantiate an AutomatedTellerMachine with a Customer.
   * 
   * @param customerId The id of the Customer.
   */
  public AutomatedTellerMachine(int customerId) {
    // create a Customer object from the information in the database
    this.currentCustomer = selector.getUserDetails(customerId);
  }

  /**
   * Withdraw money from a given account.
   * 
   * @param amount The amount of money to be withdrawn.
   * @param accountId The id of the account.
   * @return true if the money was successfully withdrawn, false otherwise
   * @throws IllegalAmountException If the amount to be deposited is not valid.
   * @throws InsufficientFundsException If the account does not have enough funds to be withdrawn.
   */
  public boolean makeWithdrawal(BigDecimal amount, int accountId)
      throws IllegalAmountException, InsufficientFundsException {
    boolean success = false;
    // check if RestrictedSavingsAccount
    if (selector.getAccountTypeName(selector.getAccountType(accountId))
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
