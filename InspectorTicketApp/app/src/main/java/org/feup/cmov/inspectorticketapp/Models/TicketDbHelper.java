package org.feup.cmov.inspectorticketapp.Models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

public class TicketDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "ticket.db";
    public final static Gson gson = new Gson();

    public TicketDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TicketContract.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TicketContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static Ticket getTicketWithSignature(SQLiteDatabase db, String signature) {
        String[] projection = { TicketEntry.COLUMN_NAME_JSON };
        String selection = TicketEntry.COLUMN_NAME_SIG + " LIKE ?";
        String[] selectionArgs = { signature };

        Cursor cursor = db.query(
                TicketEntry.TABLE_NAME,  // The table to query
                projection,              // The columns to return
                selection,               // The columns for the WHERE clause
                selectionArgs,           // The values for the WHERE clause
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                null                     // The sort order
        );

        Ticket ticket = null;
        while (cursor.moveToNext()) {
            String ticketJson = cursor.getString(cursor.getColumnIndexOrThrow(TicketEntry.COLUMN_NAME_JSON));
            ticket = gson.fromJson(ticketJson, Ticket.class);
        }

        return ticket;
    }
}