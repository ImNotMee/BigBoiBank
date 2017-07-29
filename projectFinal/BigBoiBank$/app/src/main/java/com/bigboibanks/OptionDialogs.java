package com.bigboibanks;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

}
