import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

public class RequestReader {

    DatagramChannel channel;

    public RequestReader(DatagramChannel channel) {
        this.channel = channel;
    }

    public NiceToAwesomePacket getNewPacket() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();
        try {
            SocketAddress address;
            do {
                address = channel.receive(buffer);
            } while (address == null);
            System.out.println(Arrays.toString(buffer.array()));
            NiceToAwesomePacket packet = deserialize(buffer.array());
            packet.setSocketAddress(address);
            return packet;
        } catch (ClassNotFoundException e) {
            System.out.println("Класса неееет");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public NiceToAwesomePacket deserialize(byte[] codedPacket) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(codedPacket);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayStream);
        return (NiceToAwesomePacket) inputStream.readObject();
    }

}
