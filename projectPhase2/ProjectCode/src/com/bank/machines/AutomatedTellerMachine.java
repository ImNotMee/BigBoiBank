package com.bank.machines;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.users.Customer;


public class AutomatedTellerMachine extends BankServiceSystems {
  
  /**
   * Instantiate an AutomatedTellerMachine with a Customer and their possible password.
   * @param customerId The id of the Customer.
   * @param password The possible password of the Customer.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public AutomatedTellerMachine(int customerId, String password) throws ConnectionFailedException {
    // create a Customer object from the information in the database
    this.currentCustomer = (Customer) DatabaseSelectHelper.getUserDetails(customerId);
    // ensure the customer has the correct password if the customer exists
    if (this.currentCustomer != null) {
      this.currentCustomerAuthenticated = currentCustomer.authenticate(password);
    }
  }
  
  /**
   * Instantiate an AutomatedTellerMachine with a Customer.
   * @param customerId The id of the Customer.
   * @throws ConnectionFailedException If database was not successfully connected to.
   */
  public AutomatedTellerMachine(int customerId) throws ConnectionFailedException {
    // create a Customer object from the information in the database
    this.currentCustomer = (Customer) DatabaseSelectHelper.getUserDetails(customerId);
  }
  
}
