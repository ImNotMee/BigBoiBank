package com.bank.machines;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.users.Teller;

public class TellerTerminal extends BankWorkerServiceSystems {

  /**
   * Initialize a TellerTerminal with a teller. Must pass authentication on the first try.
   * 
   * @param tellerId The id of the teller.
   * @param password The password of the teller.
   * @throws ConnectionFailedException If the database was not successfully connected to.
   */
  public TellerTerminal(int tellerId, String password) throws ConnectionFailedException {
    // create a Customer object from the information in the database
    this.currentUser = (Teller) DatabaseSelectHelper.getUserDetails(tellerId);
    // ensure the customer has the correct password
    this.currentUserAuthenticated = currentUser.authenticate(password);

    // Prints out the user info if the user is authenicated
    if (this.currentCustomerAuthenticated) {
      System.out.println(this.printDetails());
    }
  }
}
