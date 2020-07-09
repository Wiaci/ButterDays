package ClientServerCommunicaion.packets;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;

public class AwesomeToNicePacket implements Serializable {

    private static final long serialVersionUID = 7897652918503193126L;
    private String response;
    private HashMap<String, Color> colorMap;

    public AwesomeToNicePacket(String response) {
        this.response = response;
    }

    public void setColorMap(HashMap<String, Color> colorMap) {
        this.colorMap = colorMap;
    }

    public HashMap<String, Color> getColorMap() {
        return colorMap;
    }

    public void setValue(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}