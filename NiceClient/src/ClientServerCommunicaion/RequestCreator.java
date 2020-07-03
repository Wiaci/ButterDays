package ClientServerCommunicaion;

import ClientServerCommunicaion.packets.NiceToAwesomePacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RequestCreator {

    InetAddress address;
    PacketMaker user;

    public RequestCreator(InetAddress address, PacketMaker user) {
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


}
