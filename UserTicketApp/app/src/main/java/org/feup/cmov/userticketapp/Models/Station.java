package org.feup.cmov.userticketapp.Models;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class Station {

    @Getter @Setter int stationId;
    @Getter @Setter String name;
    private LatLng location;
    @Getter @Setter Float latitude;
    @Getter @Setter Float longitude;
    @Getter @Setter int labelRotationDegrees;

    @Getter @Setter ArrayList<Connection> connections;

    public final static int LABEL_LEFT = 90;
    public final static int LABEL_DOWN = 180;
    public final static int LABEL_UP = 0;

    public LatLng getLocation() {
        if (location == null) {
            location = new LatLng(latitude, longitude);
        }
        return location;
    }

    public Station(String name, LatLng location, int labelPosition) {
        this.name = name;
        this.location = location;
        this.labelRotationDegrees = labelPosition;
    }

    public class Connection {
        @Getter @Setter String direction;
        @Getter @Setter String line;
        @Getter @Setter int fromStationId;
        @Getter @Setter int toStationId;
    }
}
