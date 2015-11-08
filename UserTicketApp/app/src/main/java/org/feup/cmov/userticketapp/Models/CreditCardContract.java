package org.feup.cmov.userticketapp.Models;

public class CreditCardContract {
    public CreditCardContract() {}

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CreditCardEntry.TABLE_NAME + " (" +
                    CreditCardEntry.COLUMN_NAME_NUMBER + " INTEGER PRIMARY KEY, " +
                    CreditCardEntry.COLUMN_NAME_MONTH + " INTEGER, " +
                    CreditCardEntry.COLUMN_NAME_YEAR + " INTEGER, " +
                    CreditCardEntry.COLUMN_NAME_CODE + " INTEGER, " +
                    CreditCardEntry.COLUMN_NAME_USER_ID + " INTEGER " +
                    " ); ";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CreditCardEntry.TABLE_NAME + ";";
}
