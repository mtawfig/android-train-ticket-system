package org.feup.cmov.userticketapp.Models;

import com.google.gson.Gson;

import java.lang.reflect.Field;

import lombok.Getter;
import lombok.Setter;

public class Ticket {
    @Getter int ticketId;
    @Getter int userId;
    @Getter long date;
    @Getter long purchaseTime;
    @Getter String uuid;
    @Getter String signature;
    @Getter int tripId;
    @Getter int fromTripStepNumber;
    @Getter int toTripStepNumber;
    @Getter int seatNumber;
    @Getter Boolean used;

    @Getter Station fromStation;
    @Getter int fromStationId;
    @Getter Station toStation;
    @Getter int toStationId;

    @Getter int hoursStart;
    @Getter int minutesStart;
    @Getter int hoursEnd;
    @Getter int minutesEnd;

    transient static Field[] transmittableFields;

    static {
        String[] stringFields = { "userId", "date", "purchaseTime", "uuid", "signature", "tripId",
                "fromTripStepNumber", "toTripStepNumber", "seatNumber", "fromStationId", "toStationId",
                "hoursStart", "minutesStart", "hoursEnd", "minutesEnd" };

        transmittableFields = new Field[stringFields.length];

        for(int i = 0; i < stringFields.length; i++) {
            try {
                transmittableFields[i] = Ticket.class.getDeclaredField(stringFields[i]);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public String toTransmittableString() {
        StringBuilder builder = new StringBuilder();
        for(Field field: transmittableFields) {
            try {
                builder.append(field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            builder.append(",");
        }
        return builder.toString();
    }

    transient static Gson gson = new Gson();

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
