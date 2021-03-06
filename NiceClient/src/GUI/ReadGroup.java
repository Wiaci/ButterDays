package GUI;

import ClientServerCommunicaion.NiceClient;
import ClientServerCommunicaion.sourse.StudyGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.SocketTimeoutException;
import java.util.ResourceBundle;

public class ReadGroup extends AbstractAction {

    private String login;
    private String password;
    private JFrame groupFrame;
    private LanguageSwitcher lSwitcher;
    private boolean isAddMode;
    private JCheckBox isMax;
    private JTextField nameField;
    private JTextField xField;
    private JTextField yField;
    private JTextField studentsCountField;
    private JTextField averageMarkField;
    private JTextField adminNameField;
    private JTextField weightField;
    private JTextField passportIdField;
    private String id;
    UserMagicInteract magic;

    public void setLoginAndPassword(String login, String password, UserMagicInteract magic) {
        this.login = login;
        this.password = password;
        this.magic = magic;
    }

    public JFrame getGroupFrame() {
        return groupFrame;
    }

    public void setAddMode() {
        isMax.setVisible(true);
        lSwitcher.subscribe(groupFrame, "new_group");
        isAddMode = true;
        nameField.setText("");
        xField.setText("");
        yField.setText("");
        studentsCountField.setText("");
        averageMarkField.setText("");
        adminNameField.setText("");
        weightField.setText("");
        passportIdField.setText("");
    }

    public void setUpdateMode(String[] values, String id) {
        isAddMode = false;

        this.id = id;
        isMax.setVisible(false);
        isMax.setSelected(false);
        lSwitcher.subscribe(groupFrame, "update");
        nameField.setText(values[0]);
        xField.setText(values[1]);
        yField.setText(values[2]);
        studentsCountField.setText(values[3]);
        averageMarkField.setText(values[4]);
        adminNameField.setText(values[5]);
        weightField.setText(values[6]);
        passportIdField.setText(values[7]);
    }

    public ReadGroup(NiceClient client, LanguageSwitcher languageSwitcher, ResourceBundle bundle) {
        lSwitcher = languageSwitcher;
        groupFrame = GuiGarbage.getFrame(300, 500, JFrame.HIDE_ON_CLOSE);
        groupFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                setAddMode();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                setAddMode();
            }
        });
        groupFrame.setVisible(false);
        JPanel panel = new JPanel();
        groupFrame.add(panel);

        JLabel name = new JLabel(); languageSwitcher.subscribe(name, "name");
        JLabel x = new JLabel(); languageSwitcher.subscribe(x, "x_coordinate");
        JLabel y = new JLabel(); languageSwitcher.subscribe(y, "y_coordinate");
        JLabel studentsCount = new JLabel(); languageSwitcher.subscribe(studentsCount, "students_count");
        JLabel averageMark = new JLabel(); languageSwitcher.subscribe(averageMark, "average_mark");
        JLabel formOfEducation = new JLabel(); languageSwitcher.subscribe(formOfEducation, "form_of_education");
        JLabel semester = new JLabel(); languageSwitcher.subscribe(semester, "semester");
        JLabel adminName = new JLabel(); languageSwitcher.subscribe(adminName, "admin_name");
        JLabel weight = new JLabel(); languageSwitcher.subscribe(weight, "weight");
        JLabel passportID = new JLabel(); languageSwitcher.subscribe(passportID, "passportID");
        JLabel eyeColor = new JLabel(); languageSwitcher.subscribe(eyeColor, "eye_color");
        JLabel country = new JLabel(); languageSwitcher.subscribe(country, "country");

        nameField = new JTextField(20);
        xField = new JTextField(20);
        yField = new JTextField(20);
        studentsCountField = new JTextField(20);
        averageMarkField = new JTextField(20);
        adminNameField = new JTextField(20);
        weightField = new JTextField(20);
        passportIdField = new JTextField(20);

        JRadioButton form1 = new JRadioButton("DISTANCE_EDUCATION");
        JRadioButton form2 = new JRadioButton("FULL_TIME_EDUCATION");
        JRadioButton form3 = new JRadioButton("EVENING_CLASSES");
        JRadioButton formNone = new JRadioButton();
        languageSwitcher.subscribe(formNone, "not_defined");
        formNone.setSelected(true);
        ButtonGroup formGroup = new ButtonGroup();
        formGroup.add(form1);
        formGroup.add(form2);
        formGroup.add(form3);
        formGroup.add(formNone);

        JRadioButton semester1 = new JRadioButton("FOURTH");
        JRadioButton semester2 = new JRadioButton("FIFTH");
        JRadioButton semester3 = new JRadioButton("SIXTH");
        JRadioButton semester4 = new JRadioButton("EIGHTH");
        JRadioButton semesterNone = new JRadioButton();
        languageSwitcher.subscribe(semesterNone, "not_defined");
        semesterNone.setSelected(true);
        ButtonGroup semesterGroup = new ButtonGroup();
        semesterGroup.add(semester1);
        semesterGroup.add(semester2);
        semesterGroup.add(semester3);
        semesterGroup.add(semester4);
        semesterGroup.add(semesterNone);

        JRadioButton eyeColor1 = new JRadioButton("RED");
        JRadioButton eyeColor2 = new JRadioButton("YELLOW");
        JRadioButton eyeColor3 = new JRadioButton("ORANGE");
        JRadioButton eyeColor4 = new JRadioButton("BROWN");
        JRadioButton eyeColorNone = new JRadioButton("Not defined");
        languageSwitcher.subscribe(eyeColorNone, "not_defined");
        eyeColorNone.setSelected(true);
        ButtonGroup eyeColorGroup = new ButtonGroup();
        eyeColorGroup.add(eyeColor1);
        eyeColorGroup.add(eyeColor2);
        eyeColorGroup.add(eyeColor3);
        eyeColorGroup.add(eyeColor4);
        eyeColorGroup.add(eyeColorNone);

        JRadioButton country1 = new JRadioButton("FRANCE");
        JRadioButton country2 = new JRadioButton("SPAIN");
        JRadioButton country3 = new JRadioButton("INDIA");
        JRadioButton country4 = new JRadioButton("JAPAN");
        country1.setSelected(true);
        ButtonGroup countryGroup = new ButtonGroup();
        countryGroup.add(country1);
        countryGroup.add(country2);
        countryGroup.add(country3);
        countryGroup.add(country4);

        GridLayout layout = new GridLayout(27, 2);
        layout.setHgap(5);

        panel.setLayout(layout);
        panel.add(name);
        panel.add(nameField);
        panel.add(x);
        panel.add(xField);
        panel.add(y);
        panel.add(yField);
        panel.add(studentsCount);
        panel.add(studentsCountField);
        panel.add(averageMark);
        panel.add(averageMarkField);
        panel.add(formOfEducation);
        panel.add(form1);
        panel.add(new JLabel());
        panel.add(form2);
        panel.add(new JLabel());
        panel.add(form3);
        panel.add(new JLabel());
        panel.add(formNone);
        panel.add(semester);
        panel.add(semester1);
        panel.add(new JLabel());
        panel.add(semester2);
        panel.add(new JLabel());
        panel.add(semester3);
        panel.add(new JLabel());
        panel.add(semester4);
        panel.add(new JLabel());
        panel.add(semesterNone);
        panel.add(adminName);
        panel.add(adminNameField);
        panel.add(weight);
        panel.add(weightField);
        panel.add(passportID);
        panel.add(passportIdField);
        panel.add(eyeColor);
        panel.add(eyeColor1);
        panel.add(new JLabel());
        panel.add(eyeColor2);
        panel.add(new JLabel());
        panel.add(eyeColor3);
        panel.add(new JLabel());
        panel.add(eyeColor4);
        panel.add(new JLabel());
        panel.add(eyeColorNone);
        panel.add(country);
        panel.add(country1);
        panel.add(new JLabel());
        panel.add(country2);
        panel.add(new JLabel());
        panel.add(country3);
        panel.add(new JLabel());
        panel.add(country4);

        JButton submitButton = new JButton();
        languageSwitcher.subscribe(submitButton, "submit");
        panel.add(submitButton);

        isMax = new JCheckBox("MAX");
        panel.add(isMax);

        panel.revalidate();

        setAddMode();

        submitButton.addActionListener(s -> {

            String form = null;
            if (form1.isSelected()) form = form1.getText();
            if (form2.isSelected()) form = form2.getText();
            if (form3.isSelected()) form = form3.getText();
            if (form == null) form = "";

            String sem = null;
            if (semester1.isSelected()) sem = semester1.getText();
            if (semester2.isSelected()) sem = semester2.getText();
            if (semester3.isSelected()) sem = semester3.getText();
            if (semester4.isSelected()) sem = semester4.getText();
            if (sem == null) sem = "";

            String color = null;
            if (eyeColor1.isSelected()) color = eyeColor1.getText();
            if (eyeColor2.isSelected()) color = eyeColor2.getText();
            if (eyeColor3.isSelected()) color = eyeColor3.getText();
            if (eyeColor4.isSelected()) color = eyeColor4.getText();
            if (color == null) color = "";

            String nation = null;
            if (country1.isSelected()) nation = country1.getText();
            if (country2.isSelected()) nation = country2.getText();
            if (country3.isSelected()) nation = country3.getText();
            if (country4.isSelected()) nation = country4.getText();

            StudyGroup group = UserMagicInteract.getStudyGroup(nameField,xField,yField,studentsCountField,averageMarkField,adminNameField,
                    weightField, passportIdField, form, sem, color, nation);

            try {
                if (group != null) {
                    if (isAddMode) {
                        String response;
                        if (isMax.isSelected()) {
                            response = client.launchCommand("add_if_max", group, login, password);
                        } else {
                            response = client.launchCommand("add", group, login, password);
                        }
                        if (!response.equals("failed") && !response.equals("notMax")) {
                            magic.add(new String[] {login, response, nameField.getText(), xField.getText(), yField.getText(),
                                    studentsCountField.getText(), averageMarkField.getText(), form, sem, adminNameField.getText(),
                                    weightField.getText(), passportIdField.getText(), color, nation});
                            groupFrame.setVisible(false);
                            formNone.setSelected(true);
                            semesterNone.setSelected(true);
                            eyeColorNone.setSelected(true);
                            country1.setSelected(true);
                        } else if (response.equals("failed")) {
                            JOptionPane.showMessageDialog(panel, languageSwitcher.getBundle().getString("add_declined_passportID"),
                                    languageSwitcher.getBundle().getString("error"), JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(panel, languageSwitcher.getBundle().getString("add_declined_not_biggest"),
                                    languageSwitcher.getBundle().getString("error"), JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        String response = client.launchCommand("update " + id, group, login, password);
                        if (!response.equals("succeed")) {
                            JOptionPane.showMessageDialog(panel, languageSwitcher.getBundle().getString("update_declined_passportID"),
                                    languageSwitcher.getBundle().getString("error"), JOptionPane.ERROR_MESSAGE);
                        } else {
                            group.setId(Long.parseLong(id));
                            magic.update(group, login);
                            groupFrame.setVisible(false);
                            setAddMode();
                            formNone.setSelected(true);
                            semesterNone.setSelected(true);
                            eyeColorNone.setSelected(true);
                            country1.setSelected(true);
                        }
                    }
                }
            } catch (SocketTimeoutException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        groupFrame.setVisible(true);
    }
}