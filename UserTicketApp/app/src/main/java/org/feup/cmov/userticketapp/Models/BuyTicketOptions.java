package org.feup.cmov.userticketapp.Models;

import java.util.ArrayList;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class BuyTicketOptions {
    @Getter @Setter Station startStation;
    @Getter @Setter Station endStation;

    @Getter @Setter CreditCard creditCard;
    @Getter @Setter Date date;

    @Getter @Setter ArrayList<Integer> arraySeatNumber;
}
