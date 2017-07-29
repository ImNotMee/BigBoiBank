package com.bigboibanks;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;


public class CustomerInterface extends AppCompatActivity {

  // im lost
  private Button cb;
  private Button la;
  private Button t;
  private Button vm;
  private Button lm;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_customer_interface);

    Button checkBalance = (Button) findViewById(R.id.checkBalance);
    checkBalance.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {

      }
    });
    Button listAccounts = (Button) findViewById(R.id.listAccounts);
    listAccounts.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {

      }
    });
    Button transactions = (Button) findViewById(R.id.transactions);
    transactions.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {

      }
    });
    Button viewMessage = (Button) findViewById(R.id.viewMessage);
    viewMessage.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {

      }
    });
    Button listMessages = (Button) findViewById(R.id.listMessages);
    listMessages.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {

      }
    });

  }

  public void cbOnClick() {
    cb = (Button) findViewById(R.id.checkBalance);
    cb.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        //??? :3


      }
    });


  }


}
