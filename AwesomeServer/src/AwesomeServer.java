import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwesomeServer {
    private static Logger logger = LoggerFactory.getLogger(AwesomeServer.class);
    private DatabaseManager database;

    public AwesomeServer(String dbHost, String dbPort, String username, String password) {
        try {
            database = new DatabaseManager(dbHost, dbPort, username, password);
        } catch (SQLException e) {
            logger.warn("Connection to database failed!");
            System.exit(2);
        }
    }

    public void run() throws IOException {
        try (
             DatagramChannel serverChannel = DatagramChannel.open()) {
            serverChannel.configureBlocking(false);
            logger.info("Открываем канал передачи");
            InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 8000);
            logger.info("Адрес сокета {}", address);
            serverChannel.bind(address);
            logger.info("Канал привязан к сокету с адресом {}", address);
            logger.info("Сервер запущен. ");
            logger.info("Сервер ждёт команды от клиента.");
            RequestReader reader = new RequestReader(serverChannel);
            CommandProcessor processor = new CommandProcessor(database);
            while (true) {
                SocketAddress socketAddress = reader.getAddress();
                new Thread(new RequestReader(
                        serverChannel, database, socketAddress, reader.getBuffer(), processor))
                        .start();
            }
        }
    }

}
