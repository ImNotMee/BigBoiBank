package com.bigboibanks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bank.database.DatabaseDriverA;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import com.bank.machines.AdminTerminal;
import com.bank.users.Admin;
import com.bank.users.Customer;
import com.bank.users.Teller;
import com.bank.users.User;

import java.math.BigDecimal;

public class LogIn extends AppCompatActivity {

  protected static SharedPreferences savedInfo;
  protected static SharedPreferences.Editor savedInfoWriter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_log_in);

    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    final DatabaseSelectHelper selector = new DatabaseSelectHelper(this);
    final Context context = this;

    savedInfo = this.getPreferences(Context.MODE_PRIVATE);
    savedInfoWriter = savedInfo.edit();

    DatabaseInsertHelper inserter = new DatabaseInsertHelper(this);

    // initialize roles and accounts if first time running the app
    if (savedInfo.getBoolean("firstTime", true)) {
      // insert account types with interest rate of 0.2
      String accountTypeStr;
      String interestRate = "0.2";
      for (AccountTypes accountTypes : AccountTypes.values()) {
        accountTypeStr = accountTypes.toString();
        inserter.insertAccountType(accountTypeStr, new BigDecimal(interestRate));
      }

      // insert roles
      String roleStr;
      for (Roles role : Roles.values()) {
        roleStr = role.toString();
        inserter.insertRole(roleStr);
      }
      inserter.insertNewUser("admin", 12, "", 1, "password");
      savedInfoWriter.putBoolean("firstTime", false);
      savedInfoWriter.apply();

    }

    final EditText idText = (EditText) findViewById(R.id.inputId);
    final EditText password = (EditText) findViewById(R.id.inputPassword);

    RadioGroup userTypes = (RadioGroup) findViewById(R.id.userChoice);
    final RadioButton admin = (RadioButton) userTypes.findViewById(R.id.admin);
    final RadioButton teller = (RadioButton) userTypes.findViewById(R.id.teller);
    final RadioButton customer = (RadioButton) userTypes.findViewById(R.id.customer);

    Button logIn = (Button) findViewById(R.id.logIn);
    logIn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int id = -1;
        try {
          id = Integer.parseInt(idText.getText().toString());
        } catch (NumberFormatException e) {

        }
        User user = selector.getUserDetails(id);
        if (user != null) {
          boolean authenticated = user.authenticate(password.getText().toString());
          if (authenticated) {
            if (user instanceof Admin && admin.isChecked()) {
              Intent intent = new Intent(context, UserInterface.class);
              intent.putExtra("id", user.getId());
              intent.putExtra("password", password.getText().toString());
              intent.putExtra("machine", "admin" );
              startActivity(intent);
            } else if (user instanceof Teller && teller.isChecked()) {
              Intent intent = new Intent(context, UserInterface.class);
              intent.putExtra("id", user.getId());
              intent.putExtra("password", password.getText().toString());
              intent.putExtra("machine", "teller");
              startActivity(intent);
            } else if (user instanceof Customer && customer.isChecked()) {
              Intent intent = new Intent(context, UserInterface.class);
              intent.putExtra("id", user.getId());
              intent.putExtra("password", password.getText().toString());
              intent.putExtra("machine", "customer");
              startActivity(intent);
            } else {
              // this means they logged in as a wrong user
              Intent intent = new Intent(context, FeelsBadMan.class);
              startActivity(intent);
            }
          }
        }
      }
    });

  }

}
