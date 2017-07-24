package com.bank.machines;

import java.util.List;

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
  }
  
  /**
   * Get all the ids of the messages for the current teller.
   * @param id The id of the user to get the messages for.
   * @return The ids of all the messages.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  public List<Integer> getMessageIds() throws ConnectionFailedException {
    return DatabaseSelectHelper.getMessageIds(this.currentUser.getId());
  }
}
