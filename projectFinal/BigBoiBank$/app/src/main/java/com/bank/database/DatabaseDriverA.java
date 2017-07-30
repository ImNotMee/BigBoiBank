package com.bank.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bank.security.PasswordHelpers;

import java.math.BigDecimal;

/**
 * Created by Joe on 2017-07-17.
 */

public class DatabaseDriverA extends SQLiteOpenHelper {
  private static final int DATABASE_VERSION = 1;
  private static final String DATABASE_NAME = "bank.db";

  public DatabaseDriverA(Context context) {
    super(context, DATABASE_NAME, null, 1);

  }

  public int getDatabaseVersion() {
    return this.DATABASE_VERSION;
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL("CREATE TABLE ROLES "
            + "(ID INTEGER PRIMARY KEY NOT NULL,"
            + "NAME TEXT NOT NULL)");
    sqLiteDatabase.execSQL("CREATE TABLE ACCOUNTTYPES "
            + "(ID INTEGER PRIMARY KEY NOT NULL,"
            + "NAME TEXT NOT NULL,"
            + "INTERESTRATE TEXT)");
    sqLiteDatabase.execSQL("CREATE TABLE ACCOUNTS "
            + "(ID INTEGER PRIMARY KEY NOT NULL,"
            + "NAME TEXT NOT NULL,"
            + "BALANCE TEXT,"
            + "TYPE INTEGER NOT NULL,"
            + "FOREIGN KEY(TYPE) REFERENCES ACCOUNTTYPES(ID))");
    sqLiteDatabase.execSQL("CREATE TABLE USERS "
            + "(ID INTEGER PRIMARY KEY NOT NULL,"
            + "NAME TEXT NOT NULL,"
            + "AGE INTEGER NOT NULL,"
            + "ADDRESS CHAR(100),"
            + "ROLEID INTEGER,"
            + "FOREIGN KEY(ROLEID) REFERENCES ROLE(ID))");
    sqLiteDatabase.execSQL("CREATE TABLE USERACCOUNT "
            + "(USERID INTEGER NOT NULL,"
            + "ACCOUNTID INTEGER NOT NULL,"
            + "FOREIGN KEY(USERID) REFERENCES USER(ID),"
            + "FOREIGN KEY(ACCOUNTID) REFERENCES ACOUNT(ID),"
            + "PRIMARY KEY(USERID, ACCOUNTID))");
    sqLiteDatabase.execSQL("CREATE TABLE USERPW "
            + "(USERID INTEGER NOT NULL,"
            + "PASSWORD CHAR(64),"
            + "FOREIGN KEY(USERID) REFERENCES USER(ID))");
    sqLiteDatabase.execSQL("CREATE TABLE USERMESSAGES "
            + "(ID INTEGER PRIMARY KEY NOT NULL,"
            + "USERID INTEGER NOT NULL,"
            + "MESSAGE CHAR(512) NOT NULL,"
            + "VIEWED CHAR(1) NOT NULL,"
            + "FOREIGN KEY(USERID) REFERENCES USER(ID))");
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS USERMESSAGES");
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS USERPW");
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS USERACCOUNT");
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS USERS");
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ACCOUNTS");
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ACCOUNTTYPES");
    sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ROLES");

    onCreate(sqLiteDatabase);
  }

