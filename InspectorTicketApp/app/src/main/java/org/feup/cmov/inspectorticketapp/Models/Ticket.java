package org.feup.cmov.inspectorticketapp.Models;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

public class Ticket {

    final static private transient Gson gson = new Gson();

            @Getter int ticketId;
            @Getter User user;
    @Expose @Getter @Setter int userId;
    @Expose @Getter long date;
    @Expose @Getter long purchaseTime;
    @Expose @Getter String uuid;
            @Getter String signature;
    @Expose @Getter int tripId;
    @Expose @Getter int fromTripStepNumber;
    @Expose @Getter int toTripStepNumber;
    @Expose @Getter int seatNumber;
            @Getter boolean used;

            @Getter Station fromStation;
    @Expose @Getter int fromStationId;
            @Getter Station toStation;
    @Expose @Getter int toStationId;

    @Expose @Getter int hoursStart;
    @Expose @Getter int minutesStart;
    @Expose @Getter int hoursEnd;
    @Expose @Getter int minutesEnd;

    public ContentValues toDatabaseValues() {
        ContentValues values = new ContentValues();
        values.put(TicketEntry.COLUMN_NAME_ID, ticketId);
        values.put(TicketEntry.COLUMN_NAME_SIG, signature);
        values.put(TicketEntry.COLUMN_NAME_JSON, gson.toJson(this));
        return values;
    }


    final static private transient Gson gsonWithExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public String toVerifiableJson() {
        fromStationId = fromStation.getStationId();
        toStationId = toStation.getStationId();
        return gsonWithExpose.toJson(this);
    }
}
