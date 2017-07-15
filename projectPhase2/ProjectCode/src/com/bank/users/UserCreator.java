package com.bank.users;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.generics.Roles;


public class UserCreator {
	public User makeUser(int id, String name, int age, String address) throws ConnectionFailedException{
		String type = DatabaseSelectHelper.getRole(id);
		if(type.equals(Roles.ADMIN)){
			return new Admin(id, name, age, address);
			
		}
		else if(type.equals(Roles.CUSTOMER)){
			return new Customer(id, name, age, address);
			
			
		}
		else if(type.equals(Roles.TELLER)){
			return new Teller(id, name, age, address);
			
		}
		else return null;
	}

}
