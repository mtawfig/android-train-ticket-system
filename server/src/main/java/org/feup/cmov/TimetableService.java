package org.feup.cmov;

import static spark.Spark.*;

public class TimetableService implements Service {

    public void startService() {
        get(Server.API_PREFIX + "/hello",
                (req, res) -> "Hello World");
    }
}
