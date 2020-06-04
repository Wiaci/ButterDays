import packets.NiceToAwesomePacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RequestCreator {

    InetAddress address;
    UserMagicInteract user;

    public RequestCreator(InetAddress address, UserMagicInteract user) {
        this.address = address;
        this.user = user;
    }

    public void sendResponse(NiceToAwesomePacket packet, DatagramSocket socket, InetAddress address, int port) {
        try {
            byte[] codedPacket = serialize(packet);
            DatagramPacket packetToSend =
                    new DatagramPacket(codedPacket, codedPacket.length, address, port);
            socket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] serialize(NiceToAwesomePacket packet) throws IOException {
        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayStream);
        outputStream.writeObject(packet);
        outputStream.flush();
        return byteArrayStream.toByteArray();
    }

    public NiceToAwesomePacket createPacket(String[] command, String login, String password) throws CtrlDException {
        String firstWord = command[0];
        switch (firstWord) {
            case "authorize":
            case "register":
            case "check":
            case "info":
            case "show":
            case "clear":
            case "head":
            case "average_of_average_mark":
            case "print_field_ascending_semester_enum":
            case "remove_by_id":
            case "count_less_than_form_of_education":
                return new NiceToAwesomePacket(command, login, password);
            default: return new NiceToAwesomePacket(command, user.getStudyGroup(), login, password);
        }
    }
}
