package com.bigboibanks;

import android.app.Dialog;
import android.content.Context;
import android.icu.math.BigDecimal;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bank.generics.AccountTypesEnumMap;

import com.bank.exceptions.IllegalAmountException;
import com.bank.machines.AdminTerminal;
import com.bank.machines.BankWorkerServiceSystems;

public abstract class OptionDialogs {

  public static void makeUserDialog(final BankWorkerServiceSystems machine, final String user, Context context) {
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

    final Button confirm = (Button) makeUser.findViewById(R.id.makeUser).findViewById(R.id.confirm);
    confirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        boolean validInput = true;
        String confirmationMessage = "";
        String name = inputName.getText().toString();
        int age = 0;
        try {
          age = Integer.parseInt(inputAge.getText().toString());
        } catch (NumberFormatException e) {
          confirmationMessage += "Invalid Age. ";
          validInput = false;
        }
        String address = inputAddress.getText().toString();
        if (address.length() >= 100) {
          confirmationMessage += "Address is too long. ";
          validInput = false;
        }
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();
        if (!password.equals(confirmPassword)) {
          confirmationMessage += "Passwords do not match. ";
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
            confirmationMessage += "User successfully added with ID: ";
            confirmationMessage += String.valueOf(id);
            confirm.setText("Exit");
            confirm.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                makeUser.dismiss();
              }
            });
          } else {
            confirmationMessage += "There was an error creating the new User. ";
          }
        }
        confirmMessage.setText(confirmationMessage);
      }
    });
    makeUser.show();
  }

  public static void makeAccountDialog(final BankWorkerServiceSystems machine, final String accountType, final Context context) {
    final Dialog makeAccount = new Dialog(context);
    makeAccount.setContentView(R.layout.make_account);
    RelativeLayout layout = (RelativeLayout) makeAccount.findViewById(R.id.makeAccount);

    TextView title = (TextView) layout.findViewById(R.id.title);
    if (accountType.equals("saving")) {
      title.setText("Make a New Savings Account");
    } else if (accountType.equals("chequing")) {
      title.setText("Make a New Chequing Account");
    } else if (accountType.equals("tfsa")) {
      title.setText("Make a New TFSA Account");
    } else if (accountType.equals("restrictedSaving")) {
      title.setText("Make a New Restricted Savings Account");
    } else if (accountType.equals("balanceOwing")) {
      title.setText("Make a New Balancing Owing Account");
    }

    final EditText inputName = (EditText) layout.findViewById(R.id.accountName);
    final EditText inputBalance = (EditText) layout.findViewById(R.id.balance);
    final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);

    final Button confirm = (Button) makeAccount.findViewById(R.id.makeAccount).findViewById(R.id.confirm);
    confirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        boolean validInput = true;
        final Context finalContext = context;
        // Find the account type ID
        AccountTypesEnumMap AccountEnum = new AccountTypesEnumMap(finalContext);
        int AccountTypeID = AccountEnum.getAccountId(accountType);
        String confirmationMessage = "";
        String name = inputName.getText().toString();
        java.math.BigDecimal balance = java.math.BigDecimal.ZERO;
        try {
          balance = java.math.BigDecimal.valueOf(Double.parseDouble(inputBalance.getText().toString()));
          if (balance.doubleValue() < 0.00 && !accountType.equals("balanceOwing")) {
            confirmationMessage = "Invalid Balance. ";
            validInput = false;
          }
        } catch (NumberFormatException e) {
          confirmationMessage += "Invalid Balance. ";
          validInput = false;
        }
        if (validInput) {
          boolean success;
          if (machine instanceof AdminTerminal) {
            success = ((AdminTerminal) machine).makeNewAccount(name, balance, AccountTypeID);
          } else {
            success = machine.makeNewAccount(name, balance , AccountTypeID);
          }
          if (success) {
            confirmationMessage += "Account successfully added ";
            confirm.setText("Exit");
            confirm.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                makeAccount.dismiss();
              }
            });
          } else {
            confirmationMessage += "There was an error creating the new Account. ";
          }
        }
        confirmMessage.setText(confirmationMessage);
      }
    });
    makeAccount.show();
  }
}
