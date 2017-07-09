package com.bank.security;

import java.security.MessageDigest;

public class PasswordHelpers {
  /**
   * Returns a hashed version of password to be stored in database.
   * @param password the unhashed password
   * @return the hashsed password
   */
  public static String passwordHash(String password) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-256");
      md.update(password.getBytes("UTF-8"));
      byte[] digest = md.digest();
      return String.format("%064x", new java.math.BigInteger(1, digest));
      
    } catch (Exception e) {
      return null;
    }
  }
  
  /**
   * check if the database password matches user provided password.
   * @param pw1 the password stored in the database.
   * @param pw2 the user provided password (unhashed).
   * @return true if passwords match, false otherwise.
   */
  public static boolean comparePassword(String pw1, String pw2) {
    return pw1.equals(passwordHash(pw2));
  }
  
}
