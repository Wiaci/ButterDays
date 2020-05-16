import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;

public class ServerMain {

    static AwesomeServer awesomeServer;

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
            } else System.out.println("Нужен файл с коллекцией!");
        } catch (IOException e) {
            System.out.println("Произошло непредвиденное нечто");
        }
    }
}
