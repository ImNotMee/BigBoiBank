package com.bank.generics;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.generics.Roles;
import java.util.EnumMap;
import java.util.List;

public class RolesEnumMap {
  
  //the enumMap that this will refer to
  private static EnumMap<Roles, Integer> rolesMap = new EnumMap<>(Roles.class);
  
  public RolesEnumMap() {
    this.update();
  }
  
  /**
   * Updates the EnumMap according to whatever is inside of the database.
   * @return true if it has successfully updated and false otherwise.
   */
  public boolean update() {
    List<Integer> roleIds = DatabaseSelectHelper.getRoles();
    
    try {
      // update the enumMap according to the database
      for (Integer roleId : roleIds) {
        // for all the roleIds, there are specific roles we want to add
        String role = DatabaseSelectHelper.getRole((int) roleId);
        
        // add the value of the roleId into the enumMap for the specified role
        rolesMap.put(Roles.valueOf(role), (int) roleId);
       } 
      return true;
    } catch (ConnectionFailedException e) {
        return false;
      }
    }
  
  /**
   * Given a string representing the role, returns the associated roleId.
   * @param role the role in string must be all capitalized.
   * @return the roleId
   */
  public int getRoleId(String role) {
    return rolesMap.get(Roles.valueOf(role));
  }
 }