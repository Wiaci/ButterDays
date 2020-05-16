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
    public static void main(String[] args) {
        try {
            NiceClient niceClient = new NiceClient();
            niceClient.run();
        } catch (CtrlDException e) {
            System.out.println("Сердечко: моя остановочка");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
