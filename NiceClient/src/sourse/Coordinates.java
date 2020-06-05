package sourse;
import java.io.Serializable;

/**
 * Класс - координаты
 * @author Вячесанн Станисеевич
 * @version 7.3
 */

public class Coordinates implements Serializable {
    private int x;
    private Integer y; //Значение поля должно быть больше -791, Поле не может быть null

    public Coordinates(int x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private Coordinates() {};

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}