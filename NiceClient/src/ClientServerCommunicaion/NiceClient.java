package ClientServerCommunicaion;

import ClientServerCommunicaion.packets.AwesomeToNicePacket;
import ClientServerCommunicaion.packets.NiceToAwesomePacket;
import ClientServerCommunicaion.sourse.StudyGroup;
import GUI.GuiManager;

import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;

public class NiceClient {

    private PacketMaker packetMaker;
    private ResponseAcceptor responseAcceptor;
    private RequestCreator requestCreator;
    private final DatagramSocket socket;
    private InetAddress serverAddress;
    private int port;

    private NiceToAwesomePacket nicePacket;

    public NiceClient(InetAddress serverAddress, String port) throws SocketException{
        if (port.matches("\\d{1,5}")) {
            this.port = Integer.parseInt(port);
        }
        socket = new DatagramSocket();
        this.serverAddress = serverAddress;
        socket.setSoTimeout(5000);
        packetMaker = new PacketMaker();
        responseAcceptor = new ResponseAcceptor(socket);
        requestCreator = new RequestCreator(serverAddress, packetMaker);
    }

    public boolean checkConnection() {
        NiceToAwesomePacket checkPacket = packetMaker.createPacket(new String[] {"check"}, "1", "1");
        requestCreator.sendResponse(checkPacket, socket, serverAddress, port);
        try {
            responseAcceptor.getResponsePacket();
        } catch (SocketTimeoutException e) {
            return false;
        }
        return true;
    }

    public String launchCommand(String command, String login, String password) throws SocketTimeoutException {
        String[] newCommand = command.split("\\s+");
        NiceToAwesomePacket packetRequest = packetMaker.createPacket(newCommand, login, password);
        requestCreator.sendResponse(packetRequest, socket, serverAddress, port);

        AwesomeToNicePacket packetResponse = responseAcceptor.getResponsePacket();
        return packetResponse.getResponse();
    }

    public String launchCommand(String command, StudyGroup group, String login, String password) throws SocketTimeoutException {
        String[] newCommand = command.split("\\s+");
        NiceToAwesomePacket packetRequest = packetMaker.createPacket(newCommand, group, login, password);
        requestCreator.sendResponse(packetRequest, socket, serverAddress, port);

        AwesomeToNicePacket packetResponse = responseAcceptor.getResponsePacket();
        return packetResponse.getResponse();
    }



    LinkedList<String> listOfScripts = new LinkedList<>();
    HashMap<String, String> problemFiles = new HashMap<>(); // мап с проблемными файлами и их вызовами
    /**
     * Метод для выполнения скрипта
     * @param filename имя файла содержащего скрипт
     */
    /*private void executeScript(String filename) throws CtrlDException{
        try {
            System.out.println("'"+filename+"'");
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            listOfScripts.add(filename);
            String newLine;
            user = new DataChecker(reader, true);
            do {
                newLine = reader.readLine();
                if (newLine == null) break;
                newLine = newLine.trim();
                if (!newLine.startsWith("execute_script") || !listOfScripts.contains(newLine.replaceFirst("execute_script", "").trim())) {
                    launchCommand(newLine.split(" "));
                } else problemFiles.put(filename, newLine.replaceFirst("execute_script", "").trim());
            } while (true);
        } catch (FileNotFoundException e) {
            System.out.println("Такого файла нет");
        } catch (IOException e) {
            System.out.println("Во время выполнения скрипта произошел разрыв соединения! Нам очень жаль...");
        } finally {
            if (listOfScripts.size() > 0) listOfScripts.removeLast();
            if (listOfScripts.size() == 0 && problemFiles.size() > 0) {
                System.out.println("\n!!!\nУ вас рот в гов... Ой, то есть рекурсия в скрипте\n" +
                        "В следующих файлах был обнаружен рекурсивный вызов:");
                for (String i: problemFiles.keySet()) {
                    System.out.println("\t" + i + " - вызов скрипта " + problemFiles.get(i));
                }
                System.out.println("!!!");
                problemFiles.clear();
            }
            user = new DataChecker(new BufferedReader(new InputStreamReader(System.in)), false);
        }
    }*/
    public boolean authorize(String login, String password) throws SocketTimeoutException {
        System.out.println(login + " " + password);
        NiceToAwesomePacket packet = packetMaker.createPacket(new String[]{"authorize"}, login, password);
        requestCreator.sendResponse(packet, socket, serverAddress, port);
        AwesomeToNicePacket nicePacket = responseAcceptor.getResponsePacket();
        return nicePacket.getResponse().equals("Success");
    }

    public boolean register(String login, String password) throws SocketTimeoutException {
        NiceToAwesomePacket packet = packetMaker.createPacket(new String[]{"commonRegister"}, login, password);
        requestCreator.sendResponse(packet, socket, serverAddress, port);
        AwesomeToNicePacket nicePacket = responseAcceptor.getResponsePacket();
        String serverResponse = nicePacket.getResponse();
        return serverResponse.equals("success");
    }


}