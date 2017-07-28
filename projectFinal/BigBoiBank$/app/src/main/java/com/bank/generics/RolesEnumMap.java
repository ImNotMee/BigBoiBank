package com.bank.generics;

import android.content.Context;

import com.bank.databasehelper.DatabaseSelectHelper;

import java.util.EnumMap;
import java.util.List;

public class RolesEnumMap {
  
  //the enumMap that this will refer to
  private static EnumMap<Roles, Integer> rolesMap = new EnumMap<>(Roles.class);
  private DatabaseSelectHelper selector;


  public RolesEnumMap(Context context) {
    selector = new DatabaseSelectHelper(context);
    this.update();
  }
  
  /**
   * Updates the EnumMap according to whatever is inside of the database.
   * @return true if it has successfully updated and false otherwise.
   */
  public void update() {
    List<Integer> roleIds = selector.getRoles();
    for (Integer roleId : roleIds) {
      // for all the roleIds, there are specific roles we want to add
      String role = selector.getRole(roleId);

      // add the value of the roleId into the enumMap for the specified role
      rolesMap.put(Roles.valueOf(role), roleId);
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