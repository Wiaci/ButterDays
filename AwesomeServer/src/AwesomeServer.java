import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class AwesomeServer {

    public void run() throws IOException {
        try (DatagramChannel serverChannel = DatagramChannel.open()) {
            serverChannel.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress("localhost", 8000);
            serverChannel.bind(address);
            RequestReader requestReader = new RequestReader(serverChannel);
            CommandProcessor commandProcessor = new CommandProcessor();
            System.out.println("Сервер совсем запустился...");
            ResponseSender responseSender = new ResponseSender(serverChannel);
            while (true) {
                NiceToAwesomePacket packet = requestReader.getNewPacket();
                if (packet.getCommand()[0].equals("exit")) break;
                responseSender.sendResponse(
                        commandProcessor.runCommand(packet),
                        packet.getSocketAddress());
            }
        //TODO streamCorrupted
        }
    }

}
