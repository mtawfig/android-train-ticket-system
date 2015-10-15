package org.feup.cmov.userticketapp.Models;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class Itinerary {

    @Getter @Setter int hoursStart;
    @Getter @Setter int minutesStart;

    @Getter @Setter int hoursEnd;
    @Getter @Setter int minutesEnd;

    @Getter @Setter int duration;
    @Getter @Setter int cost;

    @Getter @Setter ArrayList<Step> steps;

    private class Step {
        @Getter @Setter int hoursStart;
        @Getter @Setter int minutesStart;

        @Getter @Setter int hoursEnd;
        @Getter @Setter int minutesEnd;

        @Getter @Setter String line;
        @Getter @Setter int numberOfStops;
        @Getter @Setter int duration;

        @Getter @Setter int wait;

        @Getter @Setter Station startStation;
        @Getter @Setter Station endStation;
    }
}
