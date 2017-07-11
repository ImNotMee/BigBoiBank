package com.bank.bank;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import com.bank.machines.AutomatedTellerMachine;
import com.bank.machines.TellerTerminal;
import com.bank.users.Admin;
import com.bank.users.Customer;
import com.bank.users.Teller;
import com.bank.users.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;


public class Bank {
  
  /**
   * This is the main method to run your entire program! Follow the Candy Cane instructions to
   * finish this off.
   * @param argv unused.
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
      } else if (argv[0].equals("1")) {
        mode = "admin";
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
          // find all the roles in the database
          List<Integer> roleIds = DatabaseSelectHelper.getRoles();
          // loop through each role until admin is found
          for (Integer id : roleIds) {
            System.out.println(Integer.valueOf(id));
            if (DatabaseSelectHelper.getRole(id).equals("ADMIN")) {
              // create the admin of the database         
              DatabaseInsertHelper.insertNewUser("Jayden Arquelada", 18, "1", id, "racecar");
              System.out.println("Admin added with ID: " + DatabaseSelectHelper.getUserRole(1));
              break;
            }
          }
          // catches if the table has already been initialized
        } catch (ConnectionFailedException e) {
          // tell the user it has already been initialized
          System.out.println("This database already has already been initialized.");
        }
      } else if (mode.equals("admin")) {
        System.out.println("You are in admin mode, please enter the password of the admin.");
        System.out.print("Password: ");
        currentInput = inputReader.readLine();
        // loop through until the password is correct
        // create a user
        Admin b = (Admin) DatabaseSelectHelper.getUserDetails(1);        
        while (!b.authenticate(currentInput)) {
          System.out.print("Incorrect password please try again. Password: ");
          currentInput = inputReader.readLine();
        }
        do {
          // ask what the admin would like to do
          System.out.println("Please input the number of what you would like to do and press "
              + "enter"); 
          System.out.println("1 - add a Teller \r0 - Exit");
          currentInput = inputReader.readLine();
          // add a teller if the input is 1
          if (currentInput.equals("1")) {
            // ask for the name of the Teller
            System.out.print("Input the name of the Teller: ");
            String name;
            name = inputReader.readLine();
            // ask for the age of the Teller
            System.out.print("Input the age of the Teller: ");
            String age = inputReader.readLine();
            // loop until the age is valid
            while (!age.matches("^[0-9]*$") || age.length() == 0 || Integer.valueOf(age) == 0) {
              System.out.print("Invalid age, please input a valid age: ");
              age = inputReader.readLine();
            }
            // ask for the address of the Teller
            System.out.print("Input the address of the Teller (100 character limit): ");
            String address = inputReader.readLine();
            // loop until the length is valid
            while (address.length() > 100) {
              System.out.print("Address is too long! Input the address of the Teller (100 character"
                  + " limit): ");
              address = inputReader.readLine();
            }
            // variable to hold the role id
            int roleId = -1;
            // get the role Ids
            List<Integer> roleIds = DatabaseSelectHelper.getRoles();
            // find the id of Teller
            for (Integer id : roleIds) {
              if (DatabaseSelectHelper.getRole(id).equals("TELLER")) {
                roleId = id;
                break;
              }
            }
            // ask for the password of the Teller
            System.out.print("Input the password of the Teller: ");
            String password = inputReader.readLine();
            // input the teller into the database
            int id = DatabaseInsertHelper.insertNewUser(name, Integer.valueOf(age), address, roleId,
                password);
            // state the id of the created teller
            System.out.println("Teller successfully added with ID: " + String.valueOf(id));
          }
        } while (!currentInput.equals("0"));
        try {
          connection.close();
        } catch (Exception e) {
          System.out.println("Looks like it was closed already :)");
        }
        System.exit(0);
      }
      // create a context menu where the User can interact through a Teller or Atm interface
      do {
        System.out.println("Please input the number of the interface you would like to interact "
            + "through and press enter:");
        System.out.println("1 - Teller Terminal\r2 - AutomatedTellerMachine (ATM)\r0 - Exit ");
        currentInput = inputReader.readLine();
        // prompt the user for their id and password, as a Teller
        if (currentInput.equals("1")) {
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
              System.out.println("1 - Set and authenticate new user\r2 - Make new user"
                  + "\r3 - Make new Account\r4 - Give interest\r5 - Make a deposit"
                  + "\r6 - Make a withdrawal\r7 - Check balance\r8 - Close customer session"
                  + "\r9 - exit");
              tellerOption = inputReader.readLine();
              // authenticate the current Customer
              if (tellerOption.equals("1")) {
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
                  tellerTerminal.setCurrentCustomer((Customer) user);
                  // try to authenticate the password
                  System.out.print("Please input the password of the Customer.");
                  String customerPassword = inputReader.readLine();
                  // try to authenticate the current customer
                  tellerTerminal.authenticateCurrentCustomer(customerPassword);
                } else {
                  System.out.println("The given ID does not belong to a customer");
                }
                // make a new Customer
              } else if (tellerOption.equals("2")) {
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
                tellerTerminal.makeNewUser(customerName, Integer.valueOf(customerAge), 
                    customerAddress, password);
                // make a new Account and give it to the current Customer
              } else if (tellerOption.equals("3")) {
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
                if (tellerTerminal.makeNewAccount(name, new BigDecimal(balance), 
                     Integer.valueOf(typeId))) {
                  // state the id of the created Customer
                  System.out.println("Account successfully added with ID: " 
                      + String.valueOf(tellerTerminal.listAccounts().get(
                          tellerTerminal.listAccounts().size() - 1).getId()));
                } else {
                  System.out.println("Account was not successfully added.");
                }
                // give interest to the current Customer
              } else if (tellerOption.equals("4")) {
                // ask for the account id of the current customer
                System.out.print("Input the ID of the Account you would like to add interest to, "
                    + "for the current Customer.");
                String accountId = inputReader.readLine();
                // loop until a valid number is given
                while (!accountId.matches("^[0-9]*$") || accountId.length() == 0) {
                  System.out.print("Invalid ID. Please try again: ");
                  accountId = inputReader.readLine();
                }
                // try to give interest 
                tellerTerminal.giveInterest(Integer.valueOf(accountId));
                // make a deposit to the current Customer
              } else if (tellerOption.equals("5")) {
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
                System.out.print("Input the ID of the Account you would like to deposit to, for the"
                    + " current Customer.");
                String accountId = inputReader.readLine();
                // loop until a valid number is given
                while (!accountId.matches("^[0-9]*$")  || accountId.length() == 0) {
                  System.out.print("Invalid ID. Please try again: ");
                  accountId = inputReader.readLine();
                }
                // check if the deposit was successful
                boolean success = tellerTerminal.makeDeposit(new BigDecimal(deposit), 
                    Integer.valueOf(accountId));
                if (success) {
                  System.out.println("Desposit of " + deposit.toString() + " was successful. New"
                      + "balance: " 
                      + tellerTerminal.checkBalance(Integer.valueOf(accountId)).toString());
                }
                // make a withdrawal from the current Customer
              } else if (tellerOption.equals("6")) {
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
                System.out.print("Input the ID of the Account you would like to withdraw from, for "
                    + "the current Customer.");
                String accountId = inputReader.readLine();
                // loop until a valid number is given
                while (!accountId.matches("^[0-9]*$") || accountId.length() == 0) {
                  System.out.print("Invalid ID. Please try again: ");
                  accountId = inputReader.readLine();
                }
                // check if the withdrawal was successful
                boolean success = tellerTerminal.makeWithdrawal(new BigDecimal(withdrawal), 
                    Integer.valueOf(accountId));
                if (success) {
                  System.out.println("The withdrawal of " + withdrawal + " was successful. New "
                      + "balance: " 
                      + tellerTerminal.checkBalance(Integer.valueOf(accountId)).toString());
                }
                // check the balance of the current Customer
              } else if (tellerOption.equals("7")) {
                // ask for the account id of the current customer
                System.out.print("Input the ID of the Account you would like to check the balance "
                    + "for, for the current Customer.");
                String accountId = inputReader.readLine();
                // loop until a valid number is given
                while (!accountId.matches("^[0-9]*$") || accountId.length() == 0) {
                  System.out.print("Invalid ID. Please try again: ");
                  accountId = inputReader.readLine();
                }
                // ensure this account can be accessed
                if (tellerTerminal.checkBalance(Integer.valueOf(accountId)) != null) {
                  System.out.println("This account has " 
                      + tellerTerminal.checkBalance(Integer.valueOf(accountId)).toString());
                }
                
                // close the current Customer session
              } else if (tellerOption.equals("8")) {
                // remove the current Customer
                tellerTerminal.deAuthenticateCustomer();
              }
            } while (!tellerOption.equals("9"));
          } else {
            System.out.println("Teller was not authenticated");
          }          
        } else if (currentInput.equals("2")) {
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
            System.out.println("1 - List Accounts and Balances\r2 - Make a deposit"
                + "\r3 - Check balance\r4 - Make a withdrawal\r5 - Exit");
            customerOption = inputReader.readLine();
            // list accounts and balances
            if (customerOption.equals("1")) {
              // get the accounts for the current Customer
              List<Account> accounts = automatedTellerMachine.listAccounts();
              if (accounts != null) {
                System.out.println("The account names, IDs, and their balances are: ");
                // string to hold all the accounts and their balance
                String ret = "";
                // loop through each account
                for (Account curr : accounts) {
                  ret += "\'" + curr.getName() + "\', " + curr.getId() +  ", " 
                      + curr.getBalance().toString() + "; ";
                }
                System.out.println(ret);
              }           
              // make a deposit
            } else if (customerOption.equals("2")) {
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
              System.out.print("Input the ID of the Account you would like to deposit to.");
              String accountId = inputReader.readLine();
              // loop until a valid number is given
              while (!accountId.matches("^[0-9]*$") || accountId.length() == 0) {
                System.out.print("Invalid ID. Please try again: ");
                accountId = inputReader.readLine();
              }
              // check if the deposit was successful
              boolean success = automatedTellerMachine.makeDeposit(new BigDecimal(deposit), 
                  Integer.valueOf(accountId));
              if (success) {
                System.out.println("Desposit of " + deposit.toString() + " was successful. New "
                    + "balance: " 
                    + automatedTellerMachine.checkBalance(Integer.valueOf(accountId)).toString());
              }
              // check balance
            } else if (customerOption.equals("3")) {
              // ask for the account id of the current customer
              System.out.print("Input the ID of the Account you would like to check the balance "
                  + "for");
              String accountId = inputReader.readLine();
              // loop until a valid number is given
              while (!accountId.matches("^[0-9]*$") || accountId.length() == 0) {
                System.out.print("Invalid ID. Please try again: ");
                accountId = inputReader.readLine();
              }
              if (automatedTellerMachine.checkBalance(Integer.valueOf(accountId)) != null) {
                System.out.println("This account has " 
                    + automatedTellerMachine.checkBalance(Integer.valueOf(accountId)).toString());
              }             
              // make a withdrawal
            } else if (customerOption.equals("4")) {
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
              System.out.print("Input the ID of the Account you would like to withdraw from ");
              String accountId = inputReader.readLine();
              // loop until a valid number is given
              while (!accountId.matches("^[0-9]*$") || accountId.length() == 0) {
                System.out.print("Invalid ID. Please try again: ");
                accountId = inputReader.readLine();
              }
              // check if the withdrawal was successful
              boolean success = automatedTellerMachine.makeWithdrawal(new BigDecimal(withdrawal), 
                  Integer.valueOf(accountId));
              if (success) {
                System.out.println("The withdrawal of " + withdrawal + " was successful. New "
                    + "balance: " 
                    + automatedTellerMachine.checkBalance(Integer.valueOf(accountId)).toString());
              }
            } 
            
          } while (!customerOption.equals("5"));
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
