import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import packets.AwesomeToNicePacket;

import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ResponseSender {

    DatagramChannel channel;
    private static final Logger logger = LoggerFactory.getLogger(ResponseSender.class);

    public ResponseSender(DatagramChannel channel) {
        this.channel = channel;
    }

    public void sendResponse(AwesomeToNicePacket packet, SocketAddress address) {
        try {
            byte[] codedPacket = serialize(packet);
            logger.info("Ответ сериализован");
            ByteBuffer buffer = ByteBuffer.wrap(codedPacket);
            buffer.clear();
            channel.send(buffer, address);
        } catch (IOException e) {
            logger.warn("Вашему вниманию представляется стектрейс {}", e.getMessage());
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
