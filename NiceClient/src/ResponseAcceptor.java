import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ResponseAcceptor {

    private DatagramSocket socket;
    private int secondsOfTrying;
    private String symbol;

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
            if (secondsOfTrying < 5) {
                System.out.print("");
                secondsOfTrying++;
                getResponsePacket();
            } else {
                System.out.print("\r");
                secondsOfTrying = 0;
                throw new SocketTimeoutException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Класса неееет");
        }
        return null;
    }

    public AwesomeToNicePacket deserialize(byte[] codedPacket) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(codedPacket);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayStream);
        return (AwesomeToNicePacket) inputStream.readObject();
    }
}