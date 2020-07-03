package ClientServerCommunicaion;

import ClientServerCommunicaion.packets.NiceToAwesomePacket;
import ClientServerCommunicaion.sourse.StudyGroup;

public class PacketMaker {

    public NiceToAwesomePacket createPacket(String[] command, String login, String password) {
        return new NiceToAwesomePacket(command, login, password);
    }

    public NiceToAwesomePacket createPacket(String[] command, StudyGroup group, String login, String password) {
        return new NiceToAwesomePacket(command, group, login, password);
    }

    /*public NiceToAwesomePacket createPacket(String[] command, String login, String password) {
        String firstWord = command[0];
        switch (firstWord) {
            case "authorize":
            case "mailRegister":
            case "commonRegister":
            case "check":
            case "info":
            case "show":
            case "clear":
            case "head":
            case "average_of_average_mark":
            case "print_field_ascending_semester_enum":
            case "remove_by_id":
            case "count_less_than_form_of_education":
                return new NiceToAwesomePacket(command, login, password);
            default: return new NiceToAwesomePacket(command, user.getStudyGroup(), login, password);
        }
    }*/
}

