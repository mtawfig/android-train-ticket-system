package org.feup.cmov.inspectorticketapp.Models;

import lombok.Getter;
import lombok.Setter;

public class SharedDataSingleton {
    private static SharedDataSingleton ourInstance = new SharedDataSingleton();

    @Getter @Setter private Ticket scannedTicket;

    public static SharedDataSingleton getInstance() {
        return ourInstance;
    }

    private SharedDataSingleton() {}
}
