package org.feup.cmov.userticketapp.Models;

import lombok.Getter;

public class SharedDataFactory {
    private static SharedDataFactory ourInstance = new SharedDataFactory();

    @Getter private Station fromStation;
    @Getter private Station toStation;

    public static SharedDataFactory getInstance() {
        return ourInstance;
    }

    private SharedDataFactory() {}

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