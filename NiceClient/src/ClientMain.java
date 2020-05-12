import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        NiceClient niceClient = new NiceClient();
        try {
            niceClient.run();
        } catch (CtrlDException e) {
            System.out.println("Сердечко: моя остановочка");
        }
        /*DatagramChannel client = DatagramChannel.open();
        String msg = new Scanner(System.in).nextLine();
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8989);
        client.send(buffer, serverAddress);
        buffer.clear();
        client.receive(buffer);
        buffer.flip();
        byte[] b = new byte[buffer.limit()];
        buffer.get(b, 0, b.length);
        System.out.println(new String(b));
        client.close();*/
    }
}
