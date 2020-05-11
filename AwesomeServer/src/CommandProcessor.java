import sourse.Person;
import sourse.StudyGroup;
import sourse.enums.FormOfEducation;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class CommandProcessor {
    @XmlElement(name = "collection")
    private final LinkedList<StudyGroup> list = new LinkedList<>();
    @XmlElement
    private final Date dateOfInitialization = new Date();

    public CommandProcessor() {
        list.forEach(s -> StudyGroup.addId(s.getId()));
        list.forEach(s -> Person.addPassportId(s.getGroupAdmin().getPassportID()));
    }

    public OutPacket runCommand(InPacket packet) {
        String command = packet.getCommand()[0];
        switch (command) {
            case "info": return info();
            case "add": return add(packet.getStudyGroup());
            case "update": return update(packet.getCommand()[1], packet.getStudyGroup());
            case "show": return show();
            case "remove_by_id": return removeByID(packet.getCommand()[1]);
            case "clear": clear(); return new OutPacket("Done");
            case "head": return head();
            case "add_if_max": return addIfMax(packet.getStudyGroup());
            case "remove_greater": return removeGreater(packet.getStudyGroup());
            case "average_of_average_mark": return averageOfAverageMark();
            case "count_less_than_form_of_education": return countLessAndSoOn(packet.getCommand()[1]);
            case "print_field_ascending_semester_enum": return printFieldAndSoOn();
        }
        return null;
    }

    public OutPacket info() {
        String info = list.size() +
                dateOfInitialization.toString();
        return new OutPacket(info);
    }

    public OutPacket show() {
        StringBuilder str = new StringBuilder();
        list.forEach(s -> str.append(s).append("\n"));
        return new OutPacket(str.toString());
    }

    public OutPacket add(StudyGroup group) {
        if (!StudyGroup.getIdSet().contains(group.getId())) {
            if (!Person.getPassportIDSet().contains(group.getGroupAdmin().getPassportID())) {
                list.add(group);
                return new OutPacket("Succeed");
            } else return new OutPacket("Failed passport");
        } else return new OutPacket("Failed id");
    }

    public OutPacket update(String id, StudyGroup group) {
        if (!StudyGroup.getIdSet().contains(Long.parseLong(id))) {
            if (!Person.getPassportIDSet().stream()
                    .filter(x -> !x.equals(group.getGroupAdmin().getPassportID()))
                    .collect(Collectors.toSet()).contains(group.getGroupAdmin().getPassportID())) {
                removeByID(id);
                group.setId(Long.parseLong(id));
                list.add(group);
                StudyGroup.addId(Long.parseLong(id));
                return new OutPacket("Succeed");
            } else return new OutPacket("Failed passport");
        }
        return new OutPacket("Failed id");
    }
    public OutPacket removeByID(String id) {
        if (StudyGroup.getIdSet().contains(Long.parseLong(id))) {
            StudyGroup a = list.stream()
                    .filter(x -> x.getId() == Long.parseLong(id))
                    .collect(Collectors.toList())
                    .get(0);
            list.remove(a);
            StudyGroup.getIdSet().remove(a.getId());
            Person.getPassportIDSet().remove(a.getGroupAdmin().getPassportID());
            return new OutPacket("Succeed");
        } else return new OutPacket("Failed");
    }
    public void clear() {
        list.clear();
        StudyGroup.clearIdList();
        Person.clearPassportIdList();
    }
    public OutPacket head() {
        if (list.size() != 0) {
            return new OutPacket(list.stream()
                    .findFirst()
                    .orElse(new StudyGroup())
                    .toString());
        } else return new OutPacket("Nothing");
    }
    public OutPacket addIfMax(StudyGroup group) {
        if (list.stream().
                noneMatch(x -> x.compareTo(group) > 0)) {
            return add(group);
        } else return new OutPacket("Failed notMax");
    }
    public OutPacket removeGreater(StudyGroup group) {
        int startSize = list.size();
        list.stream()
                .filter(x -> x.compareTo(group) > 0)
                .forEach(list::remove);
        return new OutPacket(Integer.toString(startSize - list.size()));
    }
    public OutPacket averageOfAverageMark() {
        class Thing {
            Float sum;
            void addToSum(float a) {
                sum += a;
            }
        }
        Thing thing = new Thing();
        list.forEach(x -> thing.addToSum(x.getAverageMark()));
        return new OutPacket(Float.toString(thing.sum / list.size()));
    }
    public OutPacket countLessAndSoOn(String formOfEducation) {
        return new OutPacket(Long.toString(list.stream()
                .filter(x -> x
                        .getFormOfEducation()
                        .compareTo(FormOfEducation.valueOf(formOfEducation)) < 0)
                .count()));
    }
    public OutPacket printFieldAndSoOn() {
        StringBuilder str = new StringBuilder();
        list.forEach(s -> str.append(s.getSemesterEnum()).append("\n"));
        return new OutPacket(str.toString());
    }

}
