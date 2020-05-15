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

    public AwesomeToNicePacket getResponsePacket() throws DisconnectedException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();
        try {
            SocketAddress address;
            long timeOfTrying;
            long currentTime = System.currentTimeMillis();
            do {
                address = channel.receive(buffer);
                timeOfTrying = System.currentTimeMillis() - currentTime;
                //System.out.println(timeOfTrying);
                if (timeOfTrying > 5000L) throw new DisconnectedException();
            } while (address == null);
            return deserialize(buffer.array());
        } catch (ClassNotFoundException e) {
            System.out.println("Класса неееет");
        } catch (IOException e) {
            System.out.println("Я честно-честно-честно не знаю как вызвать этот эксэпшн");
        }
        return null;
    }

    public AwesomeToNicePacket deserialize(byte[] codedPacket) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(codedPacket);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayStream);
        return (AwesomeToNicePacket) inputStream.readObject();
    }
}

class DisconnectedException extends Exception {};
