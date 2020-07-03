import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ClientServerCommunicaion.packets.NiceToAwesomePacket;

import java.io.*;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestReader implements Runnable {

    DatagramChannel channel;
    private static final Logger logger = LoggerFactory.getLogger(RequestReader.class);
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    DatabaseManager databaseManager;
    private SocketAddress address;
    private ByteBuffer buffer;
    private CommandProcessor processor;

    public RequestReader(DatagramChannel channel) {
        this.channel = channel;
    }

    public RequestReader(DatagramChannel channel, DatabaseManager databaseManager,
                         SocketAddress address, ByteBuffer buffer, CommandProcessor processor) {
        this.channel = channel;
        this.databaseManager = databaseManager;
        this.address = address;
        this.buffer = buffer;
        this.processor = processor;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public SocketAddress getAddress() {
        buffer = ByteBuffer.allocate(2048);
        buffer.clear();
        SocketAddress address = null;
        try {
            do {
                address = channel.receive(buffer);
            } while (address == null);
            logger.info("Запрос принят");
        } catch (IOException e) {
            logger.warn("Вашему вниманию представляется стектрейс {}", e.getMessage());
            System.exit(-1);
        }
        return address;
    }

    public NiceToAwesomePacket getNewPacket() {
        try {
            NiceToAwesomePacket packet = deserialize(buffer.array());
            logger.info("Объект десериализован");
            //packet.setSocketAddress(address);
            return packet;
        } catch (ClassNotFoundException e) {
            logger.warn("Класса "+ e.getCause() + " неееет");
        } catch (IOException e) {
            logger.warn("Вашему вниманию представляется стектрейс {}", e.getMessage());
            System.exit(-1);
        }
        return null;
    }

    public NiceToAwesomePacket deserialize(byte[] codedPacket) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(codedPacket);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayStream);
        return (NiceToAwesomePacket) inputStream.readObject();
    }

    @Override
    public void run() {
        NiceToAwesomePacket result = getNewPacket();
        result.setSocketAddress(address);
        Runnable task = new DoCommand(result, channel, processor);
        executorService.submit(task);
    }
}
