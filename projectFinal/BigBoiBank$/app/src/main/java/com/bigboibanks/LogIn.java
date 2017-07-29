package com.bigboibanks;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.machines.AdminTerminal;

public class LogIn extends AppCompatActivity {

  private Button button;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_log_in);
    DatabaseSelectHelper selector = new DatabaseSelectHelper(this);

    EditText idText = (EditText) findViewById(R.id.inputId);
    EditText password = (EditText) findViewById(R.id.inputPassword);

    RadioGroup userTypes = (RadioGroup) findViewById(R.id.userChoice);
    RadioButton admin = (RadioButton) userTypes.findViewById(R.id.admin);
    RadioButton teller = (RadioButton) userTypes.findViewById(R.id.teller);
    RadioButton customer = (RadioButton) userTypes.findViewById(R.id.customer);

    Button logIn = (Button) findViewById(R.id.logIn);
    logIn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });

  }

  public void buttonOnClick() {
    button = (Button) findViewById(R.id.confirmButton);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        try {
            // get the values they user put in the id
            EditText editTextId = (EditText) findViewById(R.id.inputId);
            String inputId = editTextId.getText().toString();
            // get the values the user put as the password
            EditText editTextPassword = (EditText) findViewById(R.id.inputPassword);
            String inputPassword = editTextPassword.getText().toString();

        } catch (Exception e) {
            button.setText("Invalid Input you bagoongo");
        }
      }
    });
  }


}
