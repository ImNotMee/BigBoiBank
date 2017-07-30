package com.bigboibanks;

import android.app.Dialog;
import android.content.Context;
import android.icu.math.BigDecimal;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.IllegalAmountException;
import com.bank.generics.AccountTypesEnumMap;
import com.bank.machines.AdminTerminal;
import com.bank.machines.BankServiceSystems;
import com.bank.machines.BankWorkerServiceSystems;
import com.bank.users.Customer;
import com.bank.users.User;

import java.math.BigInteger;

public abstract class OptionDialogs {

  public static void makeUserDialog(final BankWorkerServiceSystems machine, final String user, final Context context) {
    final Dialog makeUser = new Dialog(context);
    makeUser.setContentView(R.layout.make_user);
    RelativeLayout layout = (RelativeLayout) makeUser.findViewById(R.id.makeUser);
    TextView title = (TextView) layout.findViewById(R.id.title);
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


  public static void makeAccountDialog(final BankWorkerServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_SHORT).show();
    } else {

      final Dialog makeAccount = new Dialog(context);
      makeAccount.setContentView(R.layout.make_account);
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
          } else if(savings.isChecked()) {
            accountType = "SAVING";
          } else if(restrictedSavings.isChecked()) {
            accountType = "RESTRICTEDSAVING";
          } else if(balanceOwing.isChecked()) {
            accountType = "BALANCEOWING";
          }
          boolean validInput = true;
          final Context finalContext = context;
          // Find the account type ID
          AccountTypesEnumMap AccountEnum = new AccountTypesEnumMap(finalContext);
          String confirmationMessage = "";
          String name = inputName.getText().toString();
          java.math.BigDecimal balance = java.math.BigDecimal.ZERO;
          try {
            balance = java.math.BigDecimal.valueOf(Double.parseDouble(inputBalance.getText().toString()));
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
            if (machine.makeNewAccount(name, balance , AccountTypeID)) {
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

  public static void moneyTransactionDialog(final BankServiceSystems machine, final String transaction, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_SHORT).show();
    } else {
      final Dialog makeTransaction = new Dialog(context);
      makeTransaction.setContentView(R.layout.money_transaction);
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
            if (!machine.getCurrentCustomer().getAccounts().contains(id)) {
              confirmationMessage += context.getString(R.string.invalidId);
              validInput = false;
            }
          } catch (NumberFormatException e) {
            confirmationMessage += context.getString(R.string.invalidId);
            validInput = false;
          }
          java.math.BigDecimal amount = new java.math.BigDecimal(BigInteger.ZERO);
          try {
            amount = new java.math.BigDecimal(inputAmount.getText().toString());
          } catch (NumberFormatException e) {
            confirmationMessage += context.getString(R.string.invalidAmount);
            validInput = false;
          }
          if (validInput) {
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
          }
          confirmMessage.setText(confirmationMessage);
        }
      });
      makeTransaction.show();
    }
  }

  public static void setCustomerDialog(final BankWorkerServiceSystems machine, final Context context) {
    final Dialog setCustomer = new Dialog(context);
    setCustomer.setContentView(R.layout.set_customer);
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
            confirmationMessage += context.getString(R.string.invalidId);
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
          } else {
            confirmationMessage += context.getString(R.string.incorrectPassword);
          }
          confirm.setText(context.getString(R.string.back));
          confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              setCustomer.dismiss();
            }
          });

        }
        confirmMessage.setText(confirmationMessage);
      }
    });
    setCustomer.show();
  }

}
