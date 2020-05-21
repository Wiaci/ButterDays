import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sourse.Person;
import sourse.StudyGroup;
import sourse.enums.FormOfEducation;

import javax.xml.bind.annotation.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class CommandProcessor {
    @XmlElement(name = "collection")
    private final LinkedList<StudyGroup> list = new LinkedList<>();
    @XmlElement
    private final Date dateOfInitialization = new Date();
    private static final Logger logger = LoggerFactory.getLogger(CommandProcessor.class);

    public CommandProcessor() {
        list.forEach(s -> StudyGroup.addId(s.getId()));
        list.forEach(s -> Person.addPassportId(s.getGroupAdmin().getPassportID()));
    }

    public AwesomeToNicePacket runCommand(NiceToAwesomePacket packet) {
        String command = packet.getCommand()[0];
        AwesomeToNicePacket nicePacket = null;
        switch (command) {
            case "check": nicePacket = new AwesomeToNicePacket("Done"); break;
            case "info": nicePacket = info(); break;
            case "add": nicePacket = add(packet.getStudyGroup()); break;
            case "update": nicePacket = update(packet.getCommand()[1], packet.getStudyGroup()); break;
            case "show": nicePacket = show(); break;
            case "remove_by_id": nicePacket = removeByID(packet.getCommand()[1]); break;
            case "clear": clear(); nicePacket = new AwesomeToNicePacket("clear Done"); break;
            case "head": nicePacket = head(); break;
            case "add_if_max": nicePacket = addIfMax(packet.getStudyGroup()); break;
            case "remove_greater": nicePacket = removeGreater(packet.getStudyGroup()); break;
            case "average_of_average_mark": nicePacket = averageOfAverageMark(); break;
            case "count_less_than_form_of_education": nicePacket = countLessAndSoOn(packet.getCommand()[1]); break;
            case "print_field_ascending_semester_enum": nicePacket = printFieldAndSoOn();
        }
        logger.info("Команда {} выполнена", command);
        return nicePacket;
    }

    public AwesomeToNicePacket info() {
        String info = list.size() + " " +
                dateOfInitialization.toString();
        return new AwesomeToNicePacket("info " + info);
    }

    public AwesomeToNicePacket show() {
        StringBuilder str = new StringBuilder();
        String s = list.stream()
                .sorted((o1, o2) -> {
                    if (o1.getName().compareTo(o2.getName()) > 0) return 1;
                    else if (o1.getName().compareTo(o2.getName()) < 0) return -1;
                    else return 0;
                })
                .map(StudyGroup::toString)
                .collect(Collectors.joining("\n"))
            ;
        return new AwesomeToNicePacket("show " + s);
    }

    public AwesomeToNicePacket add(StudyGroup group) {
        if (!Person.getPassportIDSet().contains(group.getGroupAdmin().getPassportID())) {
            group.setId(StudyGroup.generateRandomId());
            list.add(group);
            StudyGroup.getIdSet().add(group.getId());
            return new AwesomeToNicePacket("add Succeed");
        } else return new AwesomeToNicePacket("add Failed passport");
    }

    public AwesomeToNicePacket update(String id, StudyGroup group) {
        if (StudyGroup.getIdSet().contains(Long.parseLong(id))) {
            if (!Person.getPassportIDSet().stream()
                    .filter(x -> !x.equals(group.getGroupAdmin().getPassportID()))
                    .collect(Collectors.toSet()).contains(group.getGroupAdmin().getPassportID())) {
                removeByID(id);
                group.setId(Long.parseLong(id));
                list.add(group);
                StudyGroup.addId(Long.parseLong(id));
                return new AwesomeToNicePacket("update Succeed");
            } else return new AwesomeToNicePacket("update Failed passport");
        }
        return new AwesomeToNicePacket("update Failed id");
    }
    public AwesomeToNicePacket removeByID(String id) {
        if (StudyGroup.getIdSet().contains(Long.parseLong(id))) {
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
    public void clear() {
        list.clear();
        StudyGroup.clearIdList();
        Person.clearPassportIdList();
    }
    public AwesomeToNicePacket head() {
        if (list.size() != 0) {
            return new AwesomeToNicePacket(list.stream()
                    .findFirst()
                    .orElse(new StudyGroup())
                    .toString());
        } else return new AwesomeToNicePacket("head Nothing");
    }
    public AwesomeToNicePacket addIfMax(StudyGroup group) {
        if (list.stream().
                noneMatch(x -> x.compareTo(group) > 0)) {
            return add(group);
        } else return new AwesomeToNicePacket("add Failed notMax");
    }
    public AwesomeToNicePacket removeGreater(StudyGroup group) {
        int startSize = list.size();
        List<StudyGroup> greater = list.stream()
                .filter(x -> x.compareTo(group) > 0)
                .collect(Collectors.toList());
        greater.forEach(list::remove);
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

}
