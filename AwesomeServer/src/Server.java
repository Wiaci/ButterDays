import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Server {
    public static void main(String[] args) throws IOException {
        DatagramChannel server = DatagramChannel.open();
        InetSocketAddress iAdd = new InetSocketAddress("localhost", 8989);
        server.bind(iAdd);
        System.out.println("Server Started: " + iAdd);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //receive buffer from client.
        while (true) {
            buffer.clear();
            SocketAddress remoteAdd = server.receive(buffer);
            //change mode of buffer
            buffer.flip();
            int limits = buffer.limit();
            byte[] bytes = new byte[limits];
            buffer.get(bytes, 0, limits);
            String msg = new String(bytes);
            if (msg.equals("exit")) break;
            System.out.println("Client at " + remoteAdd + "  sent: " + msg);
            buffer.flip();
            server.send(buffer, remoteAdd);
        }
        server.close();

        /*byte[] bytes = new byte[10];
        SocketAddress address = new InetSocketAddress(8000);
        System.out.println("Server Started\n" + address);
        DatagramChannel channel = DatagramChannel.open();
        channel.socket().bind(address);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.clear();
        channel.receive(buffer);
        System.out.println("Принято");
        for (int j = 0; j < 10; j++) {
            bytes[j] *= 2;
        }
        buffer.flip();
        channel.send(buffer, address);*/
    }
}
