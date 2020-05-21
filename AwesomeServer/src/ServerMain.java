import java.io.*;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMain {

    static AwesomeServer awesomeServer;
    private static Logger logger = LoggerFactory.getLogger(AwesomeServer.class);

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (awesomeServer != null) {
                FileSaver.save(awesomeServer.getCommandProcessor(), args[0]);
            }
        }));
        try {
            if (args.length > 0) {
                awesomeServer = new AwesomeServer(args[0]);
                awesomeServer.run();
            } else logger.info("Нужен файл с коллекцией!");
        } catch (IOException e) {
            logger.warn(Arrays.toString(e.getStackTrace()));
        }
    }
}
