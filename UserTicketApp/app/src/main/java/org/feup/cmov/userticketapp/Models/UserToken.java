package org.feup.cmov.userticketapp.Models;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class UserToken {

    @Getter String token;

    @Getter ArrayList<User> steps;

    public class User {
        @Getter int userId;

        @Getter String line;

        @Getter String name;
    }
}
