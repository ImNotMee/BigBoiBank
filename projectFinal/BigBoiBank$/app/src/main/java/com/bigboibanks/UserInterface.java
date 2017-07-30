package com.bigboibanks;

// credits to https://stackoverflow.com/questions/11300847/load-and-display-all-the-images-from-a-folder
//https://developer.android.com/training/camera/photobasics.html#TaskGallery
// https://androidkennel.org/android-camera-access-tutorial/

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bank.machines.AdminTerminal;
import com.bank.machines.AutomatedTellerMachine;
import com.bank.machines.BankServiceSystems;
import com.bank.machines.BankWorkerServiceSystems;
import com.bank.machines.TellerTerminal;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserInterface extends AppCompatActivity {

  final private List<List<Button>> buttons = new ArrayList<>();
  private BankServiceSystems machine;
  private Context context;
  private int currSelection = 0;
  private String machineTerminal;
  final int REQUEST_IMAGE_CAPTURE = 1;

  static File storageDir;
  static File file;

  // array of supported extensions (use a List if you prefer)
  static final String[] EXTENSIONS = new String[]{
          "jpg", "png", "bmp" // and other formats you need
  };

  // filter to identify images based on their extensions
  static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

    @Override
    public boolean accept(final File dir, final String name) {
      for (final String ext : EXTENSIONS) {
        if (name.endsWith("." + ext)) {
          return (true);
        }
      }
      return (false);
    }
  };

  ImageView image;

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

    image = (ImageView) findViewById(R.id.picture);

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
  public void listCurrentCustomers(View v){
    OptionDialogs.listCurrentUserDialog((AdminTerminal) machine , context, "CUSTOMER");
  }

  public void listCurrentAdmins(View v){
    OptionDialogs.listCurrentUserDialog((AdminTerminal) machine , context, "ADMIN");
  }

  public void listCurrentTellers(View v){
    OptionDialogs.listCurrentUserDialog((AdminTerminal) machine , context, "TELLER");
  }

  public void makeDeposit(View v) {
    final Dialog depositChequeChoice = new Dialog(context);
    depositChequeChoice.setContentView(R.layout.cheque_option);
    (depositChequeChoice.findViewById(R.id.layout).findViewById(R.id.yes)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
          // Create the File where the photo should go
          try {
            file = createImageFile();
          } catch (IOException ex) {
            // Error occurred while creating the File
          }
          // Continue only if the File was successfully created
          if (file != null) {
            Uri photoURI = FileProvider.getUriForFile(context,
                    "com.bigboibanks.android.fileprovider",
                    file);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
          }
        }
      }
    });
    (depositChequeChoice.findViewById(R.id.layout).findViewById(R.id.no)).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        OptionDialogs.moneyTransactionDialog(machine, "deposit", context);
      }
    });
    depositChequeChoice.show();
  }

  String mCurrentPhotoPath;

  private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",         /* suffix */
            storageDir      /* directory */
    );

    // Save a file: path for use with ACTION_VIEW intents
    mCurrentPhotoPath = image.getAbsolutePath();
    return image;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);




    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Toast.makeText(context, "PICTURE WAS TAKEN", Toast.LENGTH_SHORT).show();
      int i = 0;
      for (final File f : storageDir.listFiles(IMAGE_FILTER)) {
        Bitmap img = null;

        Toast.makeText(context, "PICTURE WAS TAKEN " + String.valueOf(i), Toast.LENGTH_SHORT).show();
        i++;
      }
    }
  }

  public void makeWithdrawal(View v) {
    OptionDialogs.moneyTransactionDialog(machine, "withdrawal", context);
  }

  public void setCustomer(View v) {
    OptionDialogs.setCustomerDialog((BankWorkerServiceSystems) machine, context);
  }

  public void makeAccount(View v) {
    OptionDialogs.makeAccountDialog((BankWorkerServiceSystems) machine, context);
  }

  public void listAccounts(View v) {
    OptionDialogs.listAccountsDialog(machine, context);
  }

  public void checkBalance(View v) {
    OptionDialogs.checkBalanceDialog(machine, context);
  }

  public void giveInterest(View v) {
    OptionDialogs.giveInterestDialog((BankWorkerServiceSystems) machine, context);
  }

  public void updateName(View v) {
    OptionDialogs.updateNameDialog((BankWorkerServiceSystems) machine, context);
  }

  public void updateAge(View v) {
    OptionDialogs.updateAgeDialog((BankWorkerServiceSystems) machine, context);
  }

  public void updateAddress(View v) {
    OptionDialogs.updateAddressDialog((BankWorkerServiceSystems) machine, context);
  }

  public void updatePassword(View v) {
    OptionDialogs.updatePasswordDialog((BankWorkerServiceSystems) machine, context);
  }

  public void closeCustomerSession(View v) {
    ((BankWorkerServiceSystems) machine).deAuthenticateCustomer();
    Toast.makeText(context, context.getString(R.string.closeCustomerSession), Toast.LENGTH_SHORT).show();
  }

  public void transferFunds(View v) {
    OptionDialogs.transferFunds(machine, context);
  }

  public void backUpDatabase(View v) {
    if (((AdminTerminal) machine).backUpDatabase("database_copy.ser")) {
      Toast.makeText(context, context.getString(R.string.databaseBackedUp), Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(context, context.getString(R.string.databaseBackUpFailed), Toast.LENGTH_SHORT).show();
    }
  }

  public void loadSavedDatabase(View v) {
    if (((AdminTerminal) machine).loadDatabase("database_copy.ser")) {
      Toast.makeText(context, context.getString(R.string.databaseLoaded), Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(context, context.getString(R.string.databaseNotLoaded), Toast.LENGTH_SHORT).show();
    }
  }

  public void viewCustomerBalance(View v) {
    OptionDialogs.viewUserBalanceDialog((BankWorkerServiceSystems) machine, context);
  }

  public void viewTotalBankBalance(View v) {
    Toast.makeText(context, (context.getString(R.string.totalBankBalance) + ((AdminTerminal)machine).getTotalBankBalance().toString()), Toast.LENGTH_SHORT).show();
  }

  public void leaveMessage(View v) {
    OptionDialogs.leaveMessage(machine, context);
  }

  public void showMessageIds(View v) {
    OptionDialogs.showMessageIds(machine, context);
  }

  public void seeSpecificMessage(View v) {
    OptionDialogs.seeSpecificMessage(machine, context);
  }

  public void showUserMessageIds(View v) { OptionDialogs.showUserMessageIds((BankWorkerServiceSystems) machine, context);}
}
