package org.feup.cmov.userticketapp.Models;

import java.util.ArrayList;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class Itinerary {

    @Getter int hoursStart;
    @Getter int minutesStart;

    @Getter int hoursEnd;
    @Getter int minutesEnd;

    @Getter int duration;
    @Getter int cost;

    @Getter @Setter long date;

    @Getter ArrayList<Step> steps;

    public class Train {
        @Getter int trainId;
        @Getter int capacity;
    }

    public class Step {
        @Getter int hoursStart;
        @Getter int minutesStart;

        @Getter int hoursEnd;
        @Getter int minutesEnd;

        @Getter String line;
        @Getter int numberOfStops;
        @Getter int duration;

        @Getter Integer wait;

        @Getter Station startStation;
        @Getter Station endStation;

        @Getter @Setter ArrayList<Integer> freeSeats;

        @Getter Train train;

        public long getStepId() {
            return Long.valueOf(String.format("%d%d%d%d%d%d%d%d", hoursStart, hoursEnd,
                    minutesStart, minutesEnd,
                    numberOfStops, duration,
                    startStation.getStationId(), endStation.getStationId()));
        }
    }
}
