package org.feup.cmov.userticketapp.Models;

import java.util.ArrayList;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class SharedDataSingleton {
    private static SharedDataSingleton ourInstance = new SharedDataSingleton();

    @Getter private Station fromStation;
    @Getter private Station toStation;
    @Getter @Setter private Station selectedStation;

    @Getter @Setter private Date selectedDate;

    @Getter @Setter private String creditCardNumber = "1111111111111110";
    @Getter @Setter private String creditCardMonth = "1";
    @Getter @Setter private String creditCardYear = "16";
    @Getter @Setter private String creditCardCode = "123";

    @Getter @Setter private Ticket selectedTicket;

    @Getter @Setter private ArrayList<Integer> arraySeatNumber = new ArrayList<>();

    public static SharedDataSingleton getInstance() {
        return ourInstance;
    }

    private SharedDataSingleton() {}

    public boolean setFromStation(Station station) {
        if (station != null && station != toStation && station != fromStation) {
            fromStation = station;
            return true;
        }
        return false;
    }

    public boolean setToStation(Station station) {
        if (station != null && station != toStation && station != fromStation) {
            toStation = station;
            return true;
        }
        return false;
    }

    public boolean isFromAndToStationSet() {
        return fromStation != null && toStation != null;
    }
}
