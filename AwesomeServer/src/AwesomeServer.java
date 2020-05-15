import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwesomeServer {
    private static final Logger logger = LoggerFactory.getLogger(AwesomeServer.class);
    public void run() throws IOException {
        try (
             DatagramChannel serverChannel = DatagramChannel.open()) {
            serverChannel.configureBlocking(false);
            logger.info("Открываем канал передачи {}", serverChannel);
            InetSocketAddress address = new InetSocketAddress("localhost", 8000);
            logger.info("Адрес сокета {}", address);
            serverChannel.bind(address);
            logger.info("Сервер привязан к сокету с адзресом {}", address);
            RequestReader requestReader = new RequestReader(serverChannel);
            CommandProcessor commandProcessor = new CommandProcessor();
            System.out.println("Сервер совсем запустился...");
            logger.info("Сервер запущен. ", commandProcessor);
            ResponseSender responseSender = new ResponseSender(serverChannel);
            logger.info("Сервер ждёт команды от клиента.", requestReader);
            while (true) {
                NiceToAwesomePacket packet = requestReader.getNewPacket();
                logger.info("Получение нового запроса {}", packet);
                if (packet.getCommand()[0].equals("exit")) break;

                responseSender.sendResponse(
                        commandProcessor.runCommand(packet),
                        packet.getSocketAddress());
                logger.info("Отправка ответа {}", packet);
            }
        }
    }

}
