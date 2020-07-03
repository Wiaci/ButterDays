package ClientServerCommunicaion.sourse;

import ClientServerCommunicaion.sourse.enums.Color;
import ClientServerCommunicaion.sourse.enums.Country;
import java.io.Serializable;
import java.util.HashSet;

/**
 * Класс - админ группы
 * @author Вячесанн Станисеевич
 * @version 7.3
 */

public class Person implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private float weight; //Значение поля должно быть больше 0
    private String passportID; //Длина строки должна быть не меньше 5, Значение этого поля должно быть уникальным, Длина строки не должна быть больше 20, Поле может быть null
    private Color eyeColor; //Поле может быть null
    private Country nationality; //Поле не может быть null
    private static HashSet<String> passportIDSet = new HashSet<>();

    public static HashSet<String> getPassportIDSet() {
        return passportIDSet;
    }

    private Person() {};

    public Person(String name, float weight, String passportID, Color eyeColor, Country nationality) {
        this.name = name;
        this.weight = weight;
        this.passportID = passportID;
        passportIDSet.add(passportID);
        this.eyeColor = eyeColor;
        this.nationality = nationality;
    }

    public String getName() {
        return name;
    }

    public float getWeight() {
        return weight;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public static void clearPassportIdList() {
        passportIDSet.clear();
    }

    public String getPassportID() {
        return passportID;
    }

    public static void addPassportId(String passportID) {
        passportIDSet.add(passportID);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", weight=" + weight +
                ", passportID='" + passportID + '\'' +
                ", eyeColor=" + eyeColor +
                ", nationality=" + nationality +
                '}';
    }
}
