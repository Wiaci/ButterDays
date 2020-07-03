package GUI;

import ClientServerCommunicaion.NiceClient;
import ClientServerCommunicaion.sourse.StudyGroup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.SocketTimeoutException;

public class GuiManager {

    NiceClient client;
    String login;
    String password;
    JFrame mainFrame;
    JLabel answer;
    UserMagicInteract interact = new UserMagicInteract();
    StudyGroup studyGroup;


    class ReadCommand extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                studyGroupField();
                //answer.setText(client.launchCommand(textField.getText(), login, password));
            } /*catch (SocketTimeoutException ex) {
                ex.printStackTrace();
            }*/ finally {

            }
        }
    }

    public GuiManager(NiceClient client) {
        this.client = client;
        client.checkConnection();

        mainFrame();
        authorize();


    }

    private void authorize() {
        mainFrame.setVisible(false);
        JFrame authFrame = getFrame(200, 150, 3);
        authFrame.setTitle("Authorization");
        JButton registration = new JButton("Registration");
        JButton register = new JButton("Register");
        JButton submit = new JButton("Submit");
        JButton goBack = new JButton("Go Back");

        JTextField loginField = new JTextField(10);
        JPasswordField passwordField = new JPasswordField(10);
        JLabel loginLabel = new JLabel("Login");
        JLabel passLabel = new JLabel("Password");
        JLabel message = new JLabel();

        JPanel panel = new JPanel();
        authFrame.add(panel);
        panel.add(loginLabel);
        panel.add(loginField);
        panel.add(passLabel);
        panel.add(passwordField);
        panel.add(message);
        panel.add(submit);
        panel.add(registration);
        panel.add(register);
        panel.add(goBack);
        register.setVisible(false);
        goBack.setVisible(false);
        message.setVisible(false);

        registration.addActionListener(e -> {
            registration.setVisible(false);
            submit.setVisible(false);
            register.setVisible(true);
            goBack.setVisible(true);
            message.setVisible(false);
            authFrame.setTitle("Registration");

        });

        goBack.addActionListener(e -> {
            registration.setVisible(true);
            submit.setVisible(true);
            register.setVisible(false);
            goBack.setVisible(false);
            message.setVisible(false);
            authFrame.setTitle("Authorization");
        });

        register.addActionListener(e -> {
            String inputLogin = loginField.getText();
            String inputPass = String.valueOf(passwordField.getPassword());
            try {
                if (client.register(inputLogin, inputPass)) {
                    message.setVisible(true);
                    message.setForeground(new Color(43, 119, 32, 244));
                    message.setText("Register is successful");

                    registration.setVisible(true);
                    submit.setVisible(true);
                    register.setVisible(false);
                    goBack.setVisible(false);
                    authFrame.setTitle("Authorization");
                } else {
                    message.setVisible(true);
                    message.setText("You're in base now!");
                }
            } catch (SocketTimeoutException ex) {
                ex.printStackTrace();
            }
        });

        submit.addActionListener(e -> {
            String inputLogin = loginField.getText();
            String inputPass = String.valueOf(passwordField.getPassword());
            try {
                if (client.authorize(inputLogin, inputPass)) {
                    login = inputLogin;
                    password = inputPass;
                    authFrame.setVisible(false);
                    mainFrame.setVisible(true);
                } else {
                    message.setVisible(true);
                    message.setForeground(new Color(183, 24, 26, 244));
                    message.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
                    message.setText("Incorrect login/password");
                }
            } catch (SocketTimeoutException ex) {
                ex.printStackTrace();
            }
        });

        panel.revalidate();
    }

    private void mainFrame() {
        mainFrame = getFrame(800, 500, 3);
        mainFrame.setTitle("MainFrame");
        JPanel panel = new JPanel();
        //panel.setLayout(new BorderLayout());
        mainFrame.add(panel);

        JButton addButton = new JButton(new ReadCommand());
        addButton.setText("Add");
        answer = new JLabel("Ответ будет тут!");
        JButton button = new JButton(new ReadCommand());
        button.setText("Submit");
        panel.add(addButton);
        panel.add(answer);
        panel.add(button);
        panel.revalidate();
    }

    private void studyGroupField() {
        JFrame groupFrame = getFrame(300, 500, JFrame.HIDE_ON_CLOSE);
        groupFrame.setTitle("New Group");

        JPanel panel = new JPanel();
        groupFrame.add(panel);

        JLabel name = new JLabel("Name");
        JLabel x = new JLabel("X coordinate");
        JLabel y = new JLabel("Y coordinate");
        JLabel studentsCount = new JLabel("Students Count");
        JLabel averageMark = new JLabel("Average Mark");
        JLabel formOfEducation = new JLabel("Form of Education");
        JLabel semester = new JLabel("Semester");
        JLabel adminName = new JLabel("Admin Name");
        JLabel weight = new JLabel("Weight");
        JLabel passportID = new JLabel("PassportID");
        JLabel eyeColor = new JLabel("Eye color");
        JLabel country = new JLabel("Country");

        JTextField nameField = new JTextField(20);
        JTextField xField = new JTextField(20);
        JTextField yField = new JTextField(20);
        JTextField studentsCountField = new JTextField(20);
        JTextField averageMarkField = new JTextField(20);
        JTextField adminNameField = new JTextField(20);
        JTextField weightField = new JTextField(20);
        JTextField passportIdField = new JTextField(20);

        JRadioButton form1 = new JRadioButton("DISTANCE_EDUCATION");
        JRadioButton form2 = new JRadioButton("FULL_TIME_EDUCATION");
        JRadioButton form3 = new JRadioButton("EVENING_CLASSES");
        JRadioButton formNone = new JRadioButton("Not defined");
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
        JRadioButton semesterNone = new JRadioButton("Not defined");
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

        JButton yes = new JButton("Submit");
        panel.add(yes);

        panel.revalidate();

        yes.addActionListener(e -> {

            String form = null;
            if (form1.isSelected()) form = form1.getText();
            if (form2.isSelected()) form = form2.getText();
            if (form3.isSelected()) form = form3.getText();

            String sem = null;
            if (semester1.isSelected()) sem = semester1.getText();
            if (semester2.isSelected()) sem = semester2.getText();
            if (semester3.isSelected()) sem = semester3.getText();
            if (semester4.isSelected()) sem = semester4.getText();

            String color = null;
            if (eyeColor1.isSelected()) color = eyeColor1.getText();
            if (eyeColor2.isSelected()) color = eyeColor1.getText();
            if (eyeColor3.isSelected()) color = eyeColor1.getText();
            if (eyeColor4.isSelected()) color = eyeColor1.getText();

            String nation = null;
            if (country1.isSelected()) nation = country1.getText();
            if (country2.isSelected()) nation = country2.getText();
            if (country3.isSelected()) nation = country3.getText();
            if (country4.isSelected()) nation = country4.getText();

            StudyGroup group = null;
            if (interact.checkField(nameField, s -> !s.isEmpty()) &
            interact.checkField(xField, s -> s.matches("-?\\d{1,10}")) &
            interact.checkField(yField, s -> s.matches("-?\\d{1,10}")) &
            interact.checkField(studentsCountField, s -> s.matches("\\d{1,10}")) &
            interact.checkField(averageMarkField, s -> s.matches("\\d{0,10}\\.?\\d{1,10}")) &
            interact.checkField(adminNameField, s -> !s.isEmpty()) &
            interact.checkField(weightField, s -> s.matches("\\d{0,10}\\.?\\d{1,10}")) &
            interact.checkField(passportIdField, s -> (s.isEmpty() || (s.matches(".{5,20}"))))) {
                group = interact.getStudyGroup(nameField.getText(), xField.getText(), yField.getText(),
                        studentsCountField.getText(), averageMarkField.getText(), form, sem, adminNameField.getText(),
                        weightField.getText(), passportIdField.getText(), color, nation);
            }
            try {
                if (group != null) {
                    client.launchCommand("add", group, login, password);
                    groupFrame.setVisible(false);
                }
            } catch (SocketTimeoutException ex) {
                ex.printStackTrace();
            }
        });

    }

    private JFrame getFrame(int width, int height, int closingType) {
        JFrame jFrame = new JFrame() {};
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        Dimension windowSize = new Dimension(width, height);
/*        jFrame.setMaximumSize(windowSize);
        jFrame.setMinimumSize(windowSize);*/
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(closingType);
        jFrame.setBounds(dimension.width/2 - width/2, dimension.height/2 - height/2, width, height);
        return jFrame;
    }


}


