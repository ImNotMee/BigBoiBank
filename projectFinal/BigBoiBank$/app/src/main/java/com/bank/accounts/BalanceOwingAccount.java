package com.bank.accounts;

import android.content.Context;

import java.math.BigDecimal;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.generics.AccountTypesEnumMap;

public class BalanceOwingAccount extends Account {
		
	public BalanceOwingAccount(int id, String name, BigDecimal balance, Context context) {
		selector = new DatabaseSelectHelper(context);
		updater = new DatabaseUpdateHelper(context);
		this.enumMap = new AccountTypesEnumMap(context);
		this.setId(id);
		this.setType(this.enumMap.getAccountId("BALANCEOWING"));
		this.setName(name);
		this.setBalance(balance);
	}
	
}
