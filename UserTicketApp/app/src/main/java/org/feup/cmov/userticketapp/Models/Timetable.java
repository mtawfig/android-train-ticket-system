package org.feup.cmov.userticketapp.Models;


import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;

public class Timetable {

    @Getter HashMap<Integer, ArrayList<Trip>> trips;
    @Getter String line;
    @Getter String direction;

    public class Trip {
        @Getter int hoursStart;
        @Getter int minutesStart;

        @Getter int hoursEnd;
        @Getter int minutesEnd;

        @Getter int tripNumber;
        @Getter int tripStepNumber;

        @Getter String direction;
        @Getter String line;

        @Getter Station fromStation;
        @Getter Station toStation;
    }
}
