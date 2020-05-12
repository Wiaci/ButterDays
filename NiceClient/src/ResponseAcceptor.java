import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ResponseAcceptor {

    DatagramChannel channel;

    public ResponseAcceptor(DatagramChannel channel) {
        this.channel = channel;
    }

    public AwesomeToNicePacket getResponsePacket() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();
        try {
            SocketAddress address = channel.receive(buffer);
            return deserialize(buffer.array());
        } catch (ClassNotFoundException e) {
            System.out.println("Класса неееет");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AwesomeToNicePacket deserialize(byte[] codedPacket) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(codedPacket);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayStream);
        return (AwesomeToNicePacket) inputStream.readObject();
    }
}
