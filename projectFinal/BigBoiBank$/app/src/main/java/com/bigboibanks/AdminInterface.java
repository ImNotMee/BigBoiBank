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

import java.util.ArrayList;
import java.util.List;

public class AdminInterface extends AppCompatActivity {

  final private List<List<Button>> buttons = new ArrayList<>();
  private AdminTerminal machine;
  private Context context;
  private int currSelection = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_interface);
    context = this;
    machine = new AdminTerminal(getIntent().getIntExtra("id", -1), getIntent().getStringExtra("password"), this );
    RelativeLayout buttonsContainer = (RelativeLayout) findViewById(R.id.home).findViewById(R.id.scrollView).findViewById(R.id.buttons);
    List<Button> customerOptions = new ArrayList<>();
    List<Button> messageOptions = new ArrayList<>();
    List<Button> staffOptions = new ArrayList<>();
    List<Button> bankOptions = new ArrayList<>();
    List<Button> databaseOptions = new ArrayList<>();

    // add the 13 customer options
    for (int i = 0; i < 13; i++) {
      customerOptions.add((Button) buttonsContainer.getChildAt(i));
    }
    // add the 4 message options
    for (int i = 13; i < 17; i++) {
      messageOptions.add((Button) buttonsContainer.getChildAt(i));
    }

    // add the 7 staff options
    for (int i = 17; i < 24; i++) {
      staffOptions.add((Button) buttonsContainer.getChildAt(i));
    }

    // add the 3 bank options
    for (int i = 24; i < 27; i++) {
      bankOptions.add((Button) buttonsContainer.getChildAt(i));
    }

    // add the 2 database options
    for (int i = 27; i < 29; i++) {
      databaseOptions.add((Button) buttonsContainer.getChildAt(i));
    }

    buttons.add(customerOptions);
    buttons.add(messageOptions);
    buttons.add(staffOptions);
    buttons.add(bankOptions);
    buttons.add(databaseOptions);

    Spinner spinner = (Spinner) findViewById(R.id.options);
    // Create an ArrayAdapter using the string array and a default spinner layout
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.optionsArray, android.R.layout.simple_spinner_item);
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
    OptionDialogs.makeUserDialog(machine, "admin", context);
  }

  public void makeTeller(View v) {
    OptionDialogs.makeUserDialog(machine, "teller", context);
  }

  public void makeCustomer(View v) {
    OptionDialogs.makeUserDialog(machine, "customer", context);
  }


}
