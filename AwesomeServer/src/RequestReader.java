import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class RequestReader {

    DatagramChannel channel;

    public RequestReader(DatagramChannel channel) {
        this.channel = channel;
    }

    public InPacket getNewPacket() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();
        try {
            SocketAddress address = channel.receive(buffer);
            InPacket packet = deserialize(buffer.array());
            packet.setSocketAddress(address);
            return packet;
        } catch (ClassNotFoundException e) {
            System.out.println("Класса неееет");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public InPacket deserialize(byte[] codedPacket) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(codedPacket);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayStream);
        return (InPacket) inputStream.readObject();
    }

}
