package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


public class AreaPanel extends JPanel {

    ArrayList<GroupDescriber> toDraw;
    HashSet<String> idSad;
    JTable table;
    String login;
    ReadGroup readAction;
    JFrame groupFrame;
    JFrame mainFrame;

    public void setInformation(JTable table, String login, ReadGroup readAction, JFrame groupFrame, JFrame mainFrame) {
        this.table = table;
        this.login = login;
        this.readAction = readAction;
        this.groupFrame = groupFrame;
        this.mainFrame = mainFrame;
    }

    public AreaPanel(LanguageSwitcher languageSwitcher) {
        toDraw = new ArrayList<>();
        idSad = new HashSet<>();
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

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = e.getPoint();
                if (e.getButton() == 3) {
                    for (GroupDescriber describer : toDraw) {
                        if (describer.getRectangle().contains(point)) {

                            String id = describer.getId();
                            String[] selectedRow = UserMagicInteract.getValuesByID(id);

                            String user = selectedRow[0];
                            if (user.equals(login)) {
                                readAction.setUpdateMode(new String[] {selectedRow[2], selectedRow[3],
                                        selectedRow[4], selectedRow[5], selectedRow[6], selectedRow[9],
                                        selectedRow[10], selectedRow[11], selectedRow[7], selectedRow[8],
                                        selectedRow[12], selectedRow[13]}, selectedRow[1]);
                                groupFrame.setVisible(true);
                            } else {
                                JOptionPane.showMessageDialog(mainFrame, languageSwitcher.getBundle().getString("no_access"),
                                        languageSwitcher.getBundle().getString("warning"), JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }
            }
        });

        addMouseWheelListener(e -> {
            int type = e.getWheelRotation();
            if (type == 1) toDraw.forEach(GroupDescriber::rotationPlus);
            else toDraw.forEach(GroupDescriber::rotationMinus);
            repaint();
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
                /*g2.rotate(Math.toRadians(group.getRotation()), group.getRotX(), group.getRotY());
                g2.setColor(group.getColor());
                g2.fill(group.getRectangle());*/
                group.drawHumans(g2);
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
        g2.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        g2.drawString("150", 278, 13);
        g2.drawString("-150", 278, 342);
        g2.drawString("250", 527, 172);
        g2.drawString("-250", -2, 172);

        Point start = new Point(0,0);
        double n = 200;
        Rectangle2D edges = new Rectangle2D.Double(start.x, start.y, n, n);

        /*Ellipse2D head1 = new Ellipse2D.Double(start.x, start.y,n * 0.4, n * 0.4);
        Rectangle2D body1 = new Rectangle2D.Double(0.15 * n + start.x, 0.2 * n, 0.1 * n, 0.6 * n);
        Rectangle2D arms1 = new Rectangle2D.Double(start.x, 0.55 * n, 0.4 * n, 0.075 * n);
        Rectangle2D leg1_1 = new Rectangle2D.Double(0.125 * n + start.x, 0.7 * n, 0.075 * n, 0.3 * n);
        Rectangle2D leg1_2 = new Rectangle2D.Double(0.205 * n + start.x, 0.7 * n, 0.075 * n, 0.3 * n);

        Ellipse2D head2 = new Ellipse2D.Double(start.x + 0.6 * n, start.y,n * 0.4, n * 0.4);
        Rectangle2D body2 = new Rectangle2D.Double(0.15 * n + start.x + 0.6 * n, 0.2 * n, 0.1 * n, 0.6 * n);
        Rectangle2D arms2 = new Rectangle2D.Double(start.x + 0.6 * n, 0.55 * n, 0.4 * n, 0.075 * n);
        Rectangle2D leg2_1 = new Rectangle2D.Double(0.125 * n + start.x + 0.6 * n, 0.7 * n, 0.075 * n, 0.3 * n);
        Rectangle2D leg2_2 = new Rectangle2D.Double(0.205 * n + start.x + 0.6 * n, 0.7 * n, 0.075 * n, 0.3 * n);

        g2.draw(edges);
        g2.fill(head1); g2.fill(body1); g2.fill(arms1);
        g2.rotate(Math.toRadians(20), 0.15 * n, 0.85 * n); g2.fill(leg1_1);
        g2.setTransform(transform);
        g2.rotate(Math.toRadians(-20), 0.23 * n, 0.85 * n); g2.fill(leg1_2);
        g2.setTransform(transform);

        g2.draw(edges);
        g2.fill(head2); g2.fill(body2); g2.fill(arms2);
        g2.rotate(Math.toRadians(20), 0.15 * n + 0.6 * n, 0.85 * n); g2.fill(leg2_1);
        g2.setTransform(transform);
        g2.rotate(Math.toRadians(-20), 0.23 * n + 0.6 * n, 0.85 * n); g2.fill(leg2_2);
        g2.setTransform(transform);*/
        //g2.fill(head2); g2.fill(body2); g2.fill(arms2);


        checkLocation();
    }

    public void checkSet(String[] id) {
        List<String> ids = Arrays.asList(id);
        boolean isChanged = false;
        //ArrayList<GroupDescriber> list = new ArrayList<>();
        for (GroupDescriber describer : toDraw) {
            if (!ids.contains(describer.getId())) {
                describer.setToRemove();
                isChanged = true;
            }
            //if (describer.isDeleted()) list.add(describer);
        }
        //list.forEach(s -> toDraw.remove(s));
        if (isChanged) repaint();
    }

    public void checkLocation() {
        boolean isChanged = false;
        for (GroupDescriber group : toDraw) {
            if (Math.abs(group.getY() - group.getAimY()) >= 1) {
                group.down();
                isChanged = true;
            }
            if (Math.abs(group.getY() - group.getAimY()) <= 2 && group.isDeleting()) {
                group.setState(-1);
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
        if (idSad.contains(describer.getId())) {
            GroupDescriber oldDescriber = toDraw.stream()
                    .filter(s -> s.getId().equals(describer.getId()))
                    .filter(s -> !s.isDeleted() && !s.isDeleting())
                    .collect(Collectors.toList())
                    .get(0);
            if (!oldDescriber.equals(describer)) {
                remove(describer.getId());
                toDraw.add(describer);
                describer.setToAppear();
            }
        } else {
            idSad.add(describer.getId());
            toDraw.add(describer);
            describer.setToAppear();
        }
        describer.setToAppear();
        repaint();
    }

    public void remove(String id) {
        GroupDescriber describer = null;
        for (GroupDescriber groupDescriber : toDraw) {
            if (groupDescriber.getId().equals(id)) describer = groupDescriber;
        }
        if (describer != null) describer.setToRemove();
        repaint();
    }
}

