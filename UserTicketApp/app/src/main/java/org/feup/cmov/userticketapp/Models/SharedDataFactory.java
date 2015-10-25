package org.feup.cmov.userticketapp.Models;

import lombok.Getter;
import lombok.Setter;

public class SharedDataFactory {
    private static SharedDataFactory ourInstance = new SharedDataFactory();

    @Getter private Station fromStation;
    @Getter private Station toStation;
    @Getter @Setter private Station selectedStation;

    @Getter @Setter private String creditCardNumber = "1111111111111111";
    @Getter @Setter private String creditCardMonth = "1";
    @Getter @Setter private String creditCardYear = "16";
    @Getter @Setter private String creditCardCode = "123";

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
