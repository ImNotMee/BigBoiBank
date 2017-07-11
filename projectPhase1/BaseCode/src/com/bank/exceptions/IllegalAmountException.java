package com.bank.exceptions;

public class IllegalAmountException extends Exception {

  private static final long serialVersionUID = 1L;

  public IllegalAmountException() {
    super();
  }
  
  public IllegalAmountException(String message) {
    super(message);
  }
}
