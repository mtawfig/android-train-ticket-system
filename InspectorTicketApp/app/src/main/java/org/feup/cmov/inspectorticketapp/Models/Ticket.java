package org.feup.cmov.inspectorticketapp.Models;

import android.content.ContentValues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import lombok.Getter;
import lombok.Setter;

public class Ticket {

    final static private transient Gson gson = new Gson();

            @Getter         int ticketId;
            @Getter         User user;
    @Expose @Getter @Setter int userId;
    @Expose @Getter         long date;
    @Expose @Getter         long purchaseTime;
    @Expose @Getter         String uuid;
            @Getter         String signature;
    @Expose @Getter         int tripId;
    @Expose @Getter         int fromTripStepNumber;
    @Expose @Getter         int toTripStepNumber;
    @Expose @Getter         int seatNumber;
            @Getter @Setter boolean used;

            @Getter         Station fromStation;
    @Expose @Getter         int fromStationId;
            @Getter         Station toStation;
    @Expose @Getter         int toStationId;

    @Expose @Getter         int hoursStart;
    @Expose @Getter         int minutesStart;
    @Expose @Getter         int hoursEnd;
    @Expose @Getter         int minutesEnd;

    public ContentValues toDatabaseValues() {
        ContentValues values = new ContentValues();
        values.put(TicketEntry.COLUMN_NAME_UUID, uuid);
        values.put(TicketEntry.COLUMN_NAME_JSON, gson.toJson(this));
        return values;
    }

    final static private transient Gson gsonWithExpose = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public String toVerifiableJson() {
        return gsonWithExpose.toJson(this);
    }

    transient static Field[] transmittableFields;
    transient static Class[] fieldTypes;

    static {
        String[] stringFields = { "userId", "date", "purchaseTime", "uuid", "signature", "tripId",
                "fromTripStepNumber", "toTripStepNumber", "seatNumber", "fromStationId", "toStationId",
                "hoursStart", "minutesStart", "hoursEnd", "minutesEnd" };

        fieldTypes = new Class[]{Integer.class, Long.class, Long.class, String.class, String.class, Integer.class,
                Integer.class, Integer.class, Integer.class, Integer.class, Integer.class,
                Integer.class, Integer.class, Integer.class, Integer.class};

        transmittableFields = new Field[stringFields.length];

        for(int i = 0; i < stringFields.length; i++) {
            try {
                transmittableFields[i] = Ticket.class.getDeclaredField(stringFields[i]);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public static Ticket fromTransmittableString(String string) {
        String[] dataArray = string.split(",");

        Ticket ticket = new Ticket();
        for(int i = 0; i < dataArray.length; i++) {
            Field field = transmittableFields[i];
            Class type = fieldTypes[i];
            String data = dataArray[i];
            field.getGenericType();

            try {
                Method m = type.getDeclaredMethod("valueOf", String.class);
                field.set(ticket, m.invoke(null, data));
            } catch (Exception e) {
                try {
                    field.set(ticket, data);
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return ticket;
    }

    public boolean getUsed() {
        return used;
    }
}
