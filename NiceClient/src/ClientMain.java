import java.io.*;
import java.net.InetAddress;

public class ClientMain {
    public static void main(String[] args) {
        try {
            if (args.length == 2) {
                NiceClient niceClient = new NiceClient(InetAddress.getByName(args[0]), args[1]);
                niceClient.run();
            } else {
                System.out.println("Введите аргументы командной строки: ADDRESS PORT");
            }
        } catch (CtrlDException e) {
            System.out.println("\nСердечко: моя остановочка");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
