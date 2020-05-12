import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class RequestCreator {

    DatagramChannel channel;
    UserMagicInteract user;

    public RequestCreator(DatagramChannel channel, UserMagicInteract user) {
        this.channel = channel;
        this.user = user;
    }

    public void sendResponse(NiceToAwesomePacket packet, SocketAddress address) {
        try {
            byte[] codedPacket = serialize(packet);
            ByteBuffer buffer = ByteBuffer.wrap(codedPacket);
            buffer.flip();
            channel.send(buffer, address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] serialize(NiceToAwesomePacket packet) throws IOException {
        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayStream);
        outputStream.writeObject(packet);
        return byteArrayStream.toByteArray();
    }

    public NiceToAwesomePacket createPacket(String[] command) throws CtrlDException {
        String firstWord = command[0];
        switch (firstWord) {
            case "info":
            case "show":
            case "clear":
            case "head":
            case "average_of_average_mark":
            case "print_field_ascending_semester_enum":
            case "remove_by_id":
            case "count_less_than_form_of_education":
                return new NiceToAwesomePacket(command);
            default: return new NiceToAwesomePacket(command, user.getStudyGroup());
        }
    }
}
