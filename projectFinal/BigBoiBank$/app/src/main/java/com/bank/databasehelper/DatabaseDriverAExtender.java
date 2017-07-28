package com.bank.databasehelper;

import android.content.Context;
import android.database.Cursor;

import com.bank.database.DatabaseDriverA;

import java.math.BigDecimal;


public class DatabaseDriverAExtender extends DatabaseDriverA {
    public DatabaseDriverAExtender(Context context) {
        super(context);
    }

    @Override
    protected long insertRole(String role) {
        return super.insertRole(role);
    }

    @Override
    protected long insertNewUser(String name, int age, String address, int roleId, String password) {
        return super.insertNewUser(name, age, address, roleId, password);
    }

    @Override
    protected long insertAccountType(String name, BigDecimal interestRate) {
        return super.insertAccountType(name, interestRate);
    }

    @Override
    protected long insertAccount(String name, BigDecimal balance, int typeId) {
        return super.insertAccount(name, balance, typeId);
    }

    @Override
    protected long insertUserAccount(int userId, int accountId) {
        return super.insertUserAccount(userId, accountId);
    }

    @Override
    protected long insertMessage(int userId, String message) {
        return super.insertMessage(userId, message);
    }

    @Override
    protected Cursor getRoles() {
        return super.getRoles();
    }

    @Override
    protected String getRole(int id) {
        return super.getRole(id);
    }

    @Override
    protected int getUserRole(int userId) {
        return super.getUserRole(userId);
    }

    @Override
    protected Cursor getUsersDetails() {
        return super.getUsersDetails();
    }

    @Override
    protected Cursor getUserDetails(int userId) {
        return super.getUserDetails(userId);
    }

    @Override
    protected String getPassword(int userId) {
        return super.getPassword(userId);
    }

    @Override
    protected Cursor getAccountIds(int userId) {
        return super.getAccountIds(userId);
    }

    @Override
    protected Cursor getAccountDetails(int accountId) {
        return super.getAccountDetails(accountId);
    }

    @Override
    protected BigDecimal getBalance(int accountId) {
        return super.getBalance(accountId);
    }

    @Override
    protected int getAccountType(int accountId) {
        return super.getAccountType(accountId);
    }

    @Override
    protected String getAccountTypeName(int accountTypeId) {
        return super.getAccountTypeName(accountTypeId);
    }

    @Override
    protected Cursor getAccountTypesId() {
        return super.getAccountTypesId();
    }

    @Override
    protected BigDecimal getInterestRate(int accountType) {
        return super.getInterestRate(accountType);
    }

    @Override
    protected Cursor getAllMessages(int userId) {
        return super.getAllMessages(userId);
    }

    @Override
    protected String getSpecificMessage(int messageId) {
        return super.getSpecificMessage(messageId);
    }

    @Override
    protected boolean updateRoleName(String name, int id) {
        return super.updateRoleName(name, id);
    }

    @Override
    protected boolean updateUserName(String name, int id) {
        return super.updateUserName(name, id);
    }

    @Override
    protected boolean updateUserAge(int age, int id) {
        return super.updateUserAge(age, id);
    }

    @Override
    protected boolean updateUserRole(int roleId, int id) {
        return super.updateUserRole(roleId, id);
    }

    @Override
    protected boolean updateUserAddress(String address, int id) {
        return super.updateUserAddress(address, id);
    }

    @Override
    protected boolean updateAccountName(String name, int id) {
        return super.updateAccountName(name, id);
    }

    @Override
    protected boolean updateAccountBalance(BigDecimal balance, int id) {
        return super.updateAccountBalance(balance, id);
    }

    @Override
    protected boolean updateAccountType(int typeId, int id) {
        return super.updateAccountType(typeId, id);
    }

    @Override
    protected boolean updateAccountTypeName(String name, int id) {
        return super.updateAccountTypeName(name, id);
    }

    @Override
    protected boolean updateAccountTypeInterestRate(BigDecimal interestRate, int id) {
        return super.updateAccountTypeInterestRate(interestRate, id);
    }

    @Override
    protected boolean updateUserPassword(String password, int id) {
        return super.updateUserPassword(password, id);
    }

    @Override
    protected boolean updateUserMessageState(int id) {
        return super.updateUserMessageState(id);
    }

    public void reinitialize() {
        super.onUpgrade(super.getWritableDatabase(), super.getDatabaseVersion(), super.getDatabaseVersion() + 1);
    }
}
