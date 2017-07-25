package com.bank.bank;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import com.bank.generics.RolesEnumMap;
import com.bank.machines.AdminTerminal;
import com.bank.machines.AutomatedTellerMachine;
import com.bank.machines.BankServiceSystems;
import com.bank.machines.BankWorkerServiceSystems;
import com.bank.machines.TellerTerminal;
import com.bank.users.Admin;
import com.bank.users.Customer;
import com.bank.users.Teller;
import com.bank.users.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


public class Bank {
  
  /**
   * Runs the bank.
   * @param argv Used to tell if initialize mode or a terminal.
   */
  public static void main(String[] argv) {
    Connection connection = DatabaseDriverExtender.connectOrCreateDataBase();
    try {
      BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
      // Used as the variable to hold the current input of the user
      String currentInput;
      // check what mode will be used
      String mode;
      if (argv.length == 0) {
        mode = "user";
      } else if (argv[0].equals("-1")) {
        mode = "initialize";
      } else {
        mode = "user";
      }
      if (mode.equals("initialize")) {        
        // try to see if the first account is an admin
        try {
          // initialize an empty database
          DatabaseDriverExtender.initialize(connection);
          // initialize the roles into the table
          initializeRoleTable(connection);
          // initialize the account types into the table
          initializeAccountTypes(connection);
          // check if argv contains -1
          System.out.println("Database has been initialized.");
          RolesEnumMap map = new RolesEnumMap();
          DatabaseInsertHelper.insertNewUser("Jayden Arquelada", 18, "1", map.getRoleId("ADMIN"), "racecar");
          System.out.println("Admin added with ID: 1");
          // catches if the table has already been initialized
        } catch (ConnectionFailedException e) {
          // tell the user it has already been initialized
          System.out.println("This database already has already been initialized.");
        }
      } 
      // create a context menu where the User can interact through an Admin, Teller or ATM interface
      do {
        System.out.println("Please input the number of the interface you would like to interact "
            + "through and press enter:");
        System.out.println("1 - Admin Terminal\n2 - Teller Terminal\n3 - AutomatedTellerMachine "
            + "(ATM)\n0 - Exit ");
        
        currentInput = inputReader.readLine();
        if (currentInput.equals("1")) {
          System.out.print("You are in admin mode, please enter your admin ID: ");
          String adminId = inputReader.readLine();
          // loop until a valid Id is given
          while (!adminId.matches("^[0-9]*$")  || adminId.length() == 0) {
            System.out.print("Invalid ID. Please try again: ");
            adminId = inputReader.readLine();
          }
          System.out.print("Please enter your password: ");
          String adminPassword = inputReader.readLine();
          // variable to hold whether the Teller Terminal can be accessed
          boolean access = false;
          // ensure the id is valid
          if (DatabaseSelectHelper.getRole(DatabaseSelectHelper.getUserRole(
              Integer.valueOf(adminId))).equals("ADMIN")) {
            // check that the password is correct
            Admin admin = (Admin) DatabaseSelectHelper.getUserDetails(Integer.valueOf(adminId));
            access = admin.authenticate(adminPassword);
          } else {
            System.out.println("ID does not belong to an Admin.");
          }
          if (access) {
            AdminTerminal adminTerminal = new AdminTerminal(Integer.valueOf(adminId), 
                adminPassword);
            String adminOption;
            System.out.println("Please input the number of what you would like to do and press "
                + "enter (Options 3 - 7 are available only if the current Customer is set and "
                + "authenticated): ");
            do {
              // ask what the admin would like to do
              System.out.println("Please input the number of what you would like to do and press "
                  + "enter"); 
              System.out.println("1 - Set and authenticate new Customer\n2 - Make new Customer"
                  + "\n3 - Make new Account\n4 - Give interest\n5 - Make a deposit"
                  + "\n6 - Make a withdrawal\n7 - Check balance\n8 - Close customer session"
                  + "\n9 - List Customer Accounts\n10 - View balance of Customer Accounts"
                  + "\n11 - Make new Admin\n12 - Make new Teller\n13 - View current Customers"
                  + "\n14 - View current Tellers\n15 - View current Admins"
                  + "\n16 - Promote Teller to Admin\n17 - Update Customer Name"
                  + "\n18 - Update Customer Address\n19 - Update Customer Age"
                  + "\n20 - Update Customer Password\n21 - See Available Message Ids"
                  + "\n22 - See Customer Message Ids\n23 - See Specific Message"
                  + "\n24 - Leave Message\n25 - Transfer funds\n26 - Back up Database"
                  + "\n27 - View money in bank\n28 - Exit");
              adminOption = inputReader.readLine();
              // authenticate the current Customer
              if (adminOption.equals("1")) {
                setAndAuthenticateCustomerOption(adminTerminal, inputReader);
                // make a new customer
              } else if (adminOption.equals("2")) {
                makeCustomerOption(adminTerminal, inputReader);
                // make a new account for the customer
              } else if (adminOption.equals("3")) {
                makeAccountOption(adminTerminal, inputReader);
                // give interest to the an account
              } else if (adminOption.equals("4")) {
                giveInterestOption(adminTerminal, inputReader);
                // make a deposit to the current Customer
              } else if (adminOption.equals("5")) {
                makeDepositOption(adminTerminal, inputReader);
                // make a withdrawal from the current Customer
              } else if (adminOption.equals("6")) {
                makeWithdrawalOption(adminTerminal, inputReader);
                // check the balance of the current Customer
              } else if (adminOption.equals("7")) {
                checkBalanceOption(adminTerminal, inputReader);
                // close the current Customer session
              } else if (adminOption.equals("8")) {
                closeCustomerSessionOption(adminTerminal);
                // list the accounts of the customer
              } else if (adminOption.equals("9")) {
                listCustomerAccountsOption(adminTerminal);
                // see the total money of the current customer
              } else if (adminOption.equals("10")) {
                viewCustomerTotalBalance(adminTerminal);
                // make a new admin
              } else if (adminOption.equals("11")) {
                makeNewAdminOption(adminTerminal, inputReader);
                // make a new teller
              } else if (adminOption.equals("12")) {
                makeNewTellerOption(adminTerminal, inputReader);
                // show all the current customers
              } else if (adminOption.equals("13")) {
                viewUsersOption(adminTerminal, "CUSTOMER");
              } else if (adminOption.equals("14")) {
                viewUsersOption(adminTerminal, "TELLER");
              } else if (adminOption.equals("15")) {
                viewUsersOption(adminTerminal, "ADMIN");
                // promote a teller to an admin
              } else if (adminOption.equals("16")) {
                promoteTellerOption(adminTerminal, inputReader);
                // update the customer's name
              } else if (adminOption.equals("17")) {
                updateNameOption(adminTerminal, inputReader);
                // update the customer's address
              } else if (adminOption.equals("18")) {
                updateAddressOption(adminTerminal, inputReader);
                // update the customer's age
              } else if (adminOption.equals("19")) {
                updateAgeOption(adminTerminal, inputReader);
                // update the customer's password
              } else if (adminOption.equals("20")) {
                updatePasswordOption(adminTerminal, inputReader);
                // see available message id's
              } else if (adminOption.equals("21")) {
                viewUserMessageIds(adminTerminal);
                // view message id's the customer can view
              } else if (adminOption.equals("22")) {
                viewCustomerMessageIds(adminTerminal);
                // see a specific message
              } else if (adminOption.equals("23")) {
                viewSpecificMessage(adminTerminal, inputReader);
                // leave a message
              } else if (adminOption.equals("24")) {
                leaveMessage(adminTerminal, inputReader);
                // transfer funds between 2 accounts
              } else if (adminOption.equals("25")) {
                transferFundsOption(adminTerminal, inputReader);
                // back up the database
              } else if (adminOption.equals("26")) {
                backUpDatabase(adminTerminal);
                // see total money in the bank
              } else if (adminOption.equals("27")) {
                viewBankTotalBalance(adminTerminal);
              }
            } while (!adminOption.equals("28"));
            try {
              connection.close();
            } catch (Exception e) {
              System.out.println("Looks like it was closed already :)");
            }
          }
          
          // prompt the user for their id and password, as a Teller
        } else if (currentInput.equals("2")) {
          System.out.println("TELLER TERMINAL");
          System.out.print("Please enter your Teller ID: ");
          String tellerId = inputReader.readLine();
          // loop until a valid Id is given
          while (!tellerId.matches("^[0-9]*$")  || tellerId.length() == 0) {
            System.out.print("Invalid ID. Please try again: ");
            tellerId = inputReader.readLine();
          }
          System.out.print("Please enter your Teller password: ");
          String tellerPassword = inputReader.readLine();
          // variable to hold whether the Teller Terminal can be accessed
          boolean access = false;
          // ensure the id is valid
          if (DatabaseSelectHelper.getRole(DatabaseSelectHelper.getUserRole(
              Integer.valueOf(tellerId))).equals("TELLER")) {
            // check that the password is correct
            Teller teller = (Teller) DatabaseSelectHelper.getUserDetails(Integer.valueOf(tellerId));
            access = teller.authenticate(tellerPassword);
          } else {
            System.out.println("ID does not belong to a Teller.");
          }
          // give them the Teller Terminal Interface if they are authenticated
          if (access) {
            // Create a Teller Terminal
            TellerTerminal tellerTerminal = new TellerTerminal(Integer.valueOf(tellerId), 
                tellerPassword);
            String tellerOption;
            System.out.println("Please input the number of what you would like to do and press "
                + "enter (Options 3 - 7 are available only if the current Customer is set and "
                + "authenticated): ");
            do {
              System.out.println("1 - Set and authenticate new Customer\n2 - Make new Customer"
                  + "\n3 - Make new Account\n4 - Give interest\n5 - Make a deposit"
                  + "\n6 - Make a withdrawal\n7 - Check balance\n8 - Close customer session"
                  + "\n9 - See Customer Accounts\n10 - Update Customer Name"
                  + "\n11 - Update Customer Address\n12 - Update Customer Age"
                  + "\n13 - Update Customer Password\n14 - See Available Message Ids"
                  + "\n15 - See Customer Message Ids\n16 - See Specific Message\n17 - Leave Message"
                  + "\n18 - Transfer funds\n19 - Exit");
              tellerOption = inputReader.readLine();
              // authenticate the current Customer
              if (tellerOption.equals("1")) {
                setAndAuthenticateCustomerOption(tellerTerminal, inputReader);
                // make a new Customer
              } else if (tellerOption.equals("2")) {
                makeCustomerOption(tellerTerminal, inputReader);
                // make a new Account and give it to the current Customer
              } else if (tellerOption.equals("3")) {
                makeAccountOption(tellerTerminal, inputReader);
                // give interest to the current Customer
              } else if (tellerOption.equals("4")) {
                giveInterestOption(tellerTerminal, inputReader);
                // make a deposit to the current Customer
              } else if (tellerOption.equals("5")) {
                makeDepositOption(tellerTerminal, inputReader);
                // make a withdrawal from the current Customer
              } else if (tellerOption.equals("6")) {
                makeWithdrawalOption(tellerTerminal, inputReader);
                // check the balance of the current Customer
              } else if (tellerOption.equals("7")) {
                checkBalanceOption(tellerTerminal, inputReader);                
                // close the current Customer session
              } else if (tellerOption.equals("8")) {
                closeCustomerSessionOption(tellerTerminal);
                //list the accounts of the customer
              } else if (tellerOption.equals("9")) {
                listCustomerAccountsOption(tellerTerminal);
                // update the customer's name
              } else if (tellerOption.equals("10")) {
                updateNameOption(tellerTerminal, inputReader);
                // update the customer's address
              } else if (tellerOption.equals("11")) {
                updateAddressOption(tellerTerminal, inputReader);
                // update the customer's age
              } else if (tellerOption.equals("12")) {
                updateAgeOption(tellerTerminal, inputReader);
                // update the customer's password
              } else if (tellerOption.equals("13")) {
                updatePasswordOption(tellerTerminal, inputReader);
                // view message id's the current user can see
              } else if (tellerOption.equals("14")) {
                viewUserMessageIds(tellerTerminal);
                // view message id's the customer can view
              } else if (tellerOption.equals("15")) {
                viewCustomerMessageIds(tellerTerminal);
                // view a specific message
              } else if (tellerOption.equals("16")) {
                viewSpecificMessage(tellerTerminal, inputReader);
                // leave a message
              } else if (tellerOption.equals("17")) {
                leaveMessage(tellerTerminal, inputReader);
                // transfer funds between 2 accounts
              } else if (tellerOption.equals("18")) {
                transferFundsOption(tellerTerminal, inputReader);
              }
            } while (!tellerOption.equals("19"));
          } else {
            System.out.println("Teller was not authenticated");
          }          
        } else if (currentInput.equals("3")) {
          System.out.println("AUTOMATED TELLER MACHINE");
          // variable to see if the Customer is authenticated
          boolean authenticated = false;
          // holds the customer Id
          String customerId;
          // holds the customer password
          String customerPassword;
          // loop until the Customer is authenticated
          do {
            System.out.print("Please enter your Customer ID: ");
            customerId = inputReader.readLine();
            // loop until a valid Id is given
            while (!customerId.matches("^[0-9]*$") || customerId.length() == 0) {
              System.out.print("Invalid ID. Please try again: ");
              customerId = inputReader.readLine();
            }
            System.out.print("Please enter your Customer password: ");
            customerPassword = inputReader.readLine();
            // ensure the id is valid
            if (DatabaseSelectHelper.getRole(DatabaseSelectHelper.getUserRole(
                Integer.valueOf(customerId))).equals("CUSTOMER")) {
              // check that the password is correct
              Customer customer = (Customer) DatabaseSelectHelper.getUserDetails(
                  Integer.valueOf(customerId));
              authenticated = customer.authenticate(customerPassword);
            }
            if (!DatabaseSelectHelper.getRole(DatabaseSelectHelper.getUserRole(
                Integer.valueOf(customerId))).equals("CUSTOMER")) {
              System.out.println("That is not the ID of a customer.");
            } else if (!authenticated) {
              System.out.println("Customer ID or password is wrong.");
            }
          } while (!authenticated);
          
          String customerOption;
          // Create an ATM
          AutomatedTellerMachine automatedTellerMachine = new AutomatedTellerMachine(
              Integer.valueOf(customerId), customerPassword);
          System.out.println("Please input the number of what you would like to do and press "
              + "enter: ");
          // see what the user wants to do
          do {
            System.out.println("1 - List Accounts and Balances\n2 - Make a deposit"
                + "\n3 - Check balance\n4 - Make a withdrawal\n5 - See available message Ids"
                + "\n6 - See specific message\n7 - Transfer funds\n8 - Exit");
            customerOption = inputReader.readLine();
            // list accounts and balances
            if (customerOption.equals("1")) {
              listCustomerAccountsOption(automatedTellerMachine); 
              // make a deposit
            } else if (customerOption.equals("2")) {
              makeDepositOption(automatedTellerMachine, inputReader);
              // check balance
            } else if (customerOption.equals("3")) {
              checkBalanceOption(automatedTellerMachine, inputReader);                
              // make a withdrawal
            } else if (customerOption.equals("4")) {
              makeWithdrawalOption(automatedTellerMachine, inputReader);
            } else if (customerOption.equals("5")) {
              viewCustomerMessageIds(automatedTellerMachine);
              // view a specific message
            } else if (customerOption.equals("6")) {
              viewSpecificMessage(automatedTellerMachine, inputReader);
              // transfer funds between 2 accounts
            } else if (customerOption.equals("7")) {
              transferFundsOption(automatedTellerMachine, inputReader);
            }
          } while (!customerOption.equals("8"));
        }   
      } while (!currentInput.equals("0"));
      
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        connection.close();
      } catch (Exception e) {
        System.out.println("Looks like it was closed already!");
      }
    }
  }
  
  /**
   * See the Accounts of the current Customer.
   * @param machine The bank machine to get the accounts from.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  private static void listCustomerAccountsOption(BankServiceSystems machine) 
      throws ConnectionFailedException {
    // get the accounts for the current Customer
    List<Account> accounts = machine.listCustomerAccounts();
    if (accounts != null) {
      machine.printCustomerAccounts();
    }           
  }
  
  /**
   * Deposit money to an account for the current Customer.
   * @param machine The bank machine to deposit money to
   * @param inputReader Used to read input from the User
   * @throws ConnectionFailedException If the database can not be connected to
   * @throws IOException If input or output error occurs
   */
  private static void makeDepositOption(BankServiceSystems machine, BufferedReader inputReader) 
      throws ConnectionFailedException, IOException {
    // loop until a valid deposit is given
    boolean validDeposit = false;
    String deposit = "";
    while (!validDeposit) {
      // ask for the balance of the account
      System.out.print("Input the amount to deposit (must have two decimal places): ");
      deposit = inputReader.readLine();
      try {
        new BigDecimal(deposit);
        validDeposit = true;
      } catch (NumberFormatException e) {
        System.out.println("Deposit is invalid.");
      }
    }
    // ask for the account id of the current customer
    System.out.print("Input the ID of the Account you would like to deposit to: ");
    String accountId = inputReader.readLine();
    // loop until a valid number is given
    while (!accountId.matches("^[0-9]*$") || accountId.length() == 0) {
      System.out.print("Invalid Account ID. Please try again: ");
      accountId = inputReader.readLine();
    }
    // check if the deposit was successful
    boolean success = false;
    try {
      success = machine.makeDeposit(new BigDecimal(deposit), 
          Integer.valueOf(accountId));
    } catch (IllegalAmountException e) {
      System.out.println("Illegal amount to deposit.");
    }
    if (success) {
      System.out.println("Deposit of " + deposit.toString() + " was successful. New "
          + "balance: " + machine.checkBalance(Integer.valueOf(accountId)).toString());
    }
  }
  
  /**
   * Check the balance of an account for the current Customer.
   * @param machine The bank machine to check the balance from
   * @param inputReader Used to read input from the User
   * @throws ConnectionFailedException If the database can not be connected to
   * @throws IOException If there is an error with input or output
   */
  private static void checkBalanceOption(BankServiceSystems machine, BufferedReader inputReader) 
      throws ConnectionFailedException, IOException {
    // ask for the account id of the current customer
    System.out.print("Input the ID of the Account you would like to check the balance "
        + "for: ");
    String accountId = inputReader.readLine();
    // loop until a valid number is given
    while (!accountId.matches("^[0-9]*$") || accountId.length() == 0) {
      System.out.print("Invalid ID. Please try again: ");
      accountId = inputReader.readLine();
    }
    if (machine.checkBalance(Integer.valueOf(accountId)) != null) {
      System.out.println("This account has " 
          + machine.checkBalance(Integer.valueOf(accountId)).toString());
    }             
  }
  
  /**
   * Make a withdrawal from an account for the current Customer.
   * @param machine The bank machine to withdraw from
   * @param inputReader Used to read input from the User
   * @throws ConnectionFailedException If the database can not be connected to
   * @throws IOException If there is an error with input or output
   */
  private static void makeWithdrawalOption(BankServiceSystems machine, BufferedReader inputReader) 
      throws IOException, ConnectionFailedException {
    // loop until a valid withdrawal amount is given
    boolean validWithdrawl = false;
    String withdrawal = "";
    while (!validWithdrawl) {
      // ask for the balance of the account
      System.out.print("Input the amount to withdraw (must have two decimal places): ");
      withdrawal = inputReader.readLine();
      try {
        new BigDecimal(withdrawal);
        validWithdrawl = true;
      } catch (NumberFormatException e) {
        System.out.println("Withdrawal amount is invalid.");
      }
    }
    // ask for the account id of the current customer
    System.out.print("Input the ID of the Account you would like to withdraw from: ");
    String accountId = inputReader.readLine();
    // loop until a valid number is given
    while (!accountId.matches("^[0-9]*$") || accountId.length() == 0) {
      System.out.print("Invalid ID. Please try again: ");
      accountId = inputReader.readLine();
    }
    // check if the withdrawal was successful
    boolean success = false;
    try {
      success = machine.makeWithdrawal(new BigDecimal(withdrawal), 
          Integer.valueOf(accountId));
    } catch (IllegalAmountException e) {
      System.out.println("Illegal amount given to withdraw.");
    } catch (InsufficientFundsException e) {
      System.out.println("You do not have enough money to withdraw this amount.");
    }
    if (success) {
      System.out.println("The withdrawal of " + withdrawal + " was successful. New "
          + "balance: " 
          + machine.checkBalance(Integer.valueOf(accountId)).toString());
    }
  }
  
  /**
   * Set and authenticate the current Customer.
   * @param machine The bank machine to set and authenticate the Customer from
   * @param inputReader Used to read input from the User
   * @throws ConnectionFailedException If the database can not be connected to
   * @throws IOException If there is an error with input or output
   */
  private static void setAndAuthenticateCustomerOption(BankWorkerServiceSystems machine, 
      BufferedReader inputReader) throws ConnectionFailedException, IOException {
    // ask for the customer id
    System.out.print("Input the ID of the customer you would like to load: ");
    String customerId = inputReader.readLine();
    // loop until a valid number is given
    while (!customerId.matches("^[0-9]*$")  || customerId.length() == 0) {
      System.out.print("Invalid ID. Please try again: ");
      customerId = inputReader.readLine();
    }
    // get the Customer of the id
    User user = DatabaseSelectHelper.getUserDetails(Integer.valueOf(customerId));
    // check that the User is a Customer
    if (user instanceof Customer) {
      machine.setCurrentCustomer((Customer) user);
      // try to authenticate the password
      System.out.print("Please input the password of the Customer:");
      String customerPassword = inputReader.readLine();
      // try to authenticate the current customer
      machine.authenticateCurrentCustomer(customerPassword);
    } else {
      System.out.println("The given ID does not belong to a customer");
    }
  }
  
  /**
   * Make a new Customer.
   * @param machine The bank machine to make a Customer from
   * @param inputReader Used to read input from the User
   * @throws ConnectionFailedException If the database can not be connected to
   * @throws IOException If there is an error with input or output
   */
  private static void makeCustomerOption(BankWorkerServiceSystems machine, 
      BufferedReader inputReader) throws ConnectionFailedException, IOException {
    // ask for the name of the new Customer
    System.out.print("Input the name of the Customer: ");
    String customerName;
    customerName = inputReader.readLine();
    // ask for the age of the customer
    System.out.print("Input the age of the Customer: ");
    String customerAge = inputReader.readLine();
    // loop until a valid number is given
    while (!customerAge.matches("^[0-9]*$") || customerAge.length() == 0 
        || Integer.valueOf(customerAge) == 0) {
      System.out.print("Invalid age. Please try again: ");
      customerAge = inputReader.readLine();
    }
    // ask for the address of the Customer
    System.out.print("Input the address of the Customer (100 character limit): ");
    String customerAddress = inputReader.readLine();
    // loop until the length is valid
    while (customerAddress.length() > 100) {
      System.out.print("Address is too long! Input the address of the Customer (100 "
          + "character limit): ");
      customerAddress = inputReader.readLine();
    }
    // ask for the password of the Customer
    System.out.print("Input the password of the Customer: ");
    String password = inputReader.readLine();
    // input the Customer into the database
    int id = machine.makeNewCustomer(customerName, Integer.valueOf(customerAge), 
        customerAddress, password);
    if (id != -1) {
      System.out.println("Customer was successfully added with ID " + String.valueOf(id));
    } else {
      System.out.println("Customer was not successfully added.");
    }
  }
  
  /**
   * Make a new account for the current Customer.
   * @param machine The bank machine to make an Account from
   * @param inputReader Used to read input from the User
   * @throws ConnectionFailedException If the database can not be connected to
   * @throws IOException If there is an error with input or output
   */
  private static void makeAccountOption(BankWorkerServiceSystems machine, 
      BufferedReader inputReader) throws ConnectionFailedException, IOException {
    // ask for the name of the Account
    System.out.print("Input the name of the Account: ");
    String name;
    name = inputReader.readLine();
    // loop until a valid balance is given
    boolean validBalance = false;
    String balance = "";
    while (!validBalance) {
      // ask for the balance of the account
      System.out.print("Input the balance of the Account (must have two decimal "
          + "places): ");
      balance = inputReader.readLine();
      try {
        new BigDecimal(balance);
        validBalance = true;
      } catch (NumberFormatException e) {
        System.out.println("Balance is invalid.");
      }
    }
    // get the Account Id's
    List<Integer> accountIds = DatabaseSelectHelper.getAccountTypesIds();
    // ask which account they would like to make
    System.out.println("Which kind of account would you like to make?");
    for (Integer id : accountIds) {
      System.out.println(id + " - " + DatabaseSelectHelper.getAccountTypeName(id));
    }
    // variable to find what kind of account the User wants
    String typeId;
    typeId = inputReader.readLine();
    while (!(typeId.matches("^[0-9]*$") || typeId.length() == 0) 
        || !accountIds.contains(Integer.valueOf(typeId))) {
      System.out.print("Invalid value for Account Type, try again: ");
      typeId = inputReader.readLine();
    }
    // ensure customer and teller are authenticated
    if (machine.makeNewAccount(name, new BigDecimal(balance), 
         Integer.valueOf(typeId))) {
      // state the id of the created account
      System.out.println("Account successfully added with ID: " 
          + String.valueOf(machine.listCustomerAccounts().get(
              machine.listCustomerAccounts().size() - 1).getId()));
    } else {
      System.out.println("Account was not successfully added.");
    }
  }
  
  /**
   * Give interest to an account for the current Customer.
   * @param machine The bank machine to give interest from
   * @param inputReader Used to read input from the User
   * @throws ConnectionFailedException If the database can not be connected to
   * @throws IOException If there is an error with input or output
   */
  private static void giveInterestOption(BankWorkerServiceSystems machine, 
      BufferedReader inputReader) throws ConnectionFailedException, IOException {
    // ask for the account id of the current customer
    System.out.print("Input the ID of the Account you would like to add interest to, "
        + "for the current Customer: ");
    String accountId = inputReader.readLine();
    // loop until a valid number is given
    while (!accountId.matches("^[0-9]*$") || accountId.length() == 0) {
      System.out.print("Invalid ID. Please try again: ");
      accountId = inputReader.readLine();
    }
    // try to give interest 
    machine.giveInterest(Integer.valueOf(accountId));
  }
  
  /**
   * Close the current Customer session. 
   */
  private static void closeCustomerSessionOption(BankWorkerServiceSystems machine) {
    machine.deAuthenticateCustomer();
  }
  
  /**
   * Make a new Admin.
   * @param machine The bank machine to make the Admin from
   * @param inputReader Used to read input from the User
   * @throws ConnectionFailedException If the database can not be connected to
   * @throws IOException If there is an error with input or output
   */
  private static void makeNewAdminOption(AdminTerminal machine, BufferedReader inputReader) 
      throws ConnectionFailedException, IOException {
    // ask for the name of the new Admin
    System.out.print("Input the name of the Admin: ");
    String adminName;
    adminName = inputReader.readLine();
    // ask for the age of the customer
    System.out.print("Input the age of the Admin: ");
    String adminAge = inputReader.readLine();
    // loop until a valid number is given
    while (!adminAge.matches("^[0-9]*$") || adminAge.length() == 0 
        || Integer.valueOf(adminAge) == 0) {
      System.out.print("Invalid age. Please try again: ");
      adminAge = inputReader.readLine();
    }
    // ask for the address of the Admin
    System.out.print("Input the address of the Admin (100 character limit): ");
    String adminAddress = inputReader.readLine();
    // loop until the length is valid
    while (adminAddress.length() > 100) {
      System.out.print("Address is too long! Input the address of the Admin (100 "
          + "character limit): ");
      adminAddress = inputReader.readLine();
    }
    // ask for the password of the Admin
    System.out.print("Input the password of the Admin: ");
    String password = inputReader.readLine();
    // input the Admin into the database
    int id = machine.makeNewUser(adminName, Integer.valueOf(adminAge), 
        adminAddress, password, "ADMIN");
    if (id != -1) {
      System.out.println("Admin was successfully added with ID " + String.valueOf(id));
    } else {
      System.out.println("Admin was not successfully added.");
    }
  }
  
  /**
   * Make a new Teller.
   * @param machine The bank machine to make the Teller from
   * @param inputReader Used to read input from the User
   * @throws ConnectionFailedException If the database can not be connected to
   * @throws IOException If there is an error with input or output
   */
  private static void makeNewTellerOption(AdminTerminal machine, BufferedReader inputReader) 
      throws ConnectionFailedException, IOException {
    // ask for the name of the new Teller
    System.out.print("Input the name of the Teller: ");
    String tellerName;
    tellerName = inputReader.readLine();
    // ask for the age of the Teller
    System.out.print("Input the age of the Teller: ");
    String tellerAge = inputReader.readLine();
    // loop until a valid number is given
    while (!tellerAge.matches("^[0-9]*$") || tellerAge.length() == 0 
        || Integer.valueOf(tellerAge) == 0) {
      System.out.print("Invalid age. Please try again: ");
      tellerAge = inputReader.readLine();
    }
    // ask for the address of the Teller
    System.out.print("Input the address of the Teller (100 character limit): ");
    String tellerAddress = inputReader.readLine();
    // loop until the length is valid
    while (tellerAddress.length() > 100) {
      System.out.print("Address is too long! Input the address of the Teller (100 "
          + "character limit): ");
      tellerAddress = inputReader.readLine();
    }
    // ask for the password of the Customer
    System.out.print("Input the password of the Customer: ");
    String password = inputReader.readLine();
    // input the Customer into the database
    int id = machine.makeNewUser(tellerName, Integer.valueOf(tellerAge), 
        tellerAddress, password, "TELLER");
    if (id != -1) {
      System.out.println("Teller was successfully added with ID " + String.valueOf(id));
    } else {
      System.out.println("Teller was not successfully added.");
    }
  }
  
  /**
   * Output a list of Users of the given type.
   * @param machine The bank machine to check the view the Users from
   * @param type The type of Users to find
   */
  private static void viewUsersOption(AdminTerminal machine, String type) 
      throws ConnectionFailedException {
    List<User> customers = machine.listUsers(type);
    int user = 1;
    for (User currCustomer : customers) {
      System.out.println("User " + String.valueOf(user));
      System.out.println("--------------");
      System.out.println(currCustomer.toString());
      user++;
    }
  }

  /**
   * Promotes a teller to an admin.
   * @param machine The bank machine to promote the teller from
   * @param inputReader Used to read input from the user
   * @throws ConnectionFailedException If the database can not be connected to
   * @throws IOException If there is an error with the input or output
   */
  private static void promoteTellerOption(AdminTerminal machine, BufferedReader inputReader) 
      throws ConnectionFailedException, IOException {
    System.out.print("Input the ID of the teller you would like to promote to an Admin: ");
    String tellerId = inputReader.readLine();
    // loop until a valid number is given
    while (!tellerId.matches("^[0-9]*$")  || tellerId.length() == 0) {
      System.out.print("Invalid ID. Please try again: ");
      tellerId = inputReader.readLine();
    }
    User user = DatabaseSelectHelper.getUserDetails(Integer.valueOf(tellerId));
    if (user instanceof Teller) {
      if (machine.promoteTellerToAdmin(Integer.valueOf(tellerId))) {
        System.out.println("Teller successfully promoted to admin.");
      } else {
        System.out.println("Teller was not successfully promoted to admin.");
      }
    } else {
      System.out.println("The given ID does not belong to a teller");
    }
  }
  
  /**
   * Change the name of a User in the database.
   * @param machine The machine to change the name on.
   * @param inputReader Used to read input from the user.
   * @throws ConnectionFailedException If the database can not be connected to.
   * @throws IOException If there is an error with the input or output.
   */
  private static void updateNameOption(BankWorkerServiceSystems machine, BufferedReader inputReader) 
      throws ConnectionFailedException, IOException 
    {
    System.out.print("Input the ID of the User who's name you would like to change: ");
    System.out.print("Input the new name of the User: ");
    String name = inputReader.readLine();
    if (machine.updateUserName(name)) {
      System.out.println("Name successfully updated.");
    } else {
      System.out.println("The name was not successfully updated.");
    }
  }
  
  /**
   * Change the age of a User in the database.
   * @param machine The machine to change the age on.
   * @param inputReader Used to read input from the user.
   * @throws ConnectionFailedException If the database can not be connected to.
   * @throws IOException If there is an error with the input or output.
   */
  private static void updateAgeOption(BankWorkerServiceSystems machine, BufferedReader inputReader) 
      throws ConnectionFailedException, IOException 
    {
    System.out.print("Input the new age of the current Customer: ");
    String age = inputReader.readLine();
    // loop until a valid number is given
    while (!age.matches("^[0-9]*$") || age.length() == 0 
        || Integer.valueOf(age) == 0) {
      System.out.print("Invalid age. Please try again: ");
      age = inputReader.readLine();
    }
    if (machine.updateUserAge(Integer.valueOf(age))) {
      System.out.println("Age successfully updated.");
    } else {
      System.out.println("The age was not successfully updated.");
    }
  }
  
  /**
   * Change the age of a User in the database.
   * @param machine The machine to change the age on.
   * @param inputReader Used to read input from the user.
   * @throws ConnectionFailedException If the database can not be connected to.
   * @throws IOException If there is an error with the input or output.
   */
  private static void updateAddressOption(BankWorkerServiceSystems machine, BufferedReader inputReader) 
      throws ConnectionFailedException, IOException 
    {
    System.out.print("Input the new address of the current Customer: ");
    String address = inputReader.readLine();
    // loop until the length is valid
    while (address.length() > 100) {
      System.out.print("Address is too long! Input the address of the Teller (100 "
          + "character limit): ");
      address = inputReader.readLine();
    }
    if (machine.updateUserAddress(address)) {
      System.out.println("Address successfully updated.");
    } else {
      System.out.println("The address was not successfully updated.");
    }
  }
  
  /**
   * Change the password of the current customer in the database.
   * @param machine The machine to change the password on.
   * @param inputReader Used to read input from the user.
   * @throws ConnectionFailedException If the database can not be connected to.
   * @throws IOException If there is an error with the input or output.
   */
  private static void updatePasswordOption(BankWorkerServiceSystems machine, BufferedReader inputReader) 
      throws ConnectionFailedException, IOException 
    {
    System.out.print("Input the new password of the current Customer: ");
    String password = inputReader.readLine();
    if (machine.updateUserPassword(password)) {
      System.out.println("Password successfully updated.");
    } else {
      System.out.println("The password was not successfully updated.");
    }
  }
  
  /**
   * View message ids that the user can view.
   * @param machine The machine to check for the message ids.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  private static void viewCustomerMessageIds(BankServiceSystems machine) 
      throws ConnectionFailedException {
    List<Integer> messageIds = machine.getCustomerMessageIds();
    System.out.println("You can view messages with the following ids.");
    String message = "";
    for (Integer id : messageIds) {
      message += id.toString() + ", ";
    } 
    message = message.substring(0, -2);
    message += '.';
    System.out.println("");
  }
  
  /**
   * View message ids that the user can view.
   * @param machine The machine to check for the message ids.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  private static void viewUserMessageIds(BankWorkerServiceSystems machine) 
      throws ConnectionFailedException {
    List<Integer> userMessageIds = machine.getUserMessageIds();
    List<Integer> customerMessageIds = machine.getCustomerMessageIds();
    System.out.println("You can view messages with the following ids.");
    String message = "";
    for (Integer id : userMessageIds) {
      message += id.toString() + ", ";
    } 
    for (Integer id : customerMessageIds) {
      message += id.toString() + ", ";
    } 
    message = message.substring(0, -2);
    message += '.';
    System.out.println("");
  }
  
  /**
   * View a specific message. If admin is viewing someone else's message, the view status is 
   * unchanged. Otherwise it will be changed.
   * @param machine The machine to view the messages from. 
   * @param inputReader Used to read input from the user.
   * @throws ConnectionFailedException If the database can not be connected to.
   * @throws IOException If there is an error with the input or output.
   */
  private static void viewSpecificMessage(BankServiceSystems machine, BufferedReader inputReader) 
    throws ConnectionFailedException, IOException {
    List<Integer> customerMessageIds = machine.getCustomerMessageIds();
    List<Integer> userMessageIds = new ArrayList<Integer>();
    List<Integer> adminMessageIds = new ArrayList<Integer>();
    // get the message ids the user can view
    if (machine instanceof BankWorkerServiceSystems) {
      userMessageIds = ((BankWorkerServiceSystems) machine).getUserMessageIds();
    }
    // get the message ids that belong to the current admin
    if (machine instanceof AdminTerminal) {
      adminMessageIds = ((AdminTerminal) machine).getAdminMessageIds();
    }
    System.out.print("Input the id of the message you would like to view.");
    String id = inputReader.readLine();
    while (!id.matches("^[0-9]*$")  || id.length() == 0) {
      System.out.print("Invalid ID. Please try again: ");
      id = inputReader.readLine();
    } 
    if (userMessageIds.contains(Integer.valueOf(id)) 
        || customerMessageIds.contains(Integer.valueOf(id)) 
        || adminMessageIds.contains(Integer.valueOf(id))) {
      System.out.println(machine.getMessage(Integer.valueOf(id)));
      // update the message status as long as the an admin is not viewing someone else's message
      if (!(machine instanceof AdminTerminal) || userMessageIds.contains(Integer.valueOf(id))) {
        machine.updateMessageStatus(Integer.valueOf(id));
      }

    } else {
      System.out.println("You can not view that message.");
    }
  }
  
  private static void leaveMessage(BankWorkerServiceSystems machine, BufferedReader inputReader) 
    throws ConnectionFailedException, IOException {
    // ask for the id of the account to leave the message for 
    System.out.print("Input the ID of the user you want to leave the message for: ");
    String id = inputReader.readLine();
    // loop until a valid number is given
    while (!id.matches("^[0-9]*$") || id.length() == 0) {
      System.out.print("Invalid ID. Please try again: ");
      id = inputReader.readLine();
    }
    User user = DatabaseSelectHelper.getUserDetails(Integer.valueOf(id));
    if (user != null) {
      int messageId = -1;
      if (machine instanceof TellerTerminal) {
        if (!(user instanceof Customer)) {
          System.out.println("You can not leave a message for this teller/admin.");
        } else {
          System.out.print("Input the message for the Customer (512 character limit):  ");
          String message = inputReader.readLine();
          // loop until the length is valid
          while (message.length() > 512) {
            System.out.print("Message is too long! Input the message for the Customer (512 "
                + "character limit): ");
            message = inputReader.readLine();
            messageId = machine.leaveMessage(message, Integer.valueOf(id));
          }
        }
      } else {
        System.out.print("Input the message for the User (512 character limit):  ");
        String message = inputReader.readLine();
        // loop until the length is valid
        while (message.length() > 512) {
          System.out.print("Message is too long! Input the message for the User (512 character "
              + "limit): ");
          message = inputReader.readLine();
        }
        messageId = machine.leaveMessage(message, Integer.valueOf(id));
      } 
      if (messageId == -1) {
        System.out.println("Message was not successfully left.");
      } else {
        System.out.println("Message was successfully left with ID: " + String.valueOf(messageId));
      }
    } else {
      System.out.println("The given ID does not belong in the database.");
    }
  }
  
  /**
   * Transfer funds from your account to another account.
   * @param machine The machine to transfer funds on.
   * @param inputReader Used to read input from the user. 
   * @throws ConnectionFailedException If the database can not be connected to.
   * @throws IOException If there is an error with input or output.
   */
  private static void transferFundsOption(BankServiceSystems machine,
      BufferedReader inputReader) throws ConnectionFailedException, IOException {
    // ask for the id of the account to transfer to
    System.out.print("Input the ID of the account you want to transfer funds to: ");
    String destinationId = inputReader.readLine();
    // loop until a valid number is given
    while (!destinationId.matches("^[0-9]*$") || destinationId.length() == 0) {
      System.out.print("Invalid ID. Please try again: ");
      destinationId = inputReader.readLine();
    }
    // loop until a valid transfer amount is given
    boolean validWithdrawl = false;
    String transferAmount = "";
    while (!validWithdrawl) {
      // ask for the amount to transfer
      System.out.print(
          "Input the amount to transfer to this account " + "(must have two decimal places): ");
      transferAmount = inputReader.readLine();
      try {
        new BigDecimal(transferAmount);
        validWithdrawl = true;
      } catch (NumberFormatException e) {
        System.out.println("Transfer amount is invalid.");
      }
    }
    // ask for the account id to transfer from
    System.out.print("Input the ID of the Account you would like to transfer from: ");
    String sourceId = inputReader.readLine();
    // loop until a valid number is given
    while (!sourceId.matches("^[0-9]*$") || sourceId.length() == 0) {
      System.out.print("Invalid ID. Please try again: ");
      sourceId = inputReader.readLine();
    }
    // check if the withdrawal was successful from source account
    boolean success = false;
    try {
      success = machine.makeWithdrawal(new BigDecimal(transferAmount), Integer.valueOf(sourceId));
    } catch (IllegalAmountException e) {
      System.out.println("Illegal amount given to transfer.");
    } catch (InsufficientFundsException e) {
      System.out.println("You do not have enough money to transfer this amount.");
    }
    // try to deposit to the given destination account
    boolean transferSuccess = false;
    if (success) {
      try {
        transferSuccess =
            machine.makeDeposit(new BigDecimal(transferAmount), Integer.valueOf(destinationId));
      } catch (IllegalAmountException e) {
        System.out.println("Illegal amount given to transfer.");
      }
      if (transferSuccess) {
        System.out.println("Transfer of " + transferAmount.toString() + " was successful. New "
            + "balance: " + machine.checkBalance(Integer.valueOf(sourceId)).toString());
      // deposit the amount withdrawn back to source account if transfer fails
      } else {
        System.out.println("Transfer failed, account specified might not exist!");
        try {
          transferSuccess =
              machine.makeDeposit(new BigDecimal(transferAmount), Integer.valueOf(sourceId));
        } catch (IllegalAmountException e) {
          System.out.println("Illegal amount given to transfer.");
        }
      }
    }
  }
  
  /**
   * Serializes the database and saves the ser file in w/e mangz.
   * @param machine the admin terminal.
   */
  private static void backUpDatabase(AdminTerminal machine) {
    if (machine.backUpDatabase("/home/ricky/koolaid.ser")) {
      System.out.println("Kill all hoomans");
    } else {
      System.out.println("Dont kill all hoomans");
    }
  }
  
  /**
   * View the total amount of money of the current customer.
   * @param machine The machine to get the total balance from.
   * @throws ConnectionFailedException If the database can not be connected to.
   */
  private static void viewCustomerTotalBalance(BankWorkerServiceSystems machine) 
      throws ConnectionFailedException {
    BigDecimal totalBalance = machine.getTotalBalance();
    System.out.println("The total balance the current customer has is " + totalBalance.toString());
  }
  
  private static void viewBankTotalBalance(AdminTerminal machine) throws ConnectionFailedException {
    BigDecimal totalBalance = machine.getTotalBankBalance();
    System.out.println("The total amount of money in the bank is: " + totalBalance.toString());
    
  }
  
  /**
   * Initialize all the roles in the Roles Table. Reads from the Roles Enum.
   * @param connection The connection to the database.
   */
  private static void initializeRoleTable(Connection connection) {
    String roleStr = "";
    try {
      for (Roles role : Roles.values()) {
        roleStr = role.toString();
        DatabaseInsertHelper.insertRole(roleStr);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Initialize all the Account Types in the AccountTypes table. Reads from the AccountTypes Enum.
   * @param connection The connection to the database.
   */
  private static void initializeAccountTypes(Connection connection) {
    String accountTypeStr = "";
    String interestRate = "0.2";
    try {
      for (AccountTypes accountTypes : AccountTypes.values()) {
        accountTypeStr = accountTypes.toString();
        DatabaseInsertHelper.insertAccountType(accountTypeStr, new BigDecimal(interestRate));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
