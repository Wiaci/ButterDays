import ClientServerCommunicaion.NiceClient;
import GUI.GuiGarbage;

import javax.swing.*;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class FireStarter {

    public static void main(String[] args) {
        try {
            if (args.length == 2) {
                NiceClient niceClient = new NiceClient(InetAddress.getByName(args[0]), args[1]);
                GuiGarbage gui = new GuiGarbage(niceClient);
                SwingUtilities.invokeLater(gui);
            } else {
                System.out.println("Введите аргументы командной строки: ADDRESS PORT");
            }
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }
}