package org.feup.cmov.inspectorticketapp.Models;

import android.provider.BaseColumns;

public abstract class TicketEntry implements BaseColumns {
    public static final String TABLE_NAME = "ticket";
    public static final String COLUMN_NAME_ID = "ticketId";
    public static final String COLUMN_NAME_SIG = "signature";
    public static final String COLUMN_NAME_JSON = "json";
}