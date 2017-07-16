package com.bank.accounts;
import java.math.BigDecimal;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.generics.AccountTypes;


public class AccountCreator{
	/**
	 * 
	 * @param id the account id linked to the id
	 * @param name
	 * @param balance of the account
	 * @return Account object
	 * @throws ConnectionFailedException
	 */
	
	public static Account AccountCreator(int id, String name, BigDecimal balance) throws ConnectionFailedException{
		// finds the account type associated with the id
		String type = DatabaseSelectHelper.getAccountTypeName(id);
		if(type.equals(AccountTypes.CHEQUING)){
			return new ChequingAccount(id, name, balance);
		}
		else if(type.equals(AccountTypes.SAVING)){
			return new SavingsAccount(id, name, balance);
	
		}
		else if(type.equals(AccountTypes.TFSA)){
			return new TaxFreeSavingsAccount(id, name, balance);
		}
		else return null;
	}

}
