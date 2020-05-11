import java.io.Serializable;

public class OutPacket implements Serializable {

    String response;

    public OutPacket(String response) {
        this.response = response;
    }

    public OutPacket() {}

    public void setValue(String response) {
        this.response = response;
    }
}
