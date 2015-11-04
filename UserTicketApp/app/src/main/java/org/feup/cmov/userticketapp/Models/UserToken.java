package org.feup.cmov.userticketapp.Models;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class UserToken {

    @Getter String token;
    @Getter Long expireDate;
    @Getter User user;

    public class User {
        @Getter int userId;

        @Getter String email;

        @Getter String name;
    }
}
