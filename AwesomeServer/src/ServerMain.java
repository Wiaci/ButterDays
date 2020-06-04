import java.io.*;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMain {

    static AwesomeServer awesomeServer;
    private static Logger logger = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {
        /*Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (awesomeServer != null) {
                CollectionSaver.save(awesomeServer.getCommandProcessor(), args[0]);
            }
        }));*/
        try {
            awesomeServer = new AwesomeServer();
            awesomeServer.run();
        } catch (IOException e) {
            logger.warn(Arrays.toString(e.getStackTrace()));
        }
    }
}
