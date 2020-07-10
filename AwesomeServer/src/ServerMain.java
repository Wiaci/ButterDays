import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMain {

    static AwesomeServer awesomeServer;
    private static Logger logger = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {
        try {
            if (args.length == 4) {
                awesomeServer = new AwesomeServer(args[0], args[1], args[2], args[3]);
                awesomeServer.run();
            } else {

                logger.info("Введите аргументы командной строки: HOST PORT USERNAME PASSWORD");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
