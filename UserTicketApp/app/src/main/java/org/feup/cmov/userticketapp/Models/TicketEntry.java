package org.feup.cmov.userticketapp.Models;

import android.provider.BaseColumns;

public abstract class TicketEntry implements BaseColumns {
    public static final String TABLE_NAME = "ticket";
    public static final String COLUMN_NAME_UUID = "uuid";
    public static final String COLUMN_NAME_JSON = "json";
}