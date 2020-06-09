import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.sql.SQLException;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import packets.AwesomeToNicePacket;
import packets.NiceToAwesomePacket;

public class AwesomeServer {
    private static Logger logger = LoggerFactory.getLogger(AwesomeServer.class);
    private CommandProcessor commandProcessor;
    private static final String LOCAL_IP = "localhost";

    public CommandProcessor getCommandProcessor() {
        return commandProcessor;
    }

    public void run() throws IOException {
        try (
             DatagramChannel serverChannel = DatagramChannel.open()) {
            serverChannel.configureBlocking(false);
            logger.info("Открываем канал передачи");
            InetSocketAddress address = new InetSocketAddress(LOCAL_IP, 8000);
            logger.info("Адрес сокета {}", address);
            serverChannel.bind(address);
            logger.info("Канал привязан к сокету с адресом {}", address);
            RequestReader requestReader = new RequestReader(serverChannel);
            DatabaseManager database = new DatabaseManager();
            commandProcessor = new CommandProcessor(database);
            logger.info("Сервер запущен. ");
            ResponseSender responseSender = new ResponseSender(serverChannel);
            logger.info("Сервер ждёт команды от клиента.");
            while (true) {

                Callable<NiceToAwesomePacket> task = requestReader::getNewPacket;
                FutureTask<NiceToAwesomePacket> future = new FutureTask<>(task);
                new Thread(future).start();
                NiceToAwesomePacket packet = future.get();

                Callable<AwesomeToNicePacket> call = () -> commandProcessor.runCommand(packet);
                ExecutorService service = Executors.newCachedThreadPool();
                Future<AwesomeToNicePacket> result = service.submit(call);
                AwesomeToNicePacket nicePacket = result.get();

                RecursiveAction action = new RecursiveAction() {
                    @Override
                    protected void compute() {
                        responseSender.sendResponse(nicePacket, packet.getSocketAddress());
                    }
                };
                action.fork();
                action.join();
                logger.info("Отправка ответа");
            }
        } catch (SQLException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
