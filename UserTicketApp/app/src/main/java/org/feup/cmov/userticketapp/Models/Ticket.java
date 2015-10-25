package org.feup.cmov.userticketapp.Models;

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
    @Getter boolean used;

    @Getter Station fromStation;
    @Getter Station toStation;

    @Getter int hoursStart;
    @Getter int minutesStart;
    @Getter int hoursEnd;
    @Getter int minutesEnd;
}
