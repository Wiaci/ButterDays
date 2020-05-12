import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ResponseSender {

    DatagramChannel channel;

    public ResponseSender(DatagramChannel channel) {
        this.channel = channel;
    }

    public void sendResponse(AwesomeToNicePacket packet, SocketAddress address) {
        try {
            byte[] codedPacket = serialize(packet);
            ByteBuffer buffer = ByteBuffer.wrap(codedPacket);
            buffer.clear();
            channel.send(buffer, address);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public byte[] serialize(AwesomeToNicePacket packet) throws IOException {
        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayStream);
        outputStream.writeObject(packet);
        outputStream.flush();
        return byteArrayStream.toByteArray();
    }
}
