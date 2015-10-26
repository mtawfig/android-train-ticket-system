package org.feup.cmov.inspectorticketapp.Models;

import lombok.Getter;

public class UserToken {

    @Getter String token;

    @Getter User user;

    public class User {
        @Getter int userId;

        @Getter String email;

        @Getter String name;
    }
}
