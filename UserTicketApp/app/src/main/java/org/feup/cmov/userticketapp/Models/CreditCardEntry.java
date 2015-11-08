package org.feup.cmov.userticketapp.Models;

import android.provider.BaseColumns;

public abstract class CreditCardEntry implements BaseColumns {
    public static final String TABLE_NAME = "creditCard";
    public static final String COLUMN_NAME_NUMBER = "number";
    public static final String COLUMN_NAME_MONTH = "month";
    public static final String COLUMN_NAME_CODE = "code";
    public static final String COLUMN_NAME_YEAR = "year";
    public static final String COLUMN_NAME_USER_ID = "userId";

}