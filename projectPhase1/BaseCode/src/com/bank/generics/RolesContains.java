package com.bank.generics;

public class RolesContains {

  /**
   * Check if the Enum Roles contains the given string.
   * @param role The name of the Role to be checked.
   * @return true if the Enum contains the string, false otherwise
   */
  public static boolean contains(String role) {
    if (role != null) {
      for (Roles roleName : Roles.values()) {
        if (roleName.name().equals(role.toUpperCase())) {
          return true;
        }
      } 
    }
    return false;
  }
}
