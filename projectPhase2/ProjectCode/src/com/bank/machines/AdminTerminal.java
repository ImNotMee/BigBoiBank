package com.bank.machines;

import java.util.List;

import com.bank.exceptions.ConnectionFailedException;
import com.bank.users.*;
import com.bank.generics.RolesEnumMap;

public class AdminTerminal extends BankServiceSystems{
  private Admin currentAdmin; 
  private boolean currentAdminAuthenicated;
  
  public AdminTerminal(Admin admin, boolean authenicated) {
    currentAdmin = admin;
    currentAdminAuthenicated = authenicated;
  }
  
  
  public int makeNewAdmin(String name, int age, String address, String password) throws ConnectionFailedException {
    int adminId = -1;
    if (currentAdminAuthenicated) {
      Admin newAdmin = (Admin) UserCreator.makeUser(age, password, age, password);
      adminId = newAdmin.getId();
    }
    return adminId;
  }
  
  public List<Admin> listAdmins() {
    RolesEnumMap enumMap = new RolesEnumMap();
    // Get the role Id of Admin
    int roleId = enumMap.getRoleId("ADMIN");
    // Find all the admins in the database
    
  }
  
  public List<Teller> listTellers() {
    RolesEnumMap enumMap = new RolesEnumMap();
    // Get the role Id of Teller
    int roleId = enumMap.getRoleId("TELLER");

  }
  public List<Customer> listCustomers() {
    RolesEnumMap enumMap = new RolesEnumMap();
    // Get the role Id of Customer
    int roleId = enumMap.getRoleId("CUSTOMER");

  }
  
  public boolean setCurrentCustomer(Customer customer) {
    boolean success = false;
    // Check if the Admin is authenticated and if the customer is
    if (currentAdminAuthenicated && customer.getId() == -1) {
      currentCustomer = customer;
      success = true;
    }
    return success;
  }  

  public boolean deauthenciateAdmin() {
    boolean success = false;
    if (currentAdminAuthenicated) {
      currentAdminAuthenicated = false;
      currentAdmin = null;
      success = true;
    }
    return success;
  }
}
