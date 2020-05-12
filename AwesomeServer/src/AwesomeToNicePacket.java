import java.io.Serializable;

public class AwesomeToNicePacket implements Serializable {

    String response;

    public AwesomeToNicePacket(String response) {
        this.response = response;
    }

    public AwesomeToNicePacket() {}

    public void setValue(String response) {
        this.response = response;
    }
}