  //INSERTS
  protected long insertRole(String role) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("NAME", role);
    return sqLiteDatabase.insert("ROLES", null, contentValues);
  }

  protected long insertNewUser(String name, int age, String address, int roleId, String password) {
    long id = insertUser(name, age, address, roleId);
    insertPassword(password, (int) id);
    return id;
  }

  protected long insertAccountType(String name, BigDecimal interestRate) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("NAME", name);
    contentValues.put("INTERESTRATE", interestRate.toPlainString());
    return sqLiteDatabase.insert("ACCOUNTTYPES", null, contentValues);
  }

  protected long insertAccount(String name, BigDecimal balance, int typeId) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("NAME", name);
    contentValues.put("BALANCE", balance.toPlainString());
    contentValues.put("TYPE", typeId);
    return sqLiteDatabase.insert("ACCOUNTS", null, contentValues);
  }

  protected long insertUserAccount(int userId, int accountId) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("USERID", userId);
    contentValues.put("ACCOUNTID", accountId);
    return sqLiteDatabase.insert("USERACCOUNT", null, contentValues);
  }

  protected long insertMessage(int userId, String message) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("USERID", userId);
    contentValues.put("MESSAGE", message);
    contentValues.put("VIEWED", 0);
    return sqLiteDatabase.insert("USERMESSAGES", null, contentValues);
  }

  private long insertUser(String name, int age, String address, int roleId) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("NAME", name);
    contentValues.put("AGE", age);
    contentValues.put("ADDRESS", address);
    contentValues.put("ROLEID", roleId);

    return sqLiteDatabase.insert("USERS", null, contentValues);
  }

  private void insertPassword(String password, int userId) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();

    password = PasswordHelpers.passwordHash(password);

    contentValues.put("USERID", userId);
    contentValues.put("PASSWORD", password);
    sqLiteDatabase.insert("USERPW", null, contentValues);
  }

  //SELECT METHODS
  protected Cursor getRoles() {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    return sqLiteDatabase.rawQuery("SELECT * FROM ROLES;", null);
  }

  protected String getRole(int id) {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    Cursor cursor = sqLiteDatabase.rawQuery("SELECT NAME FROM ROLES WHERE ID = ?",
            new String[]{String.valueOf(id)});
    cursor.moveToFirst();
    String value = cursor.getString(cursor.getColumnIndex("NAME"));
    cursor.close();
    return value;

  }

  protected int getUserRole(int userId) {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    Cursor cursor = sqLiteDatabase.rawQuery("SELECT ROLEID FROM USERS WHERE ID = ?",
            new String[]{String.valueOf(userId)});
    cursor.moveToFirst();
    int result = cursor.getInt(cursor.getColumnIndex("ROLEID"));
    cursor.close();
    return result;
  }

  protected Cursor getUsersDetails() {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    return sqLiteDatabase.rawQuery("SELECT * FROM USERS", null);
  }

  protected Cursor getUserDetails(int userId) {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    return sqLiteDatabase.rawQuery("SELECT * FROM USERS WHERE ID = ?",
            new String[] {String.valueOf(userId)});
  }

  protected String getPassword(int userId) {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    Cursor cursor = sqLiteDatabase.rawQuery("SELECT PASSWORD FROM USERPW WHERE USERID = ?",
            new String[] {String.valueOf(userId)});
    cursor.moveToFirst();
    String result = cursor.getString(cursor.getColumnIndex("PASSWORD"));
    cursor.close();
    return result;
  }

  protected Cursor getAccountIds(int userId) {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    return sqLiteDatabase.rawQuery("SELECT ACCOUNTID FROM USERACCOUNT WHERE USERID = ?",
            new String[] {String.valueOf(userId)});

  }

  protected Cursor getAccountDetails(int accountId) {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    return sqLiteDatabase.rawQuery("SELECT * FROM ACCOUNTS WHERE ID = ?",
            new String[] {String.valueOf(accountId)});
  }

  protected BigDecimal getBalance(int accountId) {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    Cursor cursor = sqLiteDatabase.rawQuery("SELECT BALANCE FROM ACCOUNTS WHERE ID = ?",
            new String[] {String.valueOf(accountId)});
    cursor.moveToFirst();
    BigDecimal result = new BigDecimal(cursor.getString(cursor.getColumnIndex("BALANCE")));
    cursor.close();
    return result;
  }

  protected int getAccountType(int accountId) {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    Cursor cursor = sqLiteDatabase.rawQuery("SELECT TYPE FROM ACCOUNTS WHERE ID = ?",
            new String[] {String.valueOf(accountId)});
    cursor.moveToFirst();
    int result = cursor.getInt(cursor.getColumnIndex("TYPE"));
    cursor.close();
    return result;
  }

  protected String getAccountTypeName(int accountTypeId) {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    Cursor cursor = sqLiteDatabase.rawQuery("SELECT NAME FROM ACCOUNTTYPES WHERE ID = ?",
            new String[] {String.valueOf(accountTypeId)});
    cursor.moveToFirst();
    String result = cursor.getString(cursor.getColumnIndex("NAME"));
    cursor.close();
    return result;
  }

  protected Cursor getAccountTypesId() {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    return sqLiteDatabase.rawQuery("SELECT ID FROM ACCOUNTTYPES", null);
  }

  protected BigDecimal getInterestRate(int accountType) {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    Cursor cursor = sqLiteDatabase.rawQuery("SELECT INTERESTRATE FROM ACCOUNTTYPES WHERE ID = ?",
            new String[] {String.valueOf(accountType)});
    cursor.moveToFirst();
    BigDecimal result = new BigDecimal(cursor.getString(cursor.getColumnIndex("INTERESTRATE")));
    cursor.close();
    return result;
  }

  protected Cursor getAllMessages(int userId) {
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    return sqLiteDatabase.rawQuery("SELECT * FROM USERMESSAGES WHERE USERID = ?",
            new String[] {String.valueOf(userId)});
  }

  protected String getSpecificMessage(int messageId){
    SQLiteDatabase sqLiteDatabase = getReadableDatabase();
    Cursor cursor = sqLiteDatabase.rawQuery("SELECT MESSAGE FROM USERMESSAGES WHERE ID = ?",
            new String[] {String.valueOf(messageId)});
    cursor.moveToFirst();
    String result = cursor.getString(cursor.getColumnIndex("MESSAGE"));
    cursor.close();
    return result;
  }

  //UPDATE Methods
  protected boolean updateRoleName(String name, int id) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("NAME", name);
    return sqLiteDatabase.update("ROLES",contentValues,"ID = ?", new String[] {String.valueOf(id)})
            > 0;
  }

  protected boolean updateUserName(String name, int id) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("NAME", name);
    return sqLiteDatabase.update("USERS",contentValues,"ID = ?", new String[] {String.valueOf(id)})
            > 0;
  }

  protected boolean updateUserAge(int age, int id) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("AGE", age);
    return sqLiteDatabase.update("USERS",contentValues,"ID = ?", new String[] {String.valueOf(id)})
            > 0;
  }

  protected boolean updateUserRole(int roleId, int id) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("ROLEID", roleId);
    return sqLiteDatabase.update("USERS",contentValues,"ID = ?", new String[] {String.valueOf(id)})
            > 0;
  }

  protected boolean updateUserAddress(String address, int id) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("ADDRESS", address);
    return sqLiteDatabase.update("USERS",contentValues,"ID = ?", new String[] {String.valueOf(id)})
            > 0;
  }

  protected boolean updateAccountName(String name, int id) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("NAME", name);
    return sqLiteDatabase.update("ACCOUNTS",contentValues,"ID = ?",
            new String[] {String.valueOf(id)}) > 0;
  }

  protected boolean updateAccountBalance(BigDecimal balance, int id) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("BALANCE", balance.toPlainString());
    return sqLiteDatabase.update("ACCOUNTS",contentValues,"ID = ?",
            new String[] {String.valueOf(id)}) > 0;
  }

  protected boolean updateAccountType(int typeId, int id) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("TYPE", typeId);
    return sqLiteDatabase.update("ACCOUNTS",contentValues,"ID = ?",
            new String[] {String.valueOf(id)}) > 0;
  }

  protected boolean updateAccountTypeName(String name, int id) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("NAME", name);
    return sqLiteDatabase.update("ACCOUNTTYPES",contentValues,"ID = ?",
            new String[] {String.valueOf(id)}) > 0;
  }

  protected boolean updateAccountTypeInterestRate(BigDecimal interestRate, int id) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("INTERESTRATE", interestRate.toPlainString());
    return sqLiteDatabase.update("ACCOUNTTYPES",contentValues,"ID = ?",
            new String[] {String.valueOf(id)}) > 0;
  }

  protected boolean updateUserPassword(String password, int id) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("PASSWORD", password);
    return sqLiteDatabase.update("USERPW",contentValues,"USERID = ?",
            new String[] {String.valueOf(id)}) > 0;
  }

  protected boolean updateUserMessageState(int id) {
    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put("VIEWED", 1);
    return sqLiteDatabase.update("USERMESSAGES",contentValues,"ID = ?",
            new String[] {String.valueOf(id)}) > 0;
  }
}
