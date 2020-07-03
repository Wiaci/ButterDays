import ClientServerCommunicaion.NiceClient;
import GUI.Face;
import GUI.GuiManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

public class FireStarter {

    public static void main(String[] args) {
        try {
            if (args.length == 2) {
                NiceClient niceClient = new NiceClient(InetAddress.getByName(args[0]), args[1]);
                GuiManager gui = new GuiManager(niceClient);
            } else {
                NiceClient niceClient = new NiceClient(InetAddress.getByName("192.168.0.101"), "8000");
                GuiManager gui = new GuiManager(niceClient);
                //System.out.println("Введите аргументы командной строки: ADDRESS PORT");
            }
        } catch (Exception e) { // Переделать когда избавимся от CtrlDException
            e.printStackTrace();
        }
    }
}