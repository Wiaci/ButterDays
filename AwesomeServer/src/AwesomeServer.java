import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwesomeServer {
    private static Logger logger = LoggerFactory.getLogger(AwesomeServer.class);
    private String filename;
    private CommandProcessor commandProcessor;

    public AwesomeServer(String filename) {
        this.filename = filename;
    }

    public CommandProcessor getCommandProcessor() {
        return commandProcessor;
    }

    public void run() throws IOException {
        try (
             DatagramChannel serverChannel = DatagramChannel.open()) {
            serverChannel.configureBlocking(false);
            logger.info("Открываем канал передачи");
            InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8000);
            logger.info("Адрес сокета {}", address);
            serverChannel.bind(address);
            logger.info("Канал привязан к сокету с адресом {}", address);
            RequestReader requestReader = new RequestReader(serverChannel);
            commandProcessor = FileSaver.load(filename);
            logger.info("Сервер запущен. ");
            ResponseSender responseSender = new ResponseSender(serverChannel);
            logger.info("Сервер ждёт команды от клиента.");
            while (true) {
                NiceToAwesomePacket packet = requestReader.getNewPacket();
                if (packet.getCommand()[0].equals("exit")) break;

                responseSender.sendResponse(
                        commandProcessor.runCommand(packet),
                        packet.getSocketAddress());
                logger.info("Отправка ответа");
            }
        }
    }

}
