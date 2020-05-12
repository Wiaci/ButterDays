import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class NiceClient {

    public void run() throws IOException, CtrlDException {
        try (DatagramChannel clientChannel = DatagramChannel.open()) {
            clientChannel.configureBlocking(false);
            InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8000);
            ResponseAcceptor responseAcceptor = new ResponseAcceptor(clientChannel);
            UserMagicInteract user =
                    new UserMagicInteract(
                            new BufferedReader(
                                    new InputStreamReader(System.in)), false);
            RequestCreator requestCreator = new RequestCreator(clientChannel, user);
            user.printHello();
            while (true) {
                String[] newCommand = user.getNewCommand();
                if (newCommand.length == 0) continue;
                if (newCommand[0].equals("exit")) break;
                if (newCommand[0].equals("help")) {
                    user.help();
                    continue;
                }
                if (newCommand[0].equals("execute_script") && newCommand.length > 1) {
                    executeScript(newCommand[1]);
                    continue;
                }
                if (!user.check(newCommand)) {
                    System.out.println("Введи help и прозрей!");
                    continue;
                }
                NiceToAwesomePacket packet = requestCreator.createPacket(newCommand);
                requestCreator.sendResponse(packet, serverAddress);

                AwesomeToNicePacket packet1 = responseAcceptor.getResponsePacket();
                user.printResponse(packet1);
            }
        }
    }

    public void executeScript(String filename) {}
}
//