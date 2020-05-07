import sourse.StudyGroup;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.LinkedList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class CommandProcessor {
    @XmlElement(name = "collection")
    private final LinkedList<StudyGroup> list = new LinkedList<>();
    @XmlElement
    private final Date dateOfInitialization = new Date();


    /*public OutPacket<?> runCommand(InPacket packet) {
        String command = packet.getCommand()[0];
        switch (command) {
            case "info": info(); break;
        }
    }*/

    public void info() {}
    public void show() {}
    public void add() {}
    public void update() {}
    public void removeByID() {}
    public void clear() {}
    public void execute_script() {}
    public void head() {}
    public void addIfMax() {}
    public void removeGreater() {}

}
