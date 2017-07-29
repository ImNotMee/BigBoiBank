package com.bigboibanks;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.bank.machines.AdminTerminal;
import com.bank.machines.AutomatedTellerMachine;
import com.bank.machines.BankServiceSystems;
import com.bank.machines.BankWorkerServiceSystems;
import com.bank.machines.TellerTerminal;

import java.util.ArrayList;
import java.util.List;

public class UserInterface extends AppCompatActivity {

  final private List<List<Button>> buttons = new ArrayList<>();
  private BankServiceSystems machine;
  private Context context;
  private int currSelection = 0;
  private String machineTerminal;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_interface);
    context = this;
    machineTerminal = getIntent().getStringExtra("machine");
    if (machineTerminal.equals("admin")) {
      machine = new AdminTerminal(getIntent().getIntExtra("id", -1), getIntent().getStringExtra("password"), this );
    } else if (machineTerminal.equals("teller")) {
      machine = new TellerTerminal(getIntent().getIntExtra("id", -1), getIntent().getStringExtra("password"), this );
    } else {
      machine = new AutomatedTellerMachine(getIntent().getIntExtra("id", -1), getIntent().getStringExtra("password"), this );
    }
    RelativeLayout buttonsContainer = (RelativeLayout) findViewById(R.id.home).findViewById(R.id.scrollView).findViewById(R.id.buttons);
    List<Button> customerOptions = new ArrayList<>();
    List<Button> messageOptions = new ArrayList<>();
    List<Button> staffOptions = new ArrayList<>();
    List<Button> bankOptions = new ArrayList<>();
    List<Button> databaseOptions = new ArrayList<>();

    if (machineTerminal.equals("customer")) {
      // list customer accounts button
      customerOptions.add((Button) buttonsContainer.findViewById(R.id.listAccounts));
      // make deposit
      customerOptions.add((Button) buttonsContainer.findViewById(R.id.makeDeposit));
      // make withdrawal
      customerOptions.add((Button) buttonsContainer.findViewById(R.id.makeWithdrawal));
      // check balance
      customerOptions.add((Button) buttonsContainer.findViewById(R.id.checkBalance));
      // transfer funds
      customerOptions.add((Button) buttonsContainer.findViewById(R.id.transferFunds));
    } else {
      // add the 13 customer options
      for (int i = 0; i < 13; i++) {
        customerOptions.add((Button) buttonsContainer.getChildAt(i));
      }
    }
    buttons.add(customerOptions);

    if (machineTerminal.equals("customer")) {
      // view message ids customer can see
      messageOptions.add((Button) buttonsContainer.findViewById(R.id.customerMessages));
      messageOptions.get(0).setText(getString(R.string.userMessages));
      // see specific message
      messageOptions.add((Button) buttonsContainer.findViewById(R.id.seeMessage));
    } else {
      // add the 4 message options
      for (int i = 13; i < 17; i++) {
        messageOptions.add((Button) buttonsContainer.getChildAt(i));
      }
    }
    buttons.add(messageOptions);

    if (machineTerminal.equals("admin")) {
      // add the 7 staff options
      for (int i = 17; i < 24; i++) {
        staffOptions.add((Button) buttonsContainer.getChildAt(i));
      }
      buttons.add(staffOptions);
    } else if (machineTerminal.equals("teller")) {
      // make customer
      staffOptions.add((Button) buttonsContainer.findViewById(R.id.makeCustomer));
      buttons.add(staffOptions);
    }

    if (machineTerminal.equals("admin")) {
      // add the 3 bank options
      for (int i = 24; i < 27; i++) {
        bankOptions.add((Button) buttonsContainer.getChildAt(i));
      }
      buttons.add(bankOptions);
    } else if (machineTerminal.equals("teller")) {
      // find total balance of a customer
      bankOptions.add((Button) buttonsContainer.findViewById(R.id.customerBalance));
      buttons.add(bankOptions);
    }

    if (machineTerminal.equals("admin")) {
      // add the 2 database options
      for (int i = 27; i < 29; i++) {
        databaseOptions.add((Button) buttonsContainer.getChildAt(i));
      }
      buttons.add(databaseOptions);
    }

    Spinner spinner = (Spinner) findViewById(R.id.options);
    ArrayAdapter<CharSequence> adapter;
    if (machineTerminal.equals("admin")) {
      // Create an ArrayAdapter using the string array and a default spinner layout
      adapter = ArrayAdapter.createFromResource(this,
              R.array.adminOptionsArray, android.R.layout.simple_spinner_item);
    } else if (machineTerminal.equals("teller")) {
      adapter = ArrayAdapter.createFromResource(this,
              R.array.tellerOptionsArray, android.R.layout.simple_spinner_item);
    } else {
      adapter = ArrayAdapter.createFromResource(this,
              R.array.customerOptionsArray, android.R.layout.simple_spinner_item);
    }
    // Specify the layout to use when the list of choices appears
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
    spinner.setAdapter(adapter);
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // hide current buttons that are showing
        for (Button currButton : buttons.get(currSelection)) {
          currButton.setVisibility(View.GONE);
        }
        currSelection = position;
        // show buttons for corresponding selection
        for (Button currButton : buttons.get(position)) {
          currButton.setVisibility(View.VISIBLE);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

  }

  public void makeAdmin(View v) {
    OptionDialogs.makeUserDialog((BankWorkerServiceSystems) machine, "admin", context);
  }

  public void makeTeller(View v) {
    OptionDialogs.makeUserDialog((BankWorkerServiceSystems)machine, "teller", context);
  }

  public void makeCustomer(View v) {
    OptionDialogs.makeUserDialog((BankWorkerServiceSystems)machine, "customer", context);
  }


}
