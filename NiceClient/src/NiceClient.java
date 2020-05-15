import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.LinkedList;

public class NiceClient {

    UserMagicInteract user;
    ResponseAcceptor responseAcceptor;
    RequestCreator requestCreator;
    InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8000);
    int connectionTries = 0;

    public void run() throws IOException, CtrlDException {
        try (DatagramChannel clientChannel = DatagramChannel.open()) {
            clientChannel.configureBlocking(false);
            user = new UserMagicInteract(
                    new BufferedReader(
                            new InputStreamReader(System.in)), false);
            responseAcceptor = new ResponseAcceptor(clientChannel);
            requestCreator = new RequestCreator(clientChannel, user);
            try {
                if (!checkConnection()) {
                    System.out.println("Сервер недоступен");
                    if (++connectionTries == 7) {
                        System.out.println("Время ожидания истекло. Штраф 1000 рублей");
                        return;
                    }
                    throw new DisconnectedException();
                }
                connectionTries = 0;
                user.printHello();
                boolean isInterrupted = false;
                while (!isInterrupted) {
                    String[] newCommand = user.getNewCommand();
                    isInterrupted = launchCommand(newCommand);
                }
            } catch (DisconnectedException e) {
                System.out.println("Переподключение...");
                run();
            }

        }
    }

    public boolean checkConnection() throws CtrlDException {
        NiceToAwesomePacket checkPacket = requestCreator.createPacket(new String[] {"check"});
        requestCreator.sendResponse(checkPacket, serverAddress);
        try {
            AwesomeToNicePacket responsePacket = responseAcceptor.getResponsePacket();
        } catch (DisconnectedException e) {
            return false;
        }
        return true;
    }

    public boolean launchCommand(String[] newCommand) throws CtrlDException, DisconnectedException {
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
        NiceToAwesomePacket packet = requestCreator.createPacket(newCommand);
        requestCreator.sendResponse(packet, serverAddress);

        AwesomeToNicePacket packet1 = responseAcceptor.getResponsePacket();
        user.printResponse(packet1);

        return false;
    }

    LinkedList<String> listOfScripts = new LinkedList<>();
    HashMap<String, String> problemFiles = new HashMap<>(); // мап с проблемными файлами и их вызовами
    /**
     * Метод для выполнения скрипта
     * @param filename имя файла содержащего скрипт
     */
    private void executeScript(String filename) throws CtrlDException, DisconnectedException {
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
                /*System.out.println(listOfScripts);*/
            } while (true);
        } catch (FileNotFoundException e) {
            System.out.println("Такого файла нет");
        } catch (IOException e) {
            System.out.println("Я не знаю как вызвать это исключение");
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
}
//