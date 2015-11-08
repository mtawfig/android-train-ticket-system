package org.feup.cmov.userticketapp.Models;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class SharedDataSingleton {
    private static SharedDataSingleton ourInstance = new SharedDataSingleton();

    @Getter private Station fromStation;
    @Getter private Station toStation;
    @Getter @Setter private Station selectedStation;

    @Getter @Setter private Date selectedDate;

    @Getter @Setter private CreditCard creditCard;

    @Getter @Setter private Ticket selectedTicket;

    @Getter @Setter private ArrayList<Integer> arraySeatNumber = new ArrayList<>();

    @Getter @Setter private Map<Integer, ArrayList<Integer>> freeSeats = new HashMap<>();
    @Getter @Setter private Map<Integer, Integer> trainCapacity = new HashMap<>();

    public static SharedDataSingleton getInstance() {
        return ourInstance;
    }

    private SharedDataSingleton() {}

    public boolean setFromStation(Station station) {
        if (station != toStation && station != fromStation) {
            fromStation = station;
            return true;
        }
        return false;
    }

    public boolean setToStation(Station station) {
        if (station != toStation && station != fromStation) {
            toStation = station;
            return true;
        }
        return false;
    }

    public boolean isFromAndToStationSet() {
        return fromStation != null && toStation != null;
    }
}
