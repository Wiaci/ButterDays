import MailThings.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import packets.AwesomeToNicePacket;
import packets.NiceToAwesomePacket;
import sourse.*;
import sourse.enums.*;

import javax.mail.MessagingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


public class CommandProcessor {
    private LinkedList<StudyGroup> list;
    private static final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);
    private DatabaseManager database;

    public CommandProcessor(DatabaseManager database) throws SQLException {
        this.database = database;
        list = database.load();
        list.forEach(s -> StudyGroup.addId(s.getId()));
        list.forEach(s -> Person.addPassportId(s.getGroupAdmin().getPassportID()));
    }

    public AwesomeToNicePacket runCommand(NiceToAwesomePacket packet) {
        try {
            String command = packet.getCommand()[0];
            if (command.equals("register") || command.equals("check") ||
                    database.checkUser(packet.getLogin(), doSomeHash(packet.getPassword()))) {
                AwesomeToNicePacket nicePacket = null;
                switch (command) {
                    case "authorize": nicePacket = new AwesomeToNicePacket("Success"); break;
                    case "check": nicePacket = new AwesomeToNicePacket("Done"); break;
                    case "register": nicePacket = register(packet.getLogin()); break;
                    case "info": nicePacket = info(); break;
                    case "add": nicePacket = add(packet.getStudyGroup(), packet.getLogin()); break;
                    case "update": nicePacket = update(packet.getCommand()[1], packet.getStudyGroup(), packet.getLogin()); break;
                    case "show": nicePacket = show(); break;
                    case "remove_by_id": nicePacket = removeByID(packet.getCommand()[1], packet.getLogin()); break;
                    case "clear": clear(packet.getLogin()); nicePacket = new AwesomeToNicePacket("clear Done"); break;
                    case "head": nicePacket = head(); break;
                    case "add_if_max": nicePacket = addIfMax(packet.getStudyGroup(), packet.getLogin()); break;
                    case "remove_greater": nicePacket = removeGreater(packet.getStudyGroup(), packet.getLogin()); break;
                    case "average_of_average_mark": nicePacket = averageOfAverageMark(); break;
                    case "count_less_than_form_of_education": nicePacket = countLessAndSoOn(packet.getCommand()[1]); break;
                    case "print_field_ascending_semester_enum": nicePacket = printFieldAndSoOn();
                }
                logger.info("Команда {} выполнена", command);
                return nicePacket;
            }
            return new AwesomeToNicePacket("");
        } catch (SQLException e) {
            logger.warn("Оказия с базой данных!");
            return new AwesomeToNicePacket("butterDays problem");
        }
    }

    public AwesomeToNicePacket register(String email) throws SQLException {
        try {
            int passLength = (int) (Math.random() * 10 + 6);
            StringBuilder passBuilder = new StringBuilder();
            for (int i = 0; i < passLength; i++) {
                int randomSymbolCode = (int) (Math.random() * 62 + 48);
                if (randomSymbolCode > 57) randomSymbolCode += 7;
                if (randomSymbolCode > 90) randomSymbolCode += 6;
                passBuilder.append((char) randomSymbolCode);
            }
            if (database.checkLogin(email)) return new AwesomeToNicePacket("inBase");
            String password = passBuilder.toString();
            MailSender.send(email, password);
            database.registerUser(email, doSomeHash(password));
            return new AwesomeToNicePacket("success");
        } catch (MessagingException e) {
            return new AwesomeToNicePacket("wrong");
        }
    }

    public AwesomeToNicePacket info() {
        return new AwesomeToNicePacket("info " + list.size());
    }

    public AwesomeToNicePacket show() {
        String s = list.stream()
                .sorted(Comparator.comparing(StudyGroup::getName))
                .map(StudyGroup::toString)
                .collect(Collectors.joining("\n"));
        return new AwesomeToNicePacket("show " + s);
    }

    public AwesomeToNicePacket add(StudyGroup group, String login) throws SQLException {
        if (!Person.getPassportIDSet().contains(group.getGroupAdmin().getPassportID())) {
            database.addGroup(group, login);
            list.add(group);
            StudyGroup.getIdSet().add(group.getId());
            return new AwesomeToNicePacket("add Succeed");
        } else return new AwesomeToNicePacket("add Failed passport");
    }

    public AwesomeToNicePacket update(String id, StudyGroup group, String login) throws SQLException {
        if (!database.checkAccess(Long.parseLong(id), login)) return new AwesomeToNicePacket("update no access");
        if (!StudyGroup.getIdSet().contains(Long.parseLong(id))) return new AwesomeToNicePacket("update Failed id");
        if (list.stream()
                .anyMatch(x -> x.getId() != Long.parseLong(id) && x.getGroupAdmin().getPassportID() != null
                 && x.getGroupAdmin().getPassportID().equals(group.getGroupAdmin().getPassportID()))) {
            return new AwesomeToNicePacket("update Failed passport");
        }

        database.update(Integer.parseInt(id), group);
        list = database.load();
        StudyGroup.clearIdList();
        Person.clearPassportIdList();
        list.forEach(s -> StudyGroup.addId(s.getId()));
        list.forEach(s -> Person.addPassportId(s.getGroupAdmin().getPassportID()));
        return new AwesomeToNicePacket("update Succeed");
    }

    public AwesomeToNicePacket removeByID(String id, String login) throws SQLException {
        if (!database.checkAccess(Long.parseLong(id), login)) return new AwesomeToNicePacket("remove_by_id no access");
        if (StudyGroup.getIdSet().contains(Long.parseLong(id))) {
            database.removeGroup(Long.parseLong(id));
            StudyGroup a = list.stream()
                    .filter(x -> x.getId() == Long.parseLong(id))
                    .findFirst()
                    .orElse(new StudyGroup());
            list.remove(a);
            StudyGroup.getIdSet().remove(a.getId());
            Person.getPassportIDSet().remove(a.getGroupAdmin().getPassportID());
            return new AwesomeToNicePacket("remove_by_id Succeed");
        } else return new AwesomeToNicePacket("remove_by_id Failed");
    }
    public void clear(String login) throws SQLException {
        database.clear(login);
        list = database.load();
        StudyGroup.clearIdList();
        Person.clearPassportIdList();
        list.forEach(s -> StudyGroup.addId(s.getId()));
        list.forEach(s -> Person.addPassportId(s.getGroupAdmin().getPassportID()));
    }
    public AwesomeToNicePacket head() {
        if (list.size() != 0) {
            return new AwesomeToNicePacket(list.stream()
                    .findFirst()
                    .orElse(new StudyGroup())
                    .toString());
        } else return new AwesomeToNicePacket("head Nothing");
    }
    public AwesomeToNicePacket addIfMax(StudyGroup group, String login) throws SQLException {
        if (list.stream().
                noneMatch(x -> x.compareTo(group) > 0)) {
            database.addGroup(group, login);
            return add(group, login);
        } else return new AwesomeToNicePacket("add Failed notMax");
    }
    public AwesomeToNicePacket removeGreater(StudyGroup group, String login) throws SQLException {
        int startSize = list.size();
        List<StudyGroup> greater = list.stream()
                .filter(x -> x.compareTo(group) > 0)
                .collect(Collectors.toList());
        greater.forEach(list::remove);
        database.removeGreater(group, login);
        return new AwesomeToNicePacket("remove_greater " + (startSize - list.size()));
    }
    public AwesomeToNicePacket averageOfAverageMark() {
        float average = (float) list.stream()
                .mapToDouble(StudyGroup::getAverageMark)
                .average()
                .orElse(0);
        return new AwesomeToNicePacket("average_of_average_mark " + average);
    }
    public AwesomeToNicePacket countLessAndSoOn(String formOfEducation) {
        return new AwesomeToNicePacket("count_less_than_form_of_education " + (list.stream()
                .filter(x -> x.getFormOfEducation() != null)
                .filter(x -> x
                        .getFormOfEducation()
                        .compareTo(FormOfEducation.valueOf(formOfEducation)) < 0)
                .count()));
    }
    public AwesomeToNicePacket printFieldAndSoOn() {
        String s = list.stream()
                .map(StudyGroup::getSemesterEnum)
                .sorted()
                .map(Enum::toString)
                .collect(Collectors.joining("\n"));
        return new AwesomeToNicePacket("print_field_ascending_semester_enum " + s);
    }

    private String doSomeHash(String rawPass) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(rawPass.getBytes());
            byte[] digestBytes = messageDigest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte digestByte : digestBytes) {
                String s = Integer.toHexString(0xff & digestByte);
                s = (s.length() == 1) ? "0" + s : s;
                hexString.append(s);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Отправьте своему балбесу-программисту это сообщение:");
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
}
