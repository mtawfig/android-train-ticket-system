package org.feup.cmov.userticketapp.Models;

import java.util.Date;

import lombok.Getter;

public class GetItineraryOptions {
    @Getter Date selectedDate;
    @Getter Station fromStation;
    @Getter Station toStation;

    public GetItineraryOptions(Station fromStation, Station toStation, Date selectedDate) {
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.selectedDate = selectedDate;
    }

    public GetItineraryOptions(Station fromStation, Station toStation) {
        this.fromStation = fromStation;
        this.toStation = toStation;
    }
}
