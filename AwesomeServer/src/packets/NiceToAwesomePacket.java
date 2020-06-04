package packets;

import sourse.StudyGroup;
import java.io.Serializable;
import java.net.SocketAddress;

public class NiceToAwesomePacket implements Serializable {
    private String[] command;
    private StudyGroup studyGroup;
    private String login;
    private String password;
    private transient SocketAddress socketAddress;

    public NiceToAwesomePacket(String[] command, String login, String password) {
        this.command = command;
        this.login = login;
        this.password = password;
    }

    public NiceToAwesomePacket(String[] command, StudyGroup studyGroup, String login, String password) {
        this.command = command;
        this.studyGroup = studyGroup;
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

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
