package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.stream.Collectors;


public class AreaPanel extends JPanel {

    HashSet<GroupDescriber> toDraw;

    public AreaPanel(LanguageSwitcher languageSwitcher) {
        toDraw = new HashSet<>();
        setBackground(Color.WHITE);

        InfoPanel infoPanel = new InfoPanel(languageSwitcher);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = e.getPoint();
                boolean isImaged = false;
                for (GroupDescriber describer : toDraw) {
                    if (describer.getRectangle().contains(point)) {
                        isImaged = true;
                        infoPanel.imageValues(
                                UserMagicInteract.getValuesByID(
                                        describer.getId()));
                    }
                }
                if (!isImaged) infoPanel.imageValues(new String[14]);
            }
        });
        setLayout(new BorderLayout());
        add(infoPanel, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform transform = g2.getTransform();
        for (GroupDescriber group : toDraw) {
            if (!group.isDeleted()) {
                g2.rotate(Math.toRadians(group.getRotation()), group.getRotX(), group.getRotY());
                g2.setColor(group.getColor());
                g2.fill(group.getRectangle());
                g2.setTransform(transform);
            }
        }

        g2.setColor(Color.BLACK);

        Rectangle2D rectangle = new Rectangle2D.Double(25, 25, 500, 300);
        g2.draw(rectangle);
        Line2D line1 = new Line2D.Double(0, 175, 550, 175);
        Line2D line2 = new Line2D.Double(275, 5, 275, 345);
        g2.draw(line1);
        g2.draw(line2);

        checkLocation();
    }

    public void checkLocation() {
        boolean isChanged = false;
        for (GroupDescriber group : toDraw) {
            System.out.println("Y: " + group.getId() + " " + group.getY());
            System.out.println("YA: " + group.getId() + " " + group.getAimY());
            if (Math.abs(group.getY() - group.getAimY()) >= 1) {
                group.down();
                isChanged = true;
            } else if (group.isDeleting()) {
                group.setState(-1);
                isChanged = true;
            }
        }
        if (isChanged) {
            try {
                Thread.sleep(5);
                repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(GroupDescriber describer) {
        System.out.println(GroupDescriber.getIdSet());
        if (GroupDescriber.getIdSet().contains(describer.getId())) {
            GroupDescriber oldDescriber = toDraw.stream()
                    .filter(s -> s.getId().equals(describer.getId()))
                    .collect(Collectors.toList())
                    .get(0);
            if (!oldDescriber.equals(describer)) {
                remove(describer.getId());
                toDraw.add(describer);
                describer.setToAppear();
            }
        } else {
            toDraw.add(describer);
            describer.setToAppear();
        }
        describer.setToAppear();
        repaint();
    }

    public void remove(String id) {
        GroupDescriber describer = toDraw.stream()
                .filter(s -> s.getId().equals(id))
                .collect(Collectors.toList())
                .get(0);
        describer.setToRemove();
        repaint();
    }
}

