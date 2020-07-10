package GUI;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

public class GroupDescriber {

    private final int NUMBER_OF_STEPS = 60;

    private String id;
    private Color color;
    private double red;
    private double green;
    private double blue;
    private double redStep;
    private double greenStep;
    private double blueStep;

    private Rectangle2D rectangle;
    boolean isVisible;
    private double size;
    private double y;
    private double aimX;
    private double aimY;
    private double rotation;
    private double rotX;
    private double rotY;

    private Humans humans;

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
        rectangle = new Rectangle2D.Double(this.aimX, this.aimY, size, size);
        humans = new Humans(rectangle);
        state = 1;
        isVisible = false;

        red = 255; green = 255; blue = 255;
        this.color = new Color((int) red, (int) green, (int) blue);

        redStep = (255 - color.getRed()) / (double) NUMBER_OF_STEPS;
        greenStep = (255 - color.getGreen()) / (double) NUMBER_OF_STEPS;
        blueStep = (255 - color.getBlue()) / (double) NUMBER_OF_STEPS;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
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

    public String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public void opacityPlus() {
        red -= redStep;
        green -= greenStep;
        blue -= blueStep;
        if(red <0) red = 0;
        if(green <0) green = 0;
        if(blue <0) blue = 0;
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

    public void rotateAndDown() {
        rotation += 360 / (double) NUMBER_OF_STEPS;
        rotY = y + size/2;
        rotX = aimX + size/2;
    }

    public void rotationPlus() {
        rotation += 9;
    }

    public void rotationMinus() {
        rotation -= 9;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void down() {
        y++;
        rotateAndDown();
        if (state == 0) opacityMinus();
        else opacityPlus();
        rectangle.setRect(aimX, y, size, size);
        humans.updatePosition(rectangle);
    }

    public void setToAppear() {
        y = aimY - NUMBER_OF_STEPS;
        rotY = y + size/2;
        rotX = aimX + size/2;
        rectangle.setRect(this.aimX, y, size, size);
        humans.updatePosition(rectangle);
    }

    public void setToRemove() {
        state = 0;
        aimY = y + NUMBER_OF_STEPS;
    }

    public void drawHumans(Graphics2D g2) {
        g2.rotate(Math.toRadians(rotation), rotX, rotY);
        if (isVisible) {
            g2.setColor(Color.BLACK);
            g2.draw(rectangle);
        }
        g2.setColor(color);
        humans.draw(g2, color);
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

class Humans {

    Ellipse2D head1;
    Rectangle2D body1;
    Rectangle2D arms1;
    Rectangle2D leftLeg1;
    double rotXl1;
    double rotY;
    Rectangle2D rightLeg1;
    double rotXr1;

    Ellipse2D head2;
    Rectangle2D body2;
    Rectangle2D arms2;
    Rectangle2D leftLeg2;
    double rotXl2;
    Rectangle2D rightLeg2;
    double rotXr2;

    public Humans(Rectangle2D edges) {
        head1 = new Ellipse2D.Double();
        body1 = new Rectangle2D.Double();
        arms1 = new Rectangle2D.Double() ;
        leftLeg1 = new Rectangle2D.Double();
        rightLeg1 = new Rectangle2D.Double();
        head2 = new Ellipse2D.Double();
        arms2 = new Rectangle2D.Double();
        leftLeg2 = new Rectangle2D.Double();
        rightLeg2 = new Rectangle2D.Double();
        updatePosition(edges);
    }

    public void updatePosition(Rectangle2D edges) {
        double x = edges.getX();
        double y = edges.getY();
        double n = edges.getWidth();

        head1 = new Ellipse2D.Double(x, y,n * 0.4, n * 0.4);
        body1 = new Rectangle2D.Double(0.15 * n + x, 0.2 * n + y, 0.1 * n, 0.6 * n);
        arms1 = new Rectangle2D.Double(x, 0.50 * n + y, 0.4 * n, 0.075 * n);
        leftLeg1 = new Rectangle2D.Double(0.125 * n + x, 0.7 * n + y, 0.075 * n, 0.3 * n);
        rightLeg1 = new Rectangle2D.Double(0.205 * n + x, 0.7 * n + y, 0.075 * n, 0.3 * n);

        head2 = new Ellipse2D.Double(x + 0.6 * n, y,n * 0.4, n * 0.4);
        body2 = new Rectangle2D.Double(0.75 * n + x, 0.2 * n + y, 0.1 * n, 0.6 * n);
        arms2 = new Rectangle2D.Double(x + 0.6 * n, 0.50 * n + y, 0.4 * n, 0.075 * n);
        leftLeg2 = new Rectangle2D.Double(0.725 * n + x, 0.7 * n + y, 0.075 * n, 0.3 * n);
        rightLeg2 = new Rectangle2D.Double(0.805 * n + x, 0.7 * n + y, 0.075 * n, 0.3 * n);

        rotY = 0.85 * n + y;
        rotXl1 = 0.15 * n + x;
        rotXr1 = 0.23 * n + x;
        rotXl2 = 0.75 * n + x;
        rotXr2 = 0.83 * n + x;
    }

    public void draw(Graphics2D g2, Color color) {
        g2.setColor(color);
        AffineTransform transform = g2.getTransform();
        g2.fill(head1);
        g2.fill(head2);
        g2.fill(body1);
        g2.fill(body2);
        g2.fill(arms1);
        g2.fill(arms2);
        g2.rotate(Math.toRadians(20), rotXl1, rotY);
        g2.fill(leftLeg1);
        g2.setTransform(transform);
        g2.rotate(Math.toRadians(20), rotXl2, rotY);
        g2.fill(leftLeg2);
        g2.setTransform(transform);
        g2.rotate(Math.toRadians(-20), rotXr1, rotY);
        g2.fill(rightLeg1);
        g2.setTransform(transform);
        g2.rotate(Math.toRadians(-20), rotXr2, rotY);
        g2.fill(rightLeg2);
        g2.setTransform(transform);
    }


}