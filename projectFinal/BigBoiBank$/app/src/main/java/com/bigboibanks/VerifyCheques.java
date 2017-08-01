package com.bigboibanks;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.IllegalAmountException;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class VerifyCheques extends AppCompatActivity {

  /**
   * A method that verifies cheques.
   * @param savedInstanceState, the Bundle used in every Android activity.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_verify_cheuqes);
    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
    final Context context = this;
    // loop through and create a view for each cheque
    int i = 1;
    final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    for (File f : UserInterface.storageDir.listFiles(UserInterface.IMAGE_FILTER)) {
      final int iCopy = i;
      RelativeLayout details = (RelativeLayout) inflater.inflate(R.layout.cheque_selection, null);
      TextView number = (TextView) details.findViewById(R.id.chequeNumber);
      TextView verified = (TextView) details.findViewById(R.id.verified);
      final String currNumber = getString(R.string.cheque) + " " + String.valueOf(i);
      boolean currVerified = LogIn.savedInfo.getBoolean("ChequeVerified" + String.valueOf(i), true);
      final int chequeAccount = LogIn.savedInfo.getInt("ChequeAccount" + String.valueOf(i), -1);
      if (currVerified) {
        verified.setText(getString(R.string.chequeStatusChecked));
      } else {
        verified.setText(getString(R.string.chequeStatusUnchecked));
      }
      number.setText(currNumber);
      String filePath = LogIn.savedInfo.getString("ChequeFilePath" + String.valueOf(i), "");
      final Bitmap chequeImage = BitmapFactory.decodeFile(filePath);
      details.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          boolean stillVerified = LogIn.savedInfo.getBoolean("ChequeVerified" + String.valueOf(iCopy), true);
          final Dialog verifyCheque = new Dialog(context);
          verifyCheque.setContentView(R.layout.verify_cheque);
          ScrollView scrollView = (ScrollView) verifyCheque.findViewById(R.id.scrollView);
          RelativeLayout layout = (RelativeLayout) scrollView.findViewById(R.id.relativeLayout);
          final ImageView cheque = (ImageView) layout.findViewById(R.id.image);
          cheque.setImageBitmap(chequeImage);
          final EditText inputAmount = (EditText) layout.findViewById(R.id.balance);
          final TextView confirm = (TextView) layout.findViewById(R.id.confirmationMessage);
          final Button verify = (Button) layout.findViewById(R.id.verify);
          final Button decline = (Button) layout.findViewById(R.id.decline);
          if (stillVerified) {
            confirm.setText(context.getString(R.string.chequeChecked));
            inputAmount.setVisibility(View.GONE);
            verify.setVisibility(View.GONE);
            decline.setVisibility(View.GONE);
          } else {
            verify.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                BigDecimal amount;
                String confirmationMessage = "";
                try {
                  amount = new java.math.BigDecimal(inputAmount.getText().toString());
                  amount = amount.setScale(2, RoundingMode.HALF_UP);
                  try {
                    if (UserInterface.machine.makeDeposit(amount, chequeAccount)) {
                      confirmationMessage += context.getString(R.string.chequeVerified);
                      DatabaseSelectHelper selector = new DatabaseSelectHelper(context);
                      confirmationMessage += selector.getBalance(chequeAccount).toString();
                      verify.setClickable(false);
                      decline.setClickable(false);
                      // set that it has now been checked
                      LogIn.savedInfoWriter.putBoolean("ChequeVerified" + String.valueOf(iCopy), true);
                      LogIn.savedInfoWriter.apply();
                    } else {
                      confirmationMessage += context.getString(R.string.transactionFailed);
                    }
                  } catch (IllegalAmountException e) {
                    confirmationMessage += context.getString(R.string.invalidAmount);
                  }
                } catch (NumberFormatException e) {
                  confirmationMessage += context.getString(R.string.invalidAmount);
                }
                confirm.setText(confirmationMessage);
              }
            });

            decline.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                // set that it has now been checked
                LogIn.savedInfoWriter.putBoolean("ChequeVerified" + String.valueOf(iCopy), true);
                LogIn.savedInfoWriter.apply();
                verify.setClickable(false);
                decline.setClickable(false);
              }
            });
          }
          verifyCheque.show();
        }
      });
      linearLayout.addView(details);
      i++;
    }
  }
}
