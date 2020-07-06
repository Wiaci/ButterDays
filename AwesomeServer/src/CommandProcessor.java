import MailThings.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ClientServerCommunicaion.packets.AwesomeToNicePacket;
import ClientServerCommunicaion.packets.NiceToAwesomePacket;
import ClientServerCommunicaion.sourse.*;
import ClientServerCommunicaion.sourse.enums.*;

import javax.mail.MessagingException;
import java.nio.channels.DatagramChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class CommandProcessor {

    private LinkedList<StudyGroup> list;
    private static final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);
    private DatabaseManager database;

    private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public CommandProcessor(DatabaseManager database) {
        try {
            this.database = database;
            list = database.load();
            list.forEach(s -> StudyGroup.addId(s.getId()));
            list.forEach(s -> Person.addPassportId(s.getGroupAdmin().getPassportID()));
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn("Беды с базой");
            System.exit(-100500);
        }

    }

    public AwesomeToNicePacket runCommand(NiceToAwesomePacket packet) {
        try {
            String command = packet.getCommand()[0];
            logger.info(command);
            if (command.equals("getList") || command.equals("mailRegister") || command.equals("commonRegister") ||
                    command.equals("check") ||
                    database.checkUser(packet.getLogin(), doSomeHash(packet.getPassword()))) {
                AwesomeToNicePacket nicePacket = null;
                switch (command) {
                    case "getList": nicePacket = getList(); break;
                    case "authorize": nicePacket = new AwesomeToNicePacket("Success"); break;
                    case "check": nicePacket = new AwesomeToNicePacket("Done"); break;
                    case "mailRegister": nicePacket = mailRegister(packet.getLogin()); break;
                    case "commonRegister": nicePacket = commonRegister(packet.getLogin(), packet.getPassword()); break;
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
                    case "print_field_ascending_semester_enum": nicePacket = printFieldAndSoOn(); break;
                }
                logger.info("Команда {} выполнена", command);
                return nicePacket;
            }
            return new AwesomeToNicePacket("");
        } catch (SQLException e) {
            logger.warn("Оказия с базой данных!");
            e.printStackTrace();
            return new AwesomeToNicePacket("butterDays problem");
        }
    }

    public AwesomeToNicePacket getList() {
        readWriteLock.readLock().lock();
        AwesomeToNicePacket packet = new AwesomeToNicePacket(list.stream()
                .map(s -> {
                    try {
                        return s.anotherToString(database.getOwner(s.getId()));
                    } catch (SQLException e) {
                        logger.warn("Оказия с базой данных!");
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.joining("\n")));
        readWriteLock.readLock().unlock();
        System.out.println(packet.getResponse());
        return packet;
    }

    public AwesomeToNicePacket commonRegister(String login, String password) throws SQLException {
        if (database.checkLogin(login)) return new AwesomeToNicePacket("inBase");
        database.registerUser(login, doSomeHash(password));
        return new AwesomeToNicePacket("success");
    }

    public AwesomeToNicePacket mailRegister(String email) throws SQLException {
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
            e.printStackTrace();
            return new AwesomeToNicePacket("wrong");
        }
    }

    public AwesomeToNicePacket info() {
        readWriteLock.readLock().lock();
        int size = list.size();
        readWriteLock.readLock().unlock();
        return new AwesomeToNicePacket("info " + size);
    }

    public AwesomeToNicePacket show() {
        readWriteLock.readLock().lock();
        String s = list.stream()
                .sorted(Comparator.comparing(StudyGroup::getName))
                .map(StudyGroup::toString)
                .collect(Collectors.joining("\n"));
        readWriteLock.readLock().unlock();
        return new AwesomeToNicePacket("show " + s);
    }

    public AwesomeToNicePacket add(StudyGroup group, String login) throws SQLException {
        System.out.println(group);
        if (database.isPassportInBase(group.getGroupAdmin().getPassportID())) return
                new AwesomeToNicePacket("failed");
        database.addGroup(group, login);
        readWriteLock.writeLock().lock();
        list.add(group);
        StudyGroup.getIdSet().add(group.getId());
        readWriteLock.writeLock().unlock();
        return new AwesomeToNicePacket(group.getId().toString());
    }

    public AwesomeToNicePacket update(String id, StudyGroup group, String login) throws SQLException {
        if (!database.isInBase(Long.parseLong(id))) return new AwesomeToNicePacket("update Failed id");
        if (database.isPassportInBase(group.getGroupAdmin().getPassportID(), Long.parseLong(id))) return
                new AwesomeToNicePacket("update Failed passport");
        if (!database.checkAccess(Long.parseLong(id), login)) return new AwesomeToNicePacket("update no access");
        readWriteLock.writeLock().lock();
        database.update(Integer.parseInt(id), group);
        list = database.load();
        StudyGroup.clearIdList();
        Person.clearPassportIdList();
        list.forEach(s -> StudyGroup.addId(s.getId()));
        list.forEach(s -> Person.addPassportId(s.getGroupAdmin().getPassportID()));
        readWriteLock.writeLock().unlock();
        return new AwesomeToNicePacket("update Succeed");
    }

    public AwesomeToNicePacket removeByID(String id, String login) throws SQLException {
        if (!database.isInBase(Long.parseLong(id))) return new AwesomeToNicePacket("remove_by_id Failed");
        if (!database.checkAccess(Long.parseLong(id), login)) return new AwesomeToNicePacket("remove_by_id no access");
        readWriteLock.writeLock().lock();
        if (StudyGroup.getIdSet().contains(Long.parseLong(id))) {
            database.removeGroup(Long.parseLong(id));
            StudyGroup a = list.stream()
                    .filter(x -> x.getId() == Long.parseLong(id))
                    .findFirst()
                    .orElse(new StudyGroup());
            list.remove(a);
            StudyGroup.getIdSet().remove(a.getId());
            Person.getPassportIDSet().remove(a.getGroupAdmin().getPassportID());
            readWriteLock.writeLock().unlock();
            return new AwesomeToNicePacket("remove_by_id Succeed");
        } else {
            readWriteLock.writeLock().unlock();
            return new AwesomeToNicePacket("remove_by_id Failed");
        }
    }

    public void clear(String login) throws SQLException {
        readWriteLock.writeLock().lock();
        database.clear(login);
        list = database.load();
        StudyGroup.clearIdList();
        Person.clearPassportIdList();
        list.forEach(s -> StudyGroup.addId(s.getId()));
        list.forEach(s -> Person.addPassportId(s.getGroupAdmin().getPassportID()));
        readWriteLock.writeLock().unlock();
    }
    public AwesomeToNicePacket head() {
        readWriteLock.readLock().lock();
        if (list.size() != 0) {
            AwesomeToNicePacket packet = new AwesomeToNicePacket("head " + list.stream()
                    .findFirst()
                    .orElse(new StudyGroup())
                    .toString());
            readWriteLock.readLock().unlock();
            return packet;
        } else {
            readWriteLock.readLock().unlock();
            return new AwesomeToNicePacket("head Nothing");
        }
    }
    public AwesomeToNicePacket addIfMax(StudyGroup group, String login) throws SQLException {
        readWriteLock.writeLock().lock();
        if (list.stream().
                noneMatch(x -> x.compareTo(group) > 0)) {
            readWriteLock.writeLock().lock();
            database.addGroup(group, login);
            AwesomeToNicePacket packet = add(group, login);
            readWriteLock.writeLock().unlock();
            return packet;
        } else {
            readWriteLock.writeLock().unlock();
            return new AwesomeToNicePacket("notMax");
        }
    }
    public AwesomeToNicePacket removeGreater(StudyGroup group, String login) throws SQLException {
        readWriteLock.writeLock().lock();
        int startSize = list.size();
        database.removeGreater(group, login);
        list = database.load();
        StudyGroup.clearIdList();
        Person.clearPassportIdList();
        list.forEach(s -> StudyGroup.addId(s.getId()));
        list.forEach(s -> Person.addPassportId(s.getGroupAdmin().getPassportID()));
        readWriteLock.writeLock().unlock();
        return new AwesomeToNicePacket("remove_greater " + (startSize - list.size()));
    }
    public AwesomeToNicePacket averageOfAverageMark() {
        readWriteLock.readLock().lock();
        float average = (float) list.stream()
                .mapToDouble(StudyGroup::getAverageMark)
                .average()
                .orElse(0);
        readWriteLock.readLock().unlock();
        return new AwesomeToNicePacket("average_of_average_mark " + average);
    }
    public AwesomeToNicePacket countLessAndSoOn(String formOfEducation) {
       readWriteLock.readLock().lock();
       long count = list.stream()
               .filter(x -> x.getFormOfEducation() != null)
               .filter(x -> x
                       .getFormOfEducation()
                       .compareTo(FormOfEducation.valueOf(formOfEducation)) < 0)
               .count();
       readWriteLock.readLock().unlock();
       return new AwesomeToNicePacket("count_less_than_form_of_education " + count);
    }

    public AwesomeToNicePacket printFieldAndSoOn() {
        readWriteLock.readLock().lock();
        String s = list.stream()
                .filter(x -> x.getSemesterEnum() != null)
                .map(StudyGroup::getSemesterEnum)
                .sorted()
                .map(Enum::toString)
                .collect(Collectors.joining("\n"));
        readWriteLock.readLock().unlock();
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
            System.out.println("Отправьте балбесу-программисту это сообщение:");
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }


}

class DoCommand implements Runnable {
    private NiceToAwesomePacket packet;
    private DatagramChannel channel;
    private static ForkJoinPool forkJoinPool = new ForkJoinPool();
    private CommandProcessor commandProcessor;

    public DoCommand(NiceToAwesomePacket packet, DatagramChannel channel, CommandProcessor commandProcessor) {
        this.packet = packet;
        this.channel = channel;
        this.commandProcessor = commandProcessor;
    }

    @Override
    public void run() {
        AwesomeToNicePacket result = commandProcessor.runCommand(packet);
        RecursiveAction task = new ResponseSender(channel, result, packet.getSocketAddress());
        forkJoinPool.invoke(task);
    }
}