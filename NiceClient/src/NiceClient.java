import packets.AwesomeToNicePacket;
import packets.NiceToAwesomePacket;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.LinkedList;

public class NiceClient {

    private UserMagicInteract user;
    private ResponseAcceptor responseAcceptor;
    private RequestCreator requestCreator;
    private final DatagramSocket socket;
    private static final String SERVER_ADDRESS = "localhost";
    private InetAddress serverAddress;
    private static final int PORT = 8000;
    private int connectionTries;
    private String[] runningCommand;

    private String login;
    private String password;

    public NiceClient() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        serverAddress = InetAddress.getByName(SERVER_ADDRESS);
    }

    public void run() throws IOException, CtrlDException {
        socket.setSoTimeout(5000);
        user = new UserMagicInteract(
                new BufferedReader(
                        new InputStreamReader(System.in)), false);
        responseAcceptor = new ResponseAcceptor(socket);
        requestCreator = new RequestCreator(serverAddress, user);

        try {
            if (!checkConnection()) {
                System.out.println("Сервер недоступен");
                if (++connectionTries == 7) {
                    System.out.println("Время ожидания истекло. Штраф 1000 рублей");
                    return;
                }
                throw new SocketTimeoutException();
            }
            connectionTries = 0;
            if (runningCommand == null) user.printHello();
            else launchCommand(runningCommand);
            boolean isInterrupted = false;
            while (!isInterrupted) {
                String[] newCommand = user.getNewCommand();
                runningCommand = newCommand;
                isInterrupted = launchCommand(newCommand);
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Переподключение...");
            run();
        }

    }

    public boolean checkConnection() throws CtrlDException {
        NiceToAwesomePacket checkPacket = requestCreator.createPacket(new String[] {"check"}, login, password);
        requestCreator.sendResponse(checkPacket, socket, serverAddress, PORT);
        try {
            responseAcceptor.getResponsePacket();
            if (login == null) authorize();
        } catch (SocketTimeoutException e) {
            return false;
        }
        return true;
    }

    public boolean launchCommand(String[] newCommand) throws CtrlDException, SocketTimeoutException {
        if (newCommand[0].equals("")) return false;
        if (newCommand[0].equals("exit")) return true;
        if (newCommand[0].equals("help")) {
            user.help();
            return false;
        }
        if (newCommand[0].equals("execute_script") && newCommand.length > 1) {
            executeScript(newCommand[1]);
            return false;
        }
        if (!user.check(newCommand)) {
            System.out.println("Введи help и прозрей!");
            return false;
        }
        NiceToAwesomePacket packetRequest = requestCreator.createPacket(newCommand, login, password);
        requestCreator.sendResponse(packetRequest, socket, serverAddress, PORT);

        AwesomeToNicePacket packetResponse = responseAcceptor.getResponsePacket();
        user.printResponse(packetResponse);

        return false;
    }

    LinkedList<String> listOfScripts = new LinkedList<>();
    HashMap<String, String> problemFiles = new HashMap<>(); // мап с проблемными файлами и их вызовами
    /**
     * Метод для выполнения скрипта
     * @param filename имя файла содержащего скрипт
     */
    private void executeScript(String filename) throws CtrlDException{
        try {
            System.out.println("'"+filename+"'");
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            listOfScripts.add(filename);
            String newLine;
            user = new UserMagicInteract(reader, true);
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
            user = new UserMagicInteract(new BufferedReader(new InputStreamReader(System.in)), false);
        }
    }
    private void authorize() throws CtrlDException, SocketTimeoutException {
        if (user.wantToRegister()) {
            register();
            return;
        };
        while (true) {
            String[] data = user.getLoginAndPassword();
            login = data[0];
            password = data[1];
            NiceToAwesomePacket packet = requestCreator.createPacket(new String[]{"authorize"}, login, password);
            requestCreator.sendResponse(packet, socket, serverAddress, PORT);
            AwesomeToNicePacket nicePacket = responseAcceptor.getResponsePacket();
            if (!nicePacket.getResponse().equals("Success")) {
                System.out.print("Безуспешно... ");
                if (user.wantToRegister()) break;
            } else {
                System.out.println("О счастье, вы есть в базе!");
                return;
            }
        }
        register();
    }

    private void register() throws CtrlDException, SocketTimeoutException {
        String serverResponse;
        do {
            String email = user.getEMail();
            NiceToAwesomePacket packet = requestCreator.createPacket(new String[]{"register"}, email, "");
            requestCreator.sendResponse(packet, socket, serverAddress, PORT);
            AwesomeToNicePacket nicePacket = responseAcceptor.getResponsePacket();
            serverResponse = nicePacket.getResponse();
            if (serverResponse.equals("success"))
                System.out.println("Вы успешно зарегистрировались. На вашу почту был выслан пароль");
            if (serverResponse.equals("inBase"))
                System.out.println("Такой пользователь уже зарегистрирован");
            authorize();
        } while (!serverResponse.equals("success"));
    }
}