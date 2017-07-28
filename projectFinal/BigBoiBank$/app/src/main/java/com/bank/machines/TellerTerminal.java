package com.bank.machines;

import android.content.Context;

import java.util.List;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.users.Customer;

public class TellerTerminal extends BankWorkerServiceSystems {

  /**
   * Initialize a TellerTerminal with a teller. Must pass authentication on the first try.
   * 
   * @param tellerId The id of the teller.
   * @param password The password of the teller.
   */
  public TellerTerminal(int tellerId, String password, Context context) {
    insertor = new DatabaseInsertHelper(context);
    selector = new DatabaseSelectHelper(context);
    updater = new DatabaseUpdateHelper(context);
    // create a Customer object from the information in the database
    this.currentUser = selector.getUserDetails(tellerId);
    // ensure the customer has the correct password
    this.currentUserAuthenticated = currentUser.authenticate(password);
  }
  
  /**
   * Get all the ids of the messages for the current teller.
   * @return The ids of all the messages.
   */
  public List<Integer> getUserMessageIds() {
    return selector.getMessageIds(this.currentUser.getId());
  }
  
  /**
   * Leave a new message for a user.
   * @param message The message for the user.
   * @param userId The id of the user who the message is for.
   * @return The id of the message, or -1 if it was unsuccessful.
   */
  @Override
  public int leaveMessage(String message, int userId) {
    if (this.currentUserAuthenticated) {
      // tellers can only leave messages for Customers
      if (selector.getUserDetails(userId) instanceof Customer) {
        
      }
      return insertor.insertMessage(userId, message);
    } else {
      return -1;
    }
  }
}
