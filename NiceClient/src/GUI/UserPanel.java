package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class UserPanel extends JPanel {

    Color color;
    String name;
    int nameX;

    public UserPanel() {
        nameX = 40;

        addMouseWheelListener(s -> {
            int type = s.getWheelRotation();
            if (type == 1) nameX += 3;
            else nameX -= 3;
            if (nameX > 200) nameX = 20;
            if (nameX < 40) nameX = 200;
            repaint();
        });
    }

    public void setColor(Color color) {
        this.color = color;
        repaint();
    }

    public void setNewName(String name) {
        this.name = name;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        Rectangle2D rectangle = new Rectangle2D.Double(40, 40, 160, 160);
        g2.fill(rectangle);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        if (name == null) name = "Queen Elisabeth";
        g2.drawString(name, nameX, 250);
        revalidate();
    }
}
