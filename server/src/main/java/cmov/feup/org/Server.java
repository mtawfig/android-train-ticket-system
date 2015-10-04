package cmov.feup.org;

import org.apache.commons.cli.*;

import java.util.ArrayList;

import static spark.Spark.before;
import static spark.SparkBase.port;

public class Server {

    private static final int DEFAULT_SERVER_PORT = 9090;
    public static final String API_PREFIX = "/api";

    private static void enableCORS(final String origin, final String methods, final String headers) {
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Allow-Credentials", "true");
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
        });
    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "help", false, "Command line argument help");
        options.addOption("s", "serverPort", true, "pt.up.fe.cmov.Server port");

        CommandLineParser commandLineParser = new DefaultParser();

        int serverPort = DEFAULT_SERVER_PORT;

        try {
            CommandLine cmd = commandLineParser.parse(options, args);
            if(cmd.hasOption("s")) {
                try {
                    serverPort = Integer.parseInt(cmd.getOptionValue("s"));
                    if(serverPort <= 0)
                        throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    System.out.println("Invalid port number");
                    return;
                }
            }
        } catch (ParseException e) {
            System.out.println("Failed to parse command line arguments, correct usage:\n");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "EventConsumerGroup", options );
            return;
        }

        port(serverPort);

        ArrayList<Service> services = new ArrayList<>();
        services.add(new TimetableService());

        services.forEach(Service::startService);

        // enableCORS("some address", "*", "*");
    }
}
