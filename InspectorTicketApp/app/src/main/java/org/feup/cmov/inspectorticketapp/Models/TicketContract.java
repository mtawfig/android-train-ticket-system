package org.feup.cmov.inspectorticketapp.Models;

import android.provider.BaseColumns;

public final class TicketContract {
    public TicketContract() {}

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TicketEntry.TABLE_NAME + " (" +
                    TicketEntry.COLUMN_NAME_UUID + " TEXT PRIMARY KEY," +
                    TicketEntry.COLUMN_NAME_JSON + " TEXT" +
                    " ); " +
                    "CREATE INDEX uuid_idx ON " + TicketEntry.TABLE_NAME + " (" + TicketEntry.COLUMN_NAME_UUID + ");";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TicketEntry.TABLE_NAME + ";";
}