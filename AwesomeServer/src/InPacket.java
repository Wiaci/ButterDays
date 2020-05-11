import sourse.StudyGroup;
import java.io.Serializable;
import java.net.SocketAddress;

public class InPacket implements Serializable {
    private String[] command;
    private StudyGroup studyGroup;
    private transient SocketAddress socketAddress;

    public String[] getCommand() {
        return command;
    }

    public void setCommand(String[] command) {
        this.command = command;
    }

    public StudyGroup getStudyGroup() {
        return studyGroup;
    }

    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }
}
