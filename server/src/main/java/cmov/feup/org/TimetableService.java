package cmov.feup.org;

import static spark.Spark.*;

public class TimetableService implements Service {

    public void startService() {
        get("/hello", (req, res) -> "Hello World");
    }
}
