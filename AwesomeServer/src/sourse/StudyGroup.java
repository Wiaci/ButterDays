package sourse;

import sourse.enums.*;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;

/**
 * Класс - студенческая группа
 * @author Вячесанн Станисеевич
 * @version 7.3
 */

public class StudyGroup implements Comparable<StudyGroup>, Serializable {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long studentsCount; //Значение поля должно быть больше 0, Поле может быть null
    private float averageMark; //Значение поля должно быть больше 0
    private FormOfEducation formOfEducation; //Поле может быть null
    private Semester semesterEnum; //Поле может быть null
    private Person groupAdmin; //Поле не может быть null
    private OffsetDateTime dateOfCreation;
    private static final HashSet<Long> idSet = new HashSet<>();

    public StudyGroup() {}

    public static HashSet<Long> getIdSet() {
        return idSet;
    }

    public StudyGroup(String name, Coordinates coordinates, Long studentsCount, float averageMark,
                      FormOfEducation formOfEducation, Semester semesterEnum, Person groupAdmin) {
        this.name = name;
        this.coordinates = coordinates;
        this.studentsCount = studentsCount;
        this.averageMark = averageMark;
        this.formOfEducation = formOfEducation;
        this.semesterEnum = semesterEnum;
        this.groupAdmin = groupAdmin;
        creationDate = ZonedDateTime.now();
        dateOfCreation = OffsetDateTime.now();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "StudyGroup " +  name + "\n" +
                "\tid=" + id +
                "\n\t coordinates=" + coordinates +
                "\n\t creationDate=" + creationDate +
                "\n\t studentsCount=" + studentsCount +
                "\n\t averageMark=" + averageMark +
                "\n\t formOfEducation=" + formOfEducation +
                "\n\t semesterEnum=" + semesterEnum +
                "\n\t groupAdmin=" + groupAdmin +
                '\n';
    }

    public OffsetDateTime getDateOfCreation() {
        return dateOfCreation;
    }

    public int getX() {
        return coordinates.getX();
    }

    public int getY() {
        return coordinates.getY();
    }

    public Person getGroupAdmin() {
        return groupAdmin;
    }

    public static void clearIdList() {
        idSet.clear();
    }

    public FormOfEducation getFormOfEducation() {
        return formOfEducation;
    }

    public Semester getSemesterEnum() {
        return semesterEnum;
    }

    public long getStudentsCount() {
        return studentsCount;
    }

    public Long getId(){ return id; }

    public void setId(Long id) {
        this.id = id;
    }

    public float getAverageMark() {
        return averageMark;
    }

    public static void addId(long id) {
        idSet.add(id);
    }

    public static long generateRandomId() {
        long randomId = 0L;
        do {
            randomId = (long) (Math.random() * 10000 + 1);
        } while (idSet.contains(randomId));
        return randomId;
    }

    @Override
    public int compareTo(StudyGroup studyGroup) {
        return studentsCount.compareTo(studyGroup.getStudentsCount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyGroup that = (StudyGroup) o;
        return  Objects.equals(studentsCount, that.studentsCount) &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, studentsCount);
    }
}
