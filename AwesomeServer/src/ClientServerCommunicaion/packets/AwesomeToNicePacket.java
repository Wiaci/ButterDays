package ClientServerCommunicaion.packets;

import java.io.Serializable;
public class AwesomeToNicePacket implements Serializable {

    private String response;

    public AwesomeToNicePacket(String response) {
        this.response = response;
    }

    public AwesomeToNicePacket() {}

    public void setValue(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}