package GUI;

import ClientServerCommunicaion.NiceClient;
import ClientServerCommunicaion.sourse.Coordinates;
import ClientServerCommunicaion.sourse.Person;
import ClientServerCommunicaion.sourse.StudyGroup;
import ClientServerCommunicaion.sourse.enums.Country;

import javax.swing.*;
import java.awt.*;
import java.net.SocketTimeoutException;

public class ButtonPanel extends JPanel {

    LanguageSwitcher languageSwitcher;

    public ButtonPanel(LanguageSwitcher languageSwitcher, ReadGroup readAction,
                       UserMagicInteract magic, NiceClient client, String login, String password,
                       JFrame mainFrame) {
        setSize(150, getHeight());
        setLayout(new GridLayout(8,1));
        this.languageSwitcher = languageSwitcher;
        setBackground(Color.WHITE);
        JButton reload = new JButton();
        languageSwitcher.subscribe(reload, "reload");
        JButton addButton = new JButton(readAction);
        languageSwitcher.subscribe(addButton, "add");
        add(addButton);


        reload.addActionListener(s -> {
            try {
                magic.getActualData();
            } catch (SocketTimeoutException e) {
                GuiGarbage.connectionLost();
            }
        });



        JButton infoButton = new JButton();
        languageSwitcher.subscribe(infoButton, "info");

        infoButton.addActionListener(s -> {
            try {
                String response = client.launchCommand("info", login, password);
                JOptionPane.showMessageDialog(mainFrame, languageSwitcher.getBundle().getString("groups_count") +
                                " " + response, languageSwitcher.getBundle().getString("info"),
                        JOptionPane.INFORMATION_MESSAGE);
                magic.getActualData();
            } catch (SocketTimeoutException e) {
                GuiGarbage.connectionLost();
            }
        });

        add(infoButton);

        JButton removeGreater = new JButton();
        languageSwitcher.subscribe(removeGreater, "remove_greater");

        removeGreater.addActionListener(s -> {
            try {
                String size = "";
                while (!size.matches("\\d+"))
                    size = JOptionPane.showInputDialog("Введите величину группы(положительное число)");
                //magic.removeGreater(login, Integer.parseInt(size));
                client.launchCommand("remove_greater", new StudyGroup("", new Coordinates(0,0),
                        Long.parseLong(size), 0, null, null,
                        new Person("", 0, null, null, Country.FRANCE)), login, password);
                magic.getActualData();
            } catch (SocketTimeoutException e) {
                GuiGarbage.connectionLost();
            }
        });

        add(removeGreater);

        JButton average = new JButton();
        languageSwitcher.subscribe(average, "average");

        average.addActionListener(s -> {
            try {
                JOptionPane.showMessageDialog(mainFrame, client.launchCommand(
                        "average_of_average_mark", login, password), "", JOptionPane.INFORMATION_MESSAGE);
                magic.getActualData();
            } catch (SocketTimeoutException e) {
                GuiGarbage.connectionLost();
            }
        });

        add(average);

        JButton countLess = new JButton();
        languageSwitcher.subscribe(countLess, "count_less");

        countLess.addActionListener(s -> {
            JFrame jFrame = new JFrame();
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            jFrame.setLocation(d.width*3/4, d.height/2);
            jFrame.setVisible(true);
            JPanel panel = new JPanel(new GridLayout(2,2));
            jFrame.add(panel);
            ButtonGroup group = new ButtonGroup();
            JRadioButton r1 = new JRadioButton("DISTANCE_EDUCATION"); group.add(r1); panel.add(r1);
            JRadioButton r2 = new JRadioButton("FULL_TIME_EDUCATION"); group.add(r2); panel.add(r2);
            JRadioButton r3 = new JRadioButton("EVENING_CLASSES"); group.add(r3); panel.add(r3);
            r1.setSelected(true);
            JButton ok = new JButton("OK");
            panel.add(ok);
            jFrame.pack();
            ok.addActionListener(a -> {
                jFrame.setVisible(false);
                String selected = "";
                if (r1.isSelected()) selected = r1.getText();
                if (r2.isSelected()) selected = r2.getText();
                if (r3.isSelected()) selected = r3.getText();
                try {
                    JOptionPane.showMessageDialog(mainFrame, client.launchCommand(
                            "count_less_than_form_of_education " + selected, login, password), "",
                            JOptionPane.INFORMATION_MESSAGE);
                    magic.getActualData();
                } catch (SocketTimeoutException e) {
                    GuiGarbage.connectionLost();
                }
            });
        });

        add(countLess);

        JButton printField = new JButton();
        languageSwitcher.subscribe(printField, "print_field");
        printField.addActionListener(s -> {
            try {
                JOptionPane.showMessageDialog(mainFrame, client.launchCommand(
                        "print_field_ascending_semester_enum", login, password), "",
                        JOptionPane.INFORMATION_MESSAGE);
                magic.getActualData();
            } catch (SocketTimeoutException e) {
                GuiGarbage.connectionLost();
            }
        });

        add(printField);

        add(new JLabel());

        add(reload);

        languageSwitcher.updateLabels();
    }
}