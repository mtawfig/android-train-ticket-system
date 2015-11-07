package org.feup.cmov.inspectorticketapp.Models;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

public class Station {

    public static HashMap<Integer, String> names = new HashMap<>();

    @Getter @Setter int stationId;
    @Getter @Setter String name;
}
