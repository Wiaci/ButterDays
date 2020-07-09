package GUI;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Objects;

public class GroupDescriber {

    private final int NUMBER_OF_STEPS = 60;

    private static HashSet<String> idSet = new HashSet<>();

    private String id;

    private Rectangle2D rectangle;
    private double size;

    private double y;

    private Color aimColor;
    private Color color;


    private double aimX;
    private double aimY;
    private double rotation;

    private double rotX;
    private double rotY;

    private double red;
    private double green;
    private double blue;

    private double redStep;
    private double greenStep;
    private double blueStep;


    private int state; // 1 - добавлено, 0 - удаляется, -1 - удалено

    public Rectangle2D getRectangle() {
        return rectangle;
    }

    public GroupDescriber(String id, double size, double aimX, double aimY, Color color) {
        this.id = id;
        this.size = size;
        this.aimX = aimX + 275 - size / 2;
        this.aimY = -aimY + 175 - size / 2;
        rotX = aimX + 275;
        rotY = -aimY + 175;
        aimColor = color;
        System.out.println(color);
        rectangle = new Rectangle2D.Double(this.aimX, this.aimY, size, size);
        state = 1;

        red = 255; green = 255; blue = 255;
        this.color = new Color((int) red, (int) green, (int) blue);

        redStep = (255 - color.getRed()) / (double) NUMBER_OF_STEPS;
        greenStep = (255 - color.getGreen()) / (double) NUMBER_OF_STEPS;
        blueStep = (255 - color.getBlue()) / (double) NUMBER_OF_STEPS;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isDeleted() {
        return state == -1;
    }

    public boolean isDeleting() {
        return state == 0;
    }

    public static HashSet<String> getIdSet() {
        return idSet;
    }

    public String getId() {
        return id;
    }

    public double getRotation() {
        return rotation;
    }

    public Color getColor() {
        return color;
    }

    public void opacityPlus() {
        red -= redStep;
        green -= greenStep;
        blue -= blueStep;
        color = new Color((int) red, (int) green, (int) blue);
    }

    public void opacityMinus() {
        red += redStep;
        green += greenStep;
        blue += blueStep;
        if(red > 255) red = 255;
        if(green > 255) green = 255;
        if(blue > 255) blue = 255;
        color = new Color((int) red, (int) green, (int) blue);
    }

    public double getY() {
        return y;
    }

    public double getAimY() {
        return aimY;
    }

    public double getRotX() {
        return rotX;
    }

    public double getRotY() {
        return rotY;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void down() {
        y++;
        rotate();
        System.out.println("I'm here! " + y);
        if (state == 0) opacityMinus();
        else opacityPlus();
        rectangle.setRect(aimX, y, size, size);
    }

    public void rotate() {
        rotation += 360 / (double) NUMBER_OF_STEPS;
        rotY = y + size/2;
        rotX = aimX + size/2;
    }

    public void setToAppear() {
        idSet.add(id);
        y = aimY - NUMBER_OF_STEPS;
        rotY = y + size/2;
        rotX = aimX + size/2;
        rectangle.setRect(this.aimX, y, size, size);
    }

    public void setToRemove() {
        state = 0;
        aimY = y + NUMBER_OF_STEPS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupDescriber that = (GroupDescriber) o;
        return Double.compare(that.size, size) == 0 &&
                Double.compare(that.aimX, aimX) == 0 &&
                Double.compare(that.aimY, aimY) == 0 &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, size, aimX, aimY);
    }
}