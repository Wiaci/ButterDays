import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ResponseSender {

    DatagramChannel channel;

    public ResponseSender(DatagramChannel channel) {
        this.channel = channel;
    }

    public void sendResponse(OutPacket<?> packet, SocketAddress address) {
        try {
            byte[] codedPacket = serialize(packet);
            ByteBuffer buffer = ByteBuffer.wrap(codedPacket);
            buffer.flip();
            channel.send(buffer, address);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("А такого класса нет");

        }


    }

    public byte[] serialize(OutPacket<?> packet) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayStream);
        outputStream.writeObject(packet);
        return byteArrayStream.toByteArray();
    }
}
