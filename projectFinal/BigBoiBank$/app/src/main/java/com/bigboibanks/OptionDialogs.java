package com.bigboibanks;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.NumberFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bank.accounts.Account;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.generics.AccountTypesEnumMap;
import com.bank.generics.RolesEnumMap;
import com.bank.machines.AdminTerminal;
import com.bank.machines.BankServiceSystems;
import com.bank.machines.BankWorkerServiceSystems;
import com.bank.machines.TellerTerminal;
import com.bank.users.Customer;
import com.bank.users.User;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
      if (transaction.equals("deposit")) {
        final Dialog depositChequeChoice = new Dialog(context);
        depositChequeChoice.setContentView(R.layout.cheque_option);
        (depositChequeChoice.findViewById(R.id.layout).findViewById(R.id.yes)).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            final int REQUEST_IMAGE_CAPTURE = 1;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
              ((Activity) context).startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
          }
        });
        (depositChequeChoice.findViewById(R.id.layout).findViewById(R.id.no)).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            inputMoneyTransaction(machine, transaction, context);
          }
        });
        depositChequeChoice.show();
      } else {
        inputMoneyTransaction(machine, transaction, context);
      }

    }
  }


  private static void inputMoneyTransaction(final BankServiceSystems machine, final String transaction, final Context context) {

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
          if (!new DatabaseSelectHelper(context).getAccountIds(machine.getCurrentCustomer().getId()).contains(id)) {
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
          if (transaction.equals("deposit")) {
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
          } else {
            try {
              machine.makeWithdrawal(amount, id);
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
            } catch (InsufficientFundsException e) {
              confirmationMessage += context.getString(R.string.insufficientFunds);
            }
          }

        }
        confirmMessage.setText(confirmationMessage);
      }
    });
    makeTransaction.show();
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

  public static void listAccountsDialog(final BankServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_SHORT).show();
    } else {

      List<Account> accounts = machine.listCustomerAccounts();
      if (accounts.size() == 0) {
        Toast.makeText(context, context.getString(R.string.noAccounts), Toast.LENGTH_SHORT).show();
      } else {
        final Dialog makeTransaction = new Dialog(context);
        makeTransaction.setContentView(R.layout.list_accounts);
        RelativeLayout layout = (RelativeLayout) makeTransaction.findViewById(R.id.layout);
        final ScrollView scrollView = (ScrollView) layout.findViewById(R.id.scrollView);
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (Account currAccount : accounts) {
          RelativeLayout details = (RelativeLayout) inflater.inflate(R.layout.account, null);
          String id = context.getString(R.string.accountId) + String.valueOf(currAccount.getId());
          String name = context.getString(R.string.accountName) + currAccount.getName();
          String balance = context.getString(R.string.accountBalance) + currAccount.getBalance().toString();
          String type = context.getString(R.string.accountType) + new DatabaseSelectHelper(context).getAccountTypeName(currAccount.getType());
          ((TextView) details.findViewById(R.id.id)).setText(id);
          ((TextView) details.findViewById(R.id.name)).setText(name);
          ((TextView) details.findViewById(R.id.balance)).setText(balance);
          ((TextView) details.findViewById(R.id.type)).setText(type);
          ((LinearLayout) scrollView.findViewById(R.id.linearLayout)).addView(details);

        }
        makeTransaction.show();
      }

    }
  }

  public static void checkBalanceDialog(final BankServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_SHORT).show();
    } else {
      final Dialog checkBalance = new Dialog(context);
      checkBalance.setContentView(R.layout.one_input);
      RelativeLayout layout = (RelativeLayout) checkBalance.findViewById(R.id.layout);
      final EditText inputAccountId = (EditText) layout.findViewById(R.id.input);
      final TextView balance = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button check = (Button) layout.findViewById(R.id.confirm);
      check.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          boolean validInput = true;
          String confirmationMessage = "";
          int id = -1;
          try {
            id = Integer.parseInt(inputAccountId.getText().toString());
            if (!new DatabaseSelectHelper(context).getAccountIds(machine.getCurrentCustomer().getId()).contains(id)) {
              confirmationMessage += context.getString(R.string.invalidId);
              validInput = false;
            }
          } catch (NumberFormatException e) {
            confirmationMessage += context.getString(R.string.invalidId);
            validInput = false;
          }
          if (validInput) {
            confirmationMessage += context.getString(R.string.balance);
            confirmationMessage += machine.checkBalance(id).toString();
          }
          balance.setText(confirmationMessage);
        }
      });
      checkBalance.show();
    }
  }

  public static void giveInterestDialog(final BankWorkerServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_SHORT).show();
    } else {
      final Dialog checkBalance = new Dialog(context);
      checkBalance.setContentView(R.layout.one_input);
      RelativeLayout layout = (RelativeLayout) checkBalance.findViewById(R.id.layout);
      final EditText inputAccountId = (EditText) layout.findViewById(R.id.input);
      final TextView balance = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button check = (Button) layout.findViewById(R.id.confirm);
      check.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          boolean validInput = true;
          String confirmationMessage = "";
          int id = -1;
          try {
            id = Integer.parseInt(inputAccountId.getText().toString());
            if (!new DatabaseSelectHelper(context).getAccountIds(machine.getCurrentCustomer().getId()).contains(id)) {
              confirmationMessage += context.getString(R.string.invalidId);
              validInput = false;
            }
          } catch (NumberFormatException e) {
            confirmationMessage += context.getString(R.string.invalidId);
            validInput = false;
          }
          if (validInput) {
            if (machine.giveInterest(id)) {
              confirmationMessage += context.getString(R.string.interestAdded);
              confirmationMessage += machine.checkBalance(id).toString();
            } else {
              confirmationMessage += context.getString(R.string.interestNotAdded);
            }
          }
          balance.setText(confirmationMessage);
        }
      });
      checkBalance.show();
    }
  }

  public static void updateNameDialog(final BankWorkerServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_SHORT).show();
    } else {
      final Dialog updateName = new Dialog(context);
      updateName.setContentView(R.layout.one_input);
      RelativeLayout layout = (RelativeLayout) updateName.findViewById(R.id.layout);
      ((TextView) layout.findViewById(R.id.title)).setText(context.getString(R.string.updateName));
      final EditText inputName = (EditText) layout.findViewById(R.id.input);
      inputName.setInputType(InputType.TYPE_NULL);
      inputName.setHint(context.getString(R.string.promptName));
      final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button update = (Button) layout.findViewById(R.id.confirm);
      update.setText(R.string.update);
      update.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String confirmationMessage = "";
          String name = inputName.getText().toString();
          if (name.length() == 0) {
            confirmationMessage += context.getString(R.string.invalidName);
          } else {
            machine.updateUserName(name);
            confirmationMessage += context.getString(R.string.nameUpdated);
          }
          confirmMessage.setText(confirmationMessage);
        }
      });
      updateName.show();
    }
  }

  public static void updateAgeDialog(final BankWorkerServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_SHORT).show();
    } else {
      final Dialog updateAge = new Dialog(context);
      updateAge.setContentView(R.layout.one_input);
      RelativeLayout layout = (RelativeLayout) updateAge.findViewById(R.id.layout);
      ((TextView) layout.findViewById(R.id.title)).setText(context.getString(R.string.updateAge));
      final EditText inputAge = (EditText) layout.findViewById(R.id.input);
      inputAge.setHint(context.getString(R.string.promptAge));
      final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button update = (Button) layout.findViewById(R.id.confirm);
      update.setText(R.string.update);
      update.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String confirmationMessage = "";
          try {
            int age = Integer.parseInt(inputAge.getText().toString());
            if (age != 0) {
              machine.updateUserAge(age);
              confirmationMessage += context.getString(R.string.ageUpdated);
            } else {
              confirmationMessage += context.getString(R.string.invalidAge);
            }
          } catch (NumberFormatException e) {
            confirmationMessage += context.getString(R.string.invalidId);
          }
          confirmMessage.setText(confirmationMessage);
        }
      });
      updateAge.show();
    }
  }

  public static void updateAddressDialog(final BankWorkerServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_SHORT).show();
    } else {
      final Dialog updateAddress = new Dialog(context);
      updateAddress.setContentView(R.layout.one_input);
      RelativeLayout layout = (RelativeLayout) updateAddress.findViewById(R.id.layout);
      ((TextView) layout.findViewById(R.id.title)).setText(context.getString(R.string.updateAddress));
      final EditText inputAddress = (EditText) layout.findViewById(R.id.input);
      inputAddress.setInputType(InputType.TYPE_NULL);
      inputAddress.setHint(context.getString(R.string.promptAddress));
      final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button update = (Button) layout.findViewById(R.id.confirm);
      update.setText(R.string.update);
      update.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String confirmationMessage = "";
          String address = inputAddress.getText().toString();
          if (address.length() == 0 || address.length() > 100) {
            confirmationMessage += context.getString(R.string.invalidAddress);
          } else {
            machine.updateUserAddress(address);
            confirmationMessage += context.getString(R.string.addressUpdated);
          }
          confirmMessage.setText(confirmationMessage);
        }
      });
      updateAddress.show();
    }
  }

  public static void updatePasswordDialog(final BankWorkerServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_SHORT).show();
    } else {
      final Dialog updatePassword = new Dialog(context);
      updatePassword.setContentView(R.layout.update_password);
      RelativeLayout layout = (RelativeLayout) updatePassword.findViewById(R.id.layout);
      final EditText inputPassword = (EditText) layout.findViewById(R.id.password);
      final EditText confirmPassword = (EditText) layout.findViewById(R.id.confirmPassword);
      final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button update = (Button) layout.findViewById(R.id.confirm);
      update.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String confirmationMessage = "";
          if (inputPassword.getText().toString().equals(confirmPassword.getText().toString())) {
            machine.updateUserPassword(inputPassword.getText().toString());
            confirmationMessage += context.getString(R.string.passwordUpdated);
          } else {
            confirmationMessage += context.getString(R.string.passwordNoMatch);
          }
          confirmMessage.setText(confirmationMessage);
        }
      });
      updatePassword.show();
    }
  }

  public static void transferFunds(final BankServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_SHORT).show();
    } else {
      final Dialog transferFunds = new Dialog(context);
      transferFunds.setContentView(R.layout.transfer_funds);
      RelativeLayout layout = (RelativeLayout) transferFunds.findViewById(R.id.layout);
      final EditText transferFrom = (EditText) layout.findViewById(R.id.transferFrom);
      final EditText transferTo = (EditText) layout.findViewById(R.id.transferTo);
      final EditText inputAmount = (EditText) layout.findViewById(R.id.amount);
      final TextView confirmMessage = (TextView) layout.findViewById(R.id.confirmationMessage);
      final Button transfer = (Button) layout.findViewById(R.id.transfer);
      transfer.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          String confirmationMessage = "";
          try {
            int fromId = Integer.parseInt(transferFrom.getText().toString());
            int toId = Integer.parseInt(transferTo.getText().toString());
            DatabaseSelectHelper selector = new DatabaseSelectHelper(context);
            if (selector.getAccountIds(machine.getCurrentCustomer().getId()).contains(fromId)
                    && selector.getAccountType(toId) != -1) {
              try {
                BigDecimal amount = new BigDecimal(inputAmount.getText().toString());
                machine.makeWithdrawal(amount, fromId);
                if (machine.makeDeposit(amount, toId)) {
                  confirmationMessage += context.getString(R.string.transferCompleted);
                } else {
                  confirmationMessage += context.getString(R.string.transferFailed);
                }
              } catch (NumberFormatException | IllegalAmountException e) {
                confirmationMessage += context.getString(R.string.invalidAmount);
              } catch (InsufficientFundsException e) {
                confirmationMessage += context.getString(R.string.insufficientFunds);
              }
            } else {
              confirmationMessage += context.getString(R.string.invalidId);
            }
          } catch (NumberFormatException e) {
            confirmationMessage += context.getString(R.string.invalidId);
          }

          confirmMessage.setText(confirmationMessage);
        }
      });
      transferFunds.show();
    }
  }

  public static void viewUserBalanceDialog(final BankWorkerServiceSystems machine, final Context context) {
    final Dialog customerBalance = new Dialog(context);
    customerBalance.setContentView(R.layout.one_input);
    RelativeLayout layout = (RelativeLayout) customerBalance.findViewById(R.id.layout);
    ((TextView) layout.findViewById(R.id.title)).setText(context.getString(R.string.customerBalance));
    final EditText inputID = (EditText) layout.findViewById(R.id.input);
    inputID.setHint(context.getString(R.string.promptUserId));
    final TextView balance = (TextView) layout.findViewById(R.id.confirmationMessage);
    final Button check = (Button) layout.findViewById(R.id.confirm);
    check.setText(R.string.check);
    check.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String confirmationMessage = "";
        try {
          int id = Integer.parseInt(inputID.getText().toString());
          User user = new DatabaseSelectHelper(context).getUserDetails(id);
          if (user instanceof Customer) {
            confirmationMessage += context.getString(R.string.totalBalance);
            confirmationMessage += machine.getTotalBalance(id).toString();
          } else {
            confirmationMessage += context.getString(R.string.invalidId);
          }
        } catch (NumberFormatException e) {
          confirmationMessage += context.getString(R.string.invalidId);
        }
        balance.setText(confirmationMessage);
      }
    });
    customerBalance.show();
  }

  public static void leaveMessage(final BankServiceSystems machine, final Context context) {
    final Dialog leaveMessage = new Dialog(context);
    leaveMessage.setContentView(R.layout.leave_message);
    RelativeLayout layout = (RelativeLayout) leaveMessage.findViewById(R.id.layout);
    // references to the editText and TextEdits
    final TextView notification = (TextView) layout.findViewById(R.id.notification);
    final EditText inputId = (EditText) layout.findViewById(R.id.inputId);
    final EditText inputMessage = (EditText) layout.findViewById(R.id.inputMessage);
    final Button confirm = (Button) layout.findViewById(R.id.confirmButton);
    // if the customer clicks confirm
    confirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String confirmationMessage = "";
        try {
          int userId = Integer.parseInt(inputId.getText().toString());
          String message = inputMessage.getText().toString();
          DatabaseSelectHelper selector = new DatabaseSelectHelper(context);
          if (selector.getUserDetails(userId) != null) {
            int id = ((BankWorkerServiceSystems) machine).leaveMessage(message, userId);
            confirmationMessage += "Successfully left a message with id : " + id;
          }
        } catch (Exception e) {
          confirmationMessage += "Invalid input";
        }
        notification.setText(confirmationMessage);
      }
    });
    leaveMessage.show();
  }

  public static void showMessageIds(final BankServiceSystems machine, final Context context) {
    if (machine.getCurrentCustomer() == null) {
      Toast.makeText(context, context.getString(R.string.setCustomerFirst), Toast.LENGTH_SHORT).show();
    } else {
      final Dialog dialog = new Dialog(context);
      dialog.setContentView(R.layout.list_message_ids);
      final RelativeLayout parent = (RelativeLayout) dialog.findViewById(R.id.parent);
      final ScrollView scrollView = (ScrollView) parent.findViewById(R.id.scrollView);
      final LinearLayout layout = (LinearLayout) scrollView.findViewById(R.id.layout);
      // show the user all of the message Ids that are for them
      try {
        ArrayList<Integer> ids = (ArrayList<Integer>) machine.getCustomerMessageIds();
        // now add each id to the scroll view using a textview
        for (Integer id : ids) {
          TextView textView = new TextView(context);
          textView.setText("id: " + Integer.toString((int) id));
          layout.addView(textView);
        }
      } catch (Exception e) {
        e.getMessage();
      }
      dialog.show();
    }
  }

  public static void seeSpecificMessage(final BankServiceSystems machine, final Context context) {
    final Dialog dialog = new Dialog(context);
    dialog.setContentView(R.layout.one_input);
    // change the text in the layout
    ((TextView) dialog.findViewById(R.id.title)).setText("See specific message");
    ((EditText) dialog.findViewById(R.id.input)).setHint("Input message id");
    final EditText input = (EditText) dialog.findViewById(R.id.input);
    final TextView confirmationMessage = (TextView) dialog.findViewById(R.id.confirmationMessage);
    final Button button = (Button) dialog.findViewById(R.id.confirm);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          int id = Integer.parseInt(input.getText().toString());
          String message = machine.getMessage(id);
          confirmationMessage.setText(message);
        } catch (Exception e) {
          confirmationMessage.setText(R.string.invalidId);
        }
      }
    });
    dialog.show();
  }

  public static void listCurrentUserDialog(final AdminTerminal machine, final Context context, String role) {
    List<User> users = machine.listUsers(role);

    if (users == null) {
      Toast.makeText(context, context.getString(R.string.noCurrentUser) + role, Toast.LENGTH_SHORT).show();
    } else {
      final Dialog makeTransaction = new Dialog(context);
      makeTransaction.setContentView(R.layout.list_users);
      RelativeLayout layout = (RelativeLayout) makeTransaction.findViewById(R.id.layout);
      ((TextView) layout.findViewById(R.id.title)).setText("List of " + role);
      final ScrollView scrollView = (ScrollView) layout.findViewById(R.id.scrollView);
      final LayoutInflater inflater = (LayoutInflater) context
              .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      for (User user : users) {
        RelativeLayout details = (RelativeLayout) inflater.inflate(R.layout.account, null);
        String id = context.getString(R.string.userID) + String.valueOf(user.getId());
        String name = context.getString(R.string.userName) + user.getName();
        String address = context.getString(R.string.userAddress) + user.getAddress();
        String type = context.getString(R.string.userAge) + String.valueOf(user.getAge());
        ((TextView) details.findViewById(R.id.id)).setText(id);
        ((TextView) details.findViewById(R.id.name)).setText(name);
        ((TextView) details.findViewById(R.id.balance)).setText(address);
        ((TextView) details.findViewById(R.id.type)).setText(type);
        ((LinearLayout) scrollView.findViewById(R.id.linearLayout)).addView(details);

      }
      makeTransaction.show();
    }

  }

}
