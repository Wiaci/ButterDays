import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ResponseAcceptor {

    DatagramSocket socket;

    public ResponseAcceptor(DatagramSocket socket) {
        this.socket = socket;
    }

    public AwesomeToNicePacket getResponsePacket() throws SocketTimeoutException {
        byte[] codedResponse = new byte[1024];
        DatagramPacket packetToReceive = new DatagramPacket(codedResponse, codedResponse.length);
        try {
            socket.receive(packetToReceive);
            return deserialize(codedResponse);
        } catch (SocketTimeoutException e) {
            throw new SocketTimeoutException();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Класса неееет");
        }
        /*ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.clear();*/

        /*try {
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
        }*/
        return null;
    }

    public AwesomeToNicePacket deserialize(byte[] codedPacket) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(codedPacket);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayStream);
        return (AwesomeToNicePacket) inputStream.readObject();
    }
}

class DisconnectedException extends Exception {};
