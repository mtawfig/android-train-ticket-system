package org.feup.cmov.userticketapp;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

public class Station {

    @Getter @Setter String name;
    @Getter @Setter LatLng location;
    @Getter @Setter int labelRotationDegrees;

    @Getter @Setter HashMap<String, Station> connections = new HashMap<>();

    public final static int LABEL_LEFT = 90;
    public final static int LABEL_DOWN = 180;
    public final static int LABEL_UP = 0;


    public Station(String name, LatLng location, int labelPosition) {
        this.name = name;
        this.location = location;
        this.labelRotationDegrees = labelPosition;
    }

    public static ArrayList<Station> getAllStations() {

        Station campanha = new Station("Campanha", new LatLng(41.1505929,-8.5859497), LABEL_LEFT);
        Station espinho = new Station("Espinho", new LatLng(41.0043836,-8.6456402), LABEL_LEFT);
        Station francelos  = new Station("Francelos", new LatLng(41.0812921,-8.6475706), LABEL_LEFT);
        Station saoromao  = new Station("S. Romão", new LatLng(41.277871,-8.5536718), LABEL_LEFT);
        Station ermesinde  = new Station("Ermesinde", new LatLng(41.2169514,-8.5540581), LABEL_LEFT);
        Station parada  = new Station("Parada", new LatLng(41.1602043,-8.3726025), LABEL_DOWN);
        Station saomartinho  = new Station("S. Martinho", new LatLng(41.1603533,-8.4695933), LABEL_UP);

        campanha.connections.put("S", francelos);
        campanha.connections.put("N", ermesinde);
        campanha.connections.put("E", saomartinho);

        francelos.connections.put("N", campanha);
        francelos.connections.put("S", espinho);

        espinho.connections.put("N", francelos);

        ermesinde.connections.put("N", saoromao);
        ermesinde.connections.put("S", campanha);

        saoromao.connections.put("S", ermesinde);

        saomartinho.connections.put("W", campanha);
        saomartinho.connections.put("E", parada);

        parada.connections.put("W", saomartinho);

        return new ArrayList<>(Arrays.asList(campanha, espinho, francelos, saoromao, ermesinde, parada, saomartinho));
    }
}
