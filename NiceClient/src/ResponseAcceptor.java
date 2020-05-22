import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

public class ResponseAcceptor {

    private DatagramSocket socket;
    private int secondsOfTrying;

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
            if (secondsOfTrying == 0) System.out.print("_______________\r");
            if (secondsOfTrying < 5) {
                System.out.print("***");
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
            System.out.println("Класса"+ e.getCause() + "неееет");
        }
        return null;
    }

    private AwesomeToNicePacket deserialize(byte[] codedPacket) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(codedPacket);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayStream);
        return (AwesomeToNicePacket) inputStream.readObject();
    }
}