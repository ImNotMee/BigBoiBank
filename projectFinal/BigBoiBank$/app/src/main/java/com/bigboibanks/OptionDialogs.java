package com.bigboibanks;

import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bank.accounts.Account;
import com.bank.accounts.RestrictedSavingsAccount;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.generics.AccountTypesEnumMap;
import com.bank.machines.AdminTerminal;
import com.bank.machines.BankServiceSystems;
import com.bank.machines.BankWorkerServiceSystems;
import com.bank.machines.TellerTerminal;
import com.bank.users.Customer;
import com.bank.users.Teller;
import com.bank.users.User;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public abstract class OptionDialogs {

  /**
   * A method that makes a new user.
   * @param machine , the type of terminal.
   * @param user ,a string that indicates the type of user using the bank.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void makeUserDialog(final BankWorkerServiceSystems machine, final String user, final Context context) {
    final Dialog makeUser = new Dialog(context);
    makeUser.setContentView(R.layout.make_user);
    makeUser.getWindow().setBackgroundDrawable(null);
    RelativeLayout layout = (RelativeLayout) makeUser.findViewById(R.id.makeUser);
    TextView title = (TextView) layout.findViewById(R.id.title);
    // Checks with type of user
    if (user.equals("admin")) {
      title.setText(context.getText(R.string.makeAdmin));
    } else if (user.equals("teller")) {
      title.setText(context.getText(R.string.makeTeller));
    } else if (user.equals("customer")) {
      title.setText(context.getText(R.string.makeCustomer));
    }
    final EditText inputName = (EditText) layout.findViewById(R.id.name);
    final EditText inputAge = (EditText) layout.findViewById(R.id.age);
    final EditText inputAddress = (EditText) layout.findViewById(R.id.address);
    final EditText inputPassword = (EditText) layout.findViewById(R.id.password);
    final EditText inputConfirmPassword = (EditText) layout.findViewById(R.id.confirmPassword);
    final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
    final Button confirm = (Button) layout.findViewById(R.id.confirm);
    confirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        boolean validInput = true;
        String confirmationMessage = "";
        String name = inputName.getText().toString();
        if (name.length() == 0) {
          confirmationMessage += context.getString(R.string.invalidName);
          validInput = false;
        }
        int age = 0;
        try {
          age = Integer.parseInt(inputAge.getText().toString());
        } catch (NumberFormatException e) {
          confirmationMessage += context.getString(R.string.invalidAge);
          validInput = false;
        }
        String address = inputAddress.getText().toString();
        if (address.length() >= 100) {
          confirmationMessage += context.getString(R.string.invalidAddress);
          validInput = false;
        }
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();
        if (!password.equals(confirmPassword)) {
          confirmationMessage += context.getString(R.string.passwordNoMatch);
          validInput = false;
        }
        if (validInput) {
          int id;
          if (machine instanceof AdminTerminal) {
            id = ((AdminTerminal) machine).makeNewUser(name, age, address, password, user.toUpperCase());
          } else {
            id = machine.makeNewCustomer(name, age, address, password);
          }
          if (id != -1) {
            confirmationMessage += context.getString(R.string.userAdded);
            confirmationMessage += String.valueOf(id);
            confirm.setText(context.getString(R.string.back));
            confirm.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                makeUser.dismiss();
              }
            });
          } else {
            confirmationMessage += context.getString(R.string.userNotAdded);
          }
        }
        confirmMessage.setText(confirmationMessage);
      }
    });
    makeUser.show();
  }

  /**
   * A method that creates a new account for the current customer in the terminal.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void makeAccountDialog(final BankWorkerServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_LONG).show();
    } else {
      final Dialog makeAccount = new Dialog(context);
      makeAccount.setContentView(R.layout.make_account);
      makeAccount.getWindow().setBackgroundDrawable(null);
      RelativeLayout layout = (RelativeLayout) makeAccount.findViewById(R.id.makeAccount);
      final EditText inputName = (EditText) layout.findViewById(R.id.accountName);
      final EditText inputBalance = (EditText) layout.findViewById(R.id.balance);
      RadioGroup accountTypes = (RadioGroup) layout.findViewById(R.id.accounts);
      final RadioButton tfsa = (RadioButton) accountTypes.findViewById(R.id.tfsa);
      final RadioButton chequing = (RadioButton) accountTypes.findViewById(R.id.chequing);
      final RadioButton savings = (RadioButton) accountTypes.findViewById(R.id.savings);
      final RadioButton restrictedSavings = (RadioButton) accountTypes.findViewById(R.id.restrictedSavings);
      final RadioButton balanceOwing = (RadioButton) accountTypes.findViewById(R.id.balanceOwing);
      final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button confirm = (Button) makeAccount.findViewById(R.id.makeAccount).findViewById(R.id.confirm);
      confirm.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String accountType = "";
          if (tfsa.isChecked()) {
            accountType = "TFSA";
          } else if (chequing.isChecked()) {
            accountType = "CHEQUING";
          } else if (savings.isChecked()) {
            accountType = "SAVING";
          } else if (restrictedSavings.isChecked()) {
            accountType = "RESTRICTEDSAVING";
          } else if (balanceOwing.isChecked()) {
            accountType = "BALANCEOWING";
          }
          boolean validInput = true;
          // Find the account type ID
          AccountTypesEnumMap AccountEnum = new AccountTypesEnumMap(context);
          String confirmationMessage = "";
          String name = inputName.getText().toString();
          java.math.BigDecimal balance = java.math.BigDecimal.ZERO;
          try {
            balance = java.math.BigDecimal.valueOf(Double.parseDouble(inputBalance.getText().toString()));
            balance = balance.setScale(2, RoundingMode.HALF_UP);
            if (balance.doubleValue() < 0.00 && !accountType.equals("balanceOwing")) {
              confirmationMessage = context.getString(R.string.invalidAmount);
              validInput = false;
            }
          } catch (NumberFormatException e) {
            confirmationMessage += context.getString(R.string.invalidAmount);
            validInput = false;
          }
          if (validInput) {

            int AccountTypeID = AccountEnum.getAccountId(accountType);
            if (machine.makeNewAccount(name, balance, AccountTypeID)) {
              confirmationMessage += context.getString(R.string.accountAdded);
              confirm.setText(context.getString(R.string.back));
              confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  makeAccount.dismiss();
                }
              });
            } else {
              confirmationMessage += context.getString(R.string.accountNotAdded);
            }
          }
          confirmMessage.setText(confirmationMessage);
        }
      });
      makeAccount.show();
    }
  }

  /**
   * A method that
   * @param machine , the type of terminal.
   * @param transaction ,the type of transaction.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void moneyTransactionDialog(final BankServiceSystems machine, final String transaction, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_LONG).show();
    } else {
      final Dialog makeTransaction = new Dialog(context);
      makeTransaction.setContentView(R.layout.money_transaction);
      makeTransaction.getWindow().setBackgroundDrawable(null);
      RelativeLayout layout = (RelativeLayout) makeTransaction.findViewById(R.id.layout);
      TextView title = (TextView) layout.findViewById(R.id.title);
      if (transaction.equals("deposit")) {
        title.setText(context.getText(R.string.makeDeposit));
      } else if (transaction.equals("withdrawal")) {
        title.setText(context.getText(R.string.makeWithdrawal));
      }
      final EditText inputAccountId = (EditText) layout.findViewById(R.id.account);
      final EditText inputAmount = (EditText) layout.findViewById(R.id.amount);
      final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button confirm = (Button) layout.findViewById(R.id.confirm);
      confirm.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          boolean validInput = true;
          String confirmationMessage = "";
          int id = -1;
          try {
            id = Integer.parseInt(inputAccountId.getText().toString());
            if (!new DatabaseSelectHelper(context).getAccountIds(machine.getCurrentCustomer().getId()).contains(id)) {
              confirmationMessage += context.getString(R.string.noAccountAccess);
              validInput = false;
            }
          } catch (NumberFormatException e) {
            confirmationMessage += context.getString(R.string.invalidId);
            validInput = false;
          }
          java.math.BigDecimal amount = new java.math.BigDecimal(BigInteger.ZERO);
          try {
            amount = new java.math.BigDecimal(inputAmount.getText().toString());
            amount = amount.setScale(2, RoundingMode.CEILING);
          } catch (NumberFormatException e) {
            confirmationMessage += context.getString(R.string.invalidAmount);
            validInput = false;
          }
          if (validInput) {
            if (transaction.equals("deposit")) {
              try {
                machine.makeDeposit(amount, id);
                confirm.setText(context.getString(R.string.back));
                confirm.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    makeTransaction.dismiss();
                  }
                });
                confirmationMessage += context.getString(R.string.transactionCompleted);
                DatabaseSelectHelper selector = new DatabaseSelectHelper(context);
                confirmationMessage += selector.getBalance(id).toString();
              } catch (IllegalAmountException e) {
                confirmationMessage += context.getString(R.string.invalidAmount);
              }
            } else {
              try {
                confirm.setText(context.getString(R.string.back));
                confirm.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    makeTransaction.dismiss();
                  }
                });
                DatabaseSelectHelper selector = new DatabaseSelectHelper(context);
                if (selector.getAccountDetails(id) instanceof RestrictedSavingsAccount) {
                  confirmationMessage += context.getString(R.string.withdrawWithWorker);
                } else if (machine.makeWithdrawal(amount, id)) {
                  confirmationMessage += context.getString(R.string.transactionCompleted);
                  confirmationMessage += selector.getBalance(id).toString();

                } else {
                  confirmationMessage += context.getString(R.string.transactionFailed);
                }

              } catch (IllegalAmountException e) {
                confirmationMessage += context.getString(R.string.invalidAmount);
              } catch (InsufficientFundsException e) {
                confirmationMessage += context.getString(R.string.insufficientFunds);
              }
            }

          }
          confirmMessage.setText(confirmationMessage);
        }
      });
      makeTransaction.show();

    }
  }

  /**
   * A method that sets the customer for the terminal.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void setCustomerDialog(final BankWorkerServiceSystems machine, final Context context) {
    final Dialog setCustomer = new Dialog(context);
    setCustomer.setContentView(R.layout.set_customer);
    setCustomer.getWindow().setBackgroundDrawable(null);
    RelativeLayout layout = (RelativeLayout) setCustomer.findViewById(R.id.layout);
    final EditText inputId = (EditText) layout.findViewById(R.id.id);
    final EditText inputPassword = (EditText) layout.findViewById(R.id.password);
    final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
    final Button confirm = (Button) layout.findViewById(R.id.confirm);
    confirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        boolean validInput = true;
        String confirmationMessage = "";
        int id;
        User user = null;
        try {
          id = Integer.parseInt(inputId.getText().toString());
          DatabaseSelectHelper selector = new DatabaseSelectHelper(context);
          user = selector.getUserDetails(id);
          if (!(user instanceof Customer)) {
            confirmationMessage += context.getString(R.string.notCustomer);
            validInput = false;
          }
        } catch (NumberFormatException e) {
          confirmationMessage += context.getString(R.string.invalidId);
          validInput = false;
        }
        if (validInput) {
          machine.setCurrentCustomer((Customer) user);
          if (machine.authenticateCurrentCustomer(inputPassword.getText().toString())) {
            confirmationMessage += context.getString(R.string.customerSet);
            showCurrentCustomerDialog(machine, context);
            confirm.setText(context.getString(R.string.back));
            confirm.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                setCustomer.dismiss();
              }
            });
          } else {
            confirmationMessage += context.getString(R.string.incorrectPassword);
          }
        }
        confirmMessage.setText(confirmationMessage);
      }
    });
    setCustomer.show();
  }

  /**
   * A method that lists all the accounts of the current customer.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void listAccountsDialog(final BankServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_LONG).show();
    } else {
      List<Account> accounts = machine.listCustomerAccounts();
      if (accounts.size() == 0) {
        Toast.makeText(context, context.getString(R.string.noAccounts), Toast.LENGTH_LONG).show();
      } else {
        final Dialog makeTransaction = new Dialog(context);
        makeTransaction.setContentView(R.layout.list_accounts);
        makeTransaction.getWindow().setBackgroundDrawable(null);
        RelativeLayout layout = (RelativeLayout) makeTransaction.findViewById(R.id.layout);
        final ScrollView scrollView = (ScrollView) layout.findViewById(R.id.scrollView);
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (Account currAccount : accounts) {
          RelativeLayout details = (RelativeLayout) inflater.inflate(R.layout.account, null);
          String id = context.getString(R.string.accountId) + String.valueOf(currAccount.getId());
          String name = context.getString(R.string.accountName) + currAccount.getName();
          String balance = context.getString(R.string.accountBalance) + currAccount.getBalance().toString();
          String type = context.getString(R.string.accountType) + new DatabaseSelectHelper(context).getAccountTypeName(currAccount.getType());
          ((TextView) details.findViewById(R.id.id)).setText(id);
          ((TextView) details.findViewById(R.id.name)).setText(name);
          ((TextView) details.findViewById(R.id.balance)).setText(balance);
          ((TextView) details.findViewById(R.id.type)).setText(type);
          ((LinearLayout) scrollView.findViewById(R.id.linearLayout)).addView(details);

        }
        makeTransaction.show();
      }

    }
  }

  /**
   * A method that checks the balance of a given account.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void checkBalanceDialog(final BankServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_LONG).show();
    } else {
      final Dialog checkBalance = new Dialog(context);
      checkBalance.setContentView(R.layout.one_input);
      checkBalance.getWindow().setBackgroundDrawable(null);
      RelativeLayout layout = (RelativeLayout) checkBalance.findViewById(R.id.layout);
      final EditText inputAccountId = (EditText) layout.findViewById(R.id.input);
      final TextView balance = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button check = (Button) layout.findViewById(R.id.confirm);
      check.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          boolean validInput = true;
          String confirmationMessage = "";
          int id = -1;
          try {
            id = Integer.parseInt(inputAccountId.getText().toString());
            if (!new DatabaseSelectHelper(context).getAccountIds(machine.getCurrentCustomer().getId()).contains(id)) {
              confirmationMessage += context.getString(R.string.noAccountAccess);
              validInput = false;
            }
          } catch (NumberFormatException e) {
            confirmationMessage += context.getString(R.string.invalidId);
            validInput = false;
          }
          if (validInput) {
            confirmationMessage += context.getString(R.string.balance);
            confirmationMessage += machine.checkBalance(id).toString();
          }
          balance.setText(confirmationMessage);
        }
      });
      checkBalance.show();
    }
  }

  /**
   * A method that gives interest to the selected account.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.t
   */
  public static void giveInterestDialog(final BankWorkerServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_LONG).show();
    } else {
      final Dialog giveInterest = new Dialog(context);
      giveInterest.setContentView(R.layout.one_input);
      giveInterest.getWindow().setBackgroundDrawable(null);
      RelativeLayout layout = (RelativeLayout) giveInterest.findViewById(R.id.layout);
      ((TextView) layout.findViewById(R.id.title)).setText(context.getString(R.string.giveInterest));
      final EditText inputAccountId = (EditText) layout.findViewById(R.id.input);
      final TextView balance = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button check = (Button) layout.findViewById(R.id.confirm);
      check.setText(R.string.confirm);
      check.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          boolean validInput = true;
          String confirmationMessage = "";
          int id = -1;
          try {
            id = Integer.parseInt(inputAccountId.getText().toString());
            if (!new DatabaseSelectHelper(context).getAccountIds(machine.getCurrentCustomer().getId()).contains(id)) {
              confirmationMessage += context.getString(R.string.invalidId);
              validInput = false;
            }
          } catch (NumberFormatException e) {
            confirmationMessage += context.getString(R.string.invalidId);
            validInput = false;
          }
          if (validInput) {
            if (machine.giveInterest(id)) {
              confirmationMessage += context.getString(R.string.interestAdded);
              confirmationMessage += machine.checkBalance(id).toString();
            } else {
              confirmationMessage += context.getString(R.string.interestNotAdded);
            }
          }
          balance.setText(confirmationMessage);
        }
      });
      giveInterest.show();
    }
  }

  /**
   * A method that updates the name of the current customer.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void updateNameDialog(final BankWorkerServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_LONG).show();
    } else {
      final Dialog updateName = new Dialog(context);
      updateName.setContentView(R.layout.one_input);
      updateName.getWindow().setBackgroundDrawable(null);
      RelativeLayout layout = (RelativeLayout) updateName.findViewById(R.id.layout);
      ((TextView) layout.findViewById(R.id.title)).setText(context.getString(R.string.updateName));
      final EditText inputName = (EditText) layout.findViewById(R.id.input);
      inputName.setInputType(InputType.TYPE_CLASS_TEXT);
      inputName.setHint(context.getString(R.string.promptName));
      final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button update = (Button) layout.findViewById(R.id.confirm);
      update.setText(R.string.update);
      update.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String confirmationMessage = "";
          String name = inputName.getText().toString();
          if (name.length() == 0) {
            confirmationMessage += context.getString(R.string.invalidName);
          } else {
            machine.updateUserName(name);
            confirmationMessage += context.getString(R.string.nameUpdated);
          }
          confirmMessage.setText(confirmationMessage);
        }
      });
      updateName.show();
    }
  }

  /**
   * A method that updates the age of the current customer.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void updateAgeDialog(final BankWorkerServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_LONG).show();
    } else {
      final Dialog updateAge = new Dialog(context);
      updateAge.setContentView(R.layout.one_input);
      updateAge.getWindow().setBackgroundDrawable(null);
      RelativeLayout layout = (RelativeLayout) updateAge.findViewById(R.id.layout);
      ((TextView) layout.findViewById(R.id.title)).setText(context.getString(R.string.updateAge));
      final EditText inputAge = (EditText) layout.findViewById(R.id.input);
      inputAge.setHint(context.getString(R.string.promptAge));
      final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button update = (Button) layout.findViewById(R.id.confirm);
      update.setText(R.string.update);
      update.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String confirmationMessage = "";
          try {
            int age = Integer.parseInt(inputAge.getText().toString());
            if (age != 0) {
              machine.updateUserAge(age);
              confirmationMessage += context.getString(R.string.ageUpdated);
            } else {
              confirmationMessage += context.getString(R.string.invalidAge);
            }
          } catch (NumberFormatException e) {
            confirmationMessage += context.getString(R.string.invalidAge);
          }
          confirmMessage.setText(confirmationMessage);
        }
      });
      updateAge.show();
    }
  }

  /**
   * A method that updates the address of the current customer.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void updateAddressDialog(final BankWorkerServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_LONG).show();
    } else {
      final Dialog updateAddress = new Dialog(context);
      updateAddress.setContentView(R.layout.one_input);
      updateAddress.getWindow().setBackgroundDrawable(null);
      RelativeLayout layout = (RelativeLayout) updateAddress.findViewById(R.id.layout);
      ((TextView) layout.findViewById(R.id.title)).setText(context.getString(R.string.updateAddress));
      final EditText inputAddress = (EditText) layout.findViewById(R.id.input);
      inputAddress.setInputType(InputType.TYPE_CLASS_TEXT);
      inputAddress.setHint(context.getString(R.string.promptAddress));
      final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button update = (Button) layout.findViewById(R.id.confirm);
      update.setText(R.string.update);
      update.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String confirmationMessage = "";
          String address = inputAddress.getText().toString();
          if (address.length() == 0 || address.length() > 100) {
            confirmationMessage += context.getString(R.string.invalidAddress);
          } else {
            machine.updateUserAddress(address);
            confirmationMessage += context.getString(R.string.addressUpdated);
          }
          confirmMessage.setText(confirmationMessage);
        }
      });
      updateAddress.show();
    }
  }

  /**
   * A method that updates the password ot the current customer.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void updatePasswordDialog(final BankWorkerServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_LONG).show();
    } else {
      final Dialog updatePassword = new Dialog(context);
      updatePassword.getWindow().setBackgroundDrawable(null);
      updatePassword.setContentView(R.layout.update_password);
      RelativeLayout layout = (RelativeLayout) updatePassword.findViewById(R.id.layout);
      final EditText inputPassword = (EditText) layout.findViewById(R.id.password);
      final EditText confirmPassword = (EditText) layout.findViewById(R.id.confirmPassword);
      final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button update = (Button) layout.findViewById(R.id.confirm);
      update.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String confirmationMessage = "";
          if (inputPassword.getText().toString().equals(confirmPassword.getText().toString())) {
            machine.updateUserPassword(inputPassword.getText().toString());
            confirmationMessage += context.getString(R.string.passwordUpdated);
          } else {
            confirmationMessage += context.getString(R.string.passwordNoMatch);
          }
          confirmMessage.setText(confirmationMessage);
        }
      });
      updatePassword.show();
    }
  }

  /**
   * A method that transfers funds from one account to another account.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void transferFunds(final BankServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_LONG).show();
    } else {
      final Dialog transferFunds = new Dialog(context);
      transferFunds.getWindow().setBackgroundDrawable(null);
      transferFunds.setContentView(R.layout.transfer_funds);
      RelativeLayout layout = (RelativeLayout) transferFunds.findViewById(R.id.layout);
      final EditText transferFrom = (EditText) layout.findViewById(R.id.transferFrom);
      final EditText transferTo = (EditText) layout.findViewById(R.id.transferTo);
      final EditText inputAmount = (EditText) layout.findViewById(R.id.amount);
      final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button transfer = (Button) layout.findViewById(R.id.transfer);
      transfer.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String confirmationMessage = "";
          try {
            int fromId = Integer.parseInt(transferFrom.getText().toString());
            int toId = Integer.parseInt(transferTo.getText().toString());
            DatabaseSelectHelper selector = new DatabaseSelectHelper(context);
            if (selector.getAccountIds(machine.getCurrentCustomer().getId()).contains(fromId)
                    && selector.getAccountType(toId) != -1) {
              try {
                BigDecimal amount = new BigDecimal(inputAmount.getText().toString());
                amount = amount.setScale(2, RoundingMode.HALF_UP);
                machine.makeWithdrawal(amount, fromId);
                if (machine.makeDeposit(amount, toId)) {
                  confirmationMessage += context.getString(R.string.transferCompleted);
                } else {
                  confirmationMessage += context.getString(R.string.transferFailed);
                }
              } catch (NumberFormatException | IllegalAmountException e) {
                confirmationMessage += context.getString(R.string.invalidAmount);
              } catch (InsufficientFundsException e) {
                confirmationMessage += context.getString(R.string.insufficientFunds);
              } catch (Exception e) {
                confirmationMessage += context.getString(R.string.noAccountAccess);
              }
            } else {
              confirmationMessage += context.getString(R.string.invalidId);
            }
          } catch (NumberFormatException e) {
            confirmationMessage += context.getString(R.string.invalidId);
          }

          confirmMessage.setText(confirmationMessage);
        }
      });
      transferFunds.show();
    }
  }

  /**
   * A method that shows the current customer's accounts balances.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void viewUserBalanceDialog(final BankWorkerServiceSystems machine, final Context context) {
    final Dialog customerBalance = new Dialog(context);
    customerBalance.setContentView(R.layout.one_input);
    customerBalance.getWindow().setBackgroundDrawable(null);
    RelativeLayout layout = (RelativeLayout) customerBalance.findViewById(R.id.layout);
    ((TextView) layout.findViewById(R.id.title)).setText(context.getString(R.string.customerBalance));
    final EditText inputID = (EditText) layout.findViewById(R.id.input);
    inputID.setHint(context.getString(R.string.promptUserId));
    final TextView balance = (TextView) layout.findViewById(R.id.confirmationMessage);
    final Button check = (Button) layout.findViewById(R.id.confirm);
    check.setText(R.string.check);
    check.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String confirmationMessage = "";
        try {
          int id = Integer.parseInt(inputID.getText().toString());
          User user = new DatabaseSelectHelper(context).getUserDetails(id);
          if (user instanceof Customer) {
            confirmationMessage += context.getString(R.string.totalBalance);
            confirmationMessage += machine.getTotalBalance(id).toString();
          } else {
            confirmationMessage += context.getString(R.string.notCustomer);
          }
        } catch (NumberFormatException e) {
          confirmationMessage += context.getString(R.string.invalidId);
        }
        balance.setText(confirmationMessage);
      }
    });
    customerBalance.show();
  }

  /**
   * A method that leaves a message to the given ID of the user.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void leaveMessage(final BankServiceSystems machine, final Context context) {
    final Dialog leaveMessage = new Dialog(context);
    leaveMessage.setContentView(R.layout.leave_message);
    leaveMessage.getWindow().setBackgroundDrawable(null);
    RelativeLayout layout = (RelativeLayout) leaveMessage.findViewById(R.id.layout);
    // references to the editText and TextEdits
    final TextView notification = (TextView) layout.findViewById(R.id.notification);
    final EditText inputId = (EditText) layout.findViewById(R.id.inputId);
    final EditText inputMessage = (EditText) layout.findViewById(R.id.inputMessage);
    final Button confirm = (Button) layout.findViewById(R.id.confirmButton);
    // if the customer clicks confirm
    confirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String confirmationMessage = "";
        try {
          int userId = Integer.parseInt(inputId.getText().toString());
          String message = inputMessage.getText().toString();
          DatabaseSelectHelper selector = new DatabaseSelectHelper(context);
          boolean canLeaveMessage = true;
          if (machine instanceof TellerTerminal) {
            if (!(selector.getUserDetails(userId) instanceof Customer)) {
              confirmationMessage += context.getString(R.string.cantLeaveMessage);
              canLeaveMessage = false;
            }
          }
          if (canLeaveMessage) {
            if (selector.getUserDetails(userId) != null) {
              int id = ((BankWorkerServiceSystems) machine).leaveMessage(message, userId);
              confirmationMessage += context.getString(R.string.messageLeft) + String.valueOf(id);
            }
          }
        } catch (NumberFormatException e) {
          confirmationMessage += context.getString(R.string.invalidId);
        }
        notification.setText(confirmationMessage);
      }
    });
    leaveMessage.show();
  }

  /**
   * A method that shows all the messages' IDs.
   * @param machine , the type of terminal..
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void showMessageIds(final BankServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_LONG).show();
    } else if (machine.getCustomerMessageIds().size() == 0){
      Toast.makeText(context, context.getString(R.string.noMessages), Toast.LENGTH_LONG).show();
    } else {
      final Dialog dialog = new Dialog(context);
      dialog.setContentView(R.layout.list_message_ids);
      dialog.getWindow().setBackgroundDrawable(null);
      final RelativeLayout parent = (RelativeLayout) dialog.findViewById(R.id.parent);
      final ScrollView scrollView = (ScrollView) parent.findViewById(R.id.scrollView);
      final LinearLayout layout = (LinearLayout) scrollView.findViewById(R.id.layout);
      // show the user all of the message Ids that are for them
      try {
        ArrayList<Integer> ids = (ArrayList<Integer>) machine.getCustomerMessageIds();
        // now add each id to the scroll view using a textview
        for (Integer id : ids) {
          TextView textView = new TextView(context);
          String text = context.getString(R.string.idPrefix) + Integer.toString( id);
          textView.setText(text);
          layout.addView(textView);
        }
      } catch (Exception e) {
        e.getMessage();
      }
      dialog.show();
    }
  }

  /**
   * A method that shows a particular message based on the given ID of the message.
   * @param machine , the type of terminal..
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void seeSpecificMessage(final BankServiceSystems machine, final Context context) {
    final Dialog dialog = new Dialog(context);
    dialog.setContentView(R.layout.one_input);
    dialog.getWindow().setBackgroundDrawable(null);
    // change the text in the layout
    ((TextView) dialog.findViewById(R.id.title)).setText(context.getString(R.string.seeSpecificMessage));
    ((EditText) dialog.findViewById(R.id.input)).setHint(context.getString(R.string.inputMessageId));
    final EditText input = (EditText) dialog.findViewById(R.id.input);
    final TextView confirmationMessage = (TextView) dialog.findViewById(R.id.confirmationMessage);
    final Button button = (Button) dialog.findViewById(R.id.confirm);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          int id = Integer.parseInt(input.getText().toString());
          // admins can see all messages
          if (machine instanceof AdminTerminal) {
            String message = machine.getMessage(id);
            confirmationMessage.setText(message);
          } else {
            ArrayList<Integer> ids;
            if (machine instanceof TellerTerminal) {
              ids = (ArrayList<Integer>) ((TellerTerminal) machine).getUserMessageIds();
            } else {
              ids = (ArrayList<Integer>) machine.getCustomerMessageIds();
            }
            if (ids.contains(id)) {
              String message = machine.getMessage(id);
              confirmationMessage.setText(message);
            } else {
              confirmationMessage.setText(context.getString(R.string.noAccess));
            }
          }
        } catch (Exception e) {
          confirmationMessage.setText(R.string.invalidId);
        }
      }
    });
    dialog.show();
  }

  /**
   * A method that shows the information of the current customer.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void showCurrentCustomerDialog(final BankServiceSystems machine, final Context context) {
    Customer customer = (Customer) machine.getCurrentCustomer();
    final Dialog showCustomer = new Dialog(context);
    showCustomer.setContentView(R.layout.customer_log_in);
    showCustomer.getWindow().setBackgroundDrawable(null);
    LinearLayout layout = (LinearLayout) showCustomer.findViewById(R.id.layout);
    final LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    RelativeLayout details = (RelativeLayout) inflater.inflate(R.layout.account, null);
    String id = context.getString(R.string.userID) + String.valueOf(customer.getId());
    String name = context.getString(R.string.userName) + customer.getName();
    String address = context.getString(R.string.userAddress) + customer.getAddress();
    String type = context.getString(R.string.userAge) + String.valueOf(customer.getAge());
    ((TextView) details.findViewById(R.id.id)).setText(id);
    ((TextView) details.findViewById(R.id.name)).setText(name);
    ((TextView) details.findViewById(R.id.balance)).setText(address);
    ((TextView) details.findViewById(R.id.type)).setText(type);
    layout.addView(details);
    showCustomer.show();
  }

  /**
   * A method that lists all of the current users. of a given role.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   * @param role , the type of the user.
   */
  public static void listCurrentUserDialog(final AdminTerminal machine, final Context context, String role) {
    List<User> users = machine.listUsers(role);
    if (users.size() == 0) {
      Toast.makeText(context, context.getString(R.string.noCurrentUser), Toast.LENGTH_LONG).show();
    } else {
      final Dialog makeTransaction = new Dialog(context);
      makeTransaction.setContentView(R.layout.list_users);
      makeTransaction.getWindow().setBackgroundDrawable(null);
      RelativeLayout layout = (RelativeLayout) makeTransaction.findViewById(R.id.layout);
      String header;
      if (role.equals("ADMIN")) {
        header = context.getString(R.string.viewAdmins);
      } else if (role.equals("TELLER")) {
        header = context.getString(R.string.viewTellers);
      } else {
        header = context.getString(R.string.viewCustomers);
      }
      ((TextView) layout.findViewById(R.id.title)).setText(header);
      final ScrollView scrollView = (ScrollView) layout.findViewById(R.id.scrollView);
      final LayoutInflater inflater = (LayoutInflater) context
              .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      for (User user : users) {
        RelativeLayout details = (RelativeLayout) inflater.inflate(R.layout.account, null);
        String id = context.getString(R.string.userID) + String.valueOf(user.getId());
        String name = context.getString(R.string.userName) + user.getName();
        String address = context.getString(R.string.userAddress) + user.getAddress();
        String type = context.getString(R.string.userAge) + String.valueOf(user.getAge());
        ((TextView) details.findViewById(R.id.id)).setText(id);
        ((TextView) details.findViewById(R.id.name)).setText(name);
        ((TextView) details.findViewById(R.id.balance)).setText(address);
        ((TextView) details.findViewById(R.id.type)).setText(type);
        ((LinearLayout) scrollView.findViewById(R.id.linearLayout)).addView(details);

      }
      makeTransaction.show();
    }

  }

  /**
   * A method that shows all of the message available to the current user.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void showUserMessageIds(final BankWorkerServiceSystems machine, final Context context) {
    ArrayList<Integer> ids = (ArrayList<Integer>) machine.getUserMessageIds();
    if (ids.size() == 0) {
      Toast.makeText(context, context.getString(R.string.noMessages), Toast.LENGTH_LONG).show();
    } else {
      final Dialog dialog = new Dialog(context);
      dialog.setContentView(R.layout.list_message_ids);
      dialog.getWindow().setBackgroundDrawable(null);
      final LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.layout);
      // show the user all of the message Ids that are for them
      // now add each id to the scroll view using a textview
      for (Integer id : ids) {
        TextView textView = new TextView(context);
        String text = context.getString(R.string.idPrefix) + Integer.toString((int) id);
        textView.setText(text);
        layout.addView(textView);
      }
      dialog.show();
    }
  }

  /**
   * A method that changes a user into a Teller.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void promoteTeller(final AdminTerminal machine, final Context context) {
    final Dialog promoteTeller = new Dialog(context);
    promoteTeller.setContentView(R.layout.promote_teller);
    promoteTeller.getWindow().setBackgroundDrawable(null);
    RelativeLayout layout = (RelativeLayout) promoteTeller.findViewById(R.id.layout);
    final EditText tellerToPromote = (EditText) layout.findViewById(R.id.inputTellerId);
    final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
    final Button promote = (Button) layout.findViewById(R.id.promote);
    promote.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String confirmationMessage = "";
        try {
          int tellerId = Integer.parseInt(tellerToPromote.getText().toString());
          DatabaseSelectHelper selector = new DatabaseSelectHelper(context);
          User user = selector.getUserDetails(Integer.valueOf(tellerId));
          if (user instanceof Teller){
            if (machine.promoteTellerToAdmin(tellerId)){
              confirmationMessage += context.getString(R.string.tellerPromoted);
            } else {
              confirmationMessage += context.getString(R.string.tellerNotPromoted);
            }
          } else {
            confirmationMessage += context.getString(R.string.notTeller);
          }
        } catch (Exception e) {
          confirmationMessage += context.getString(R.string.invalidId);
        }
        confirmMessage.setText(confirmationMessage);
      }
    });
    promoteTeller.show();
  }

  /**
   * A method that updates the interest rate of a certain type of account.
   * @param machine , the type of terminal.
   * @param context ,the connection between the main activity and the dialog.
   */
  public static void updateInterestRateDialog(final AdminTerminal machine, final Context context) {
    final Dialog updateInterest = new Dialog(context);
    updateInterest.setContentView(R.layout.account_role);
    updateInterest.getWindow().setBackgroundDrawable(null);
    RelativeLayout layout = (RelativeLayout) updateInterest.findViewById(R.id.updateAccountInterest);
    final EditText inputInterest = (EditText) layout.findViewById(R.id.updateInterest);
    final Button update = (Button) layout.findViewById(R.id.confirm);
    final TextView confirm = (TextView) layout.findViewById(R.id.confirmationMessage);

    RadioGroup accountTypes = (RadioGroup) layout.findViewById(R.id.accounts);
    final RadioButton tfsa = (RadioButton) accountTypes.findViewById(R.id.tfsa);
    final RadioButton chequing = (RadioButton) accountTypes.findViewById(R.id.chequing);
    final RadioButton savings = (RadioButton) accountTypes.findViewById(R.id.savings);
    final RadioButton restrictedSavings = (RadioButton) accountTypes.findViewById(R.id.restrictedSavings);
    final RadioButton balanceOwing = (RadioButton) accountTypes.findViewById(R.id.balanceOwing);

    update.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String accountType = "";
        if (tfsa.isChecked()) {
          accountType = "TFSA";
        } else if (chequing.isChecked()) {
          accountType = "CHEQUING";
        } else if (savings.isChecked()) {
          accountType = "SAVING";
        } else if (restrictedSavings.isChecked()) {
          accountType = "RESTRICTEDSAVING";
        } else if (balanceOwing.isChecked()) {
          accountType = "BALANCEOWING";
        }
        String confirmMessage = "";
        java.math.BigDecimal interest = java.math.BigDecimal.ZERO;
        boolean validInput = true;
        try {
          interest = java.math.BigDecimal.valueOf(Double.parseDouble(inputInterest.getText().toString()));
          interest = interest.setScale(2, RoundingMode.HALF_UP);
          if (interest.doubleValue() < 0.00 || interest.doubleValue() >= 1.00) {
            confirmMessage = context.getString(R.string.invalidAmount);
            validInput = false;
          }
        } catch (NumberFormatException e) {
          confirmMessage += context.getString(R.string.invalidAmount);
          validInput = false;
        }
        if (validInput) {
          AccountTypesEnumMap accountEnumMap = new AccountTypesEnumMap(context);
          if (machine.updateInterestRate(interest, accountEnumMap.getAccountId(accountType))) {
            confirmMessage += context.getString(R.string.interestUpdated);
            update.setText(context.getString(R.string.back));
            update.setOnClickListener(new View.OnClickListener() {
              public void onClick(View v) {
                updateInterest.dismiss();
              }
            });
          } else {
            confirmMessage += context.getString(R.string.interestNotUpdated);
          }
        }
        confirm.setText(confirmMessage);
      }
    });
    updateInterest.show();
  }
}
