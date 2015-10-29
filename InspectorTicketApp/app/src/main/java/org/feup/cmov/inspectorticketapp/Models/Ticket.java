package org.feup.cmov.inspectorticketapp.Models;

import android.content.ContentValues;

import com.google.gson.Gson;

import lombok.Getter;

public class Ticket {

    final static Gson gson = new Gson();

    @Getter int ticketId;
    @Getter User user;
    @Getter long date;
    @Getter long purchaseTime;
    @Getter String uuid;
    @Getter String signature;
    @Getter int tripId;
    @Getter int fromTripStepNumber;
    @Getter int toTripStepNumber;
    @Getter int seatNumber;
    @Getter boolean used;
    
    @Getter Station fromStation;
    @Getter Station toStation;
    
    @Getter int hoursStart;
    @Getter int minutesStart;
    @Getter int hoursEnd;
    @Getter int minutesEnd;

    public ContentValues toDatabaseValues() {
        ContentValues values = new ContentValues();
        values.put(TicketEntry.COLUMN_NAME_ID, ticketId);
        values.put(TicketEntry.COLUMN_NAME_SIG, signature);
        values.put(TicketEntry.COLUMN_NAME_JSON, gson.toJson(this));
        return values;
    }
}
