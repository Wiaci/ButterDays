import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ClientServerCommunicaion.packets.AwesomeToNicePacket;

import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.RecursiveAction;

public class ResponseSender extends RecursiveAction {

    DatagramChannel channel;
    private static final Logger logger = LoggerFactory.getLogger(ResponseSender.class);
    private SocketAddress address;
    private AwesomeToNicePacket packet;

    public ResponseSender(DatagramChannel channel, AwesomeToNicePacket packet, SocketAddress address) {
        this.address = address;
        this.channel = channel;
        this.packet = packet;
    }

    public void sendResponse(AwesomeToNicePacket packet, SocketAddress address) {
        try {
            byte[] codedPacket = serialize(packet);
            logger.info("Ответ сериализован");
            ByteBuffer buffer = ByteBuffer.wrap(codedPacket);
            buffer.clear();
            channel.send(buffer, address);
            logger.info("Ответ отправлен");
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

    @Override
    protected void compute() {
        sendResponse(packet, address);
    }
}
