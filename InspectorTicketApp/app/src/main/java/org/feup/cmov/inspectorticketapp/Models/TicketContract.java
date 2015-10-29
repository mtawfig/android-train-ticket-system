package org.feup.cmov.inspectorticketapp.Models;

import android.provider.BaseColumns;

public final class TicketContract {
    public TicketContract() {}

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TicketEntry.TABLE_NAME + " (" +
                    TicketEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    TicketEntry.COLUMN_NAME_SIG + " TEXT," +
                    TicketEntry.COLUMN_NAME_JSON + " TEXT" +
                    " );";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TicketEntry.TABLE_NAME + ";";
}