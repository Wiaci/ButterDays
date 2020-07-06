package GUI;

import ClientServerCommunicaion.NiceClient;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketTimeoutException;
import java.util.Locale;
import java.util.ResourceBundle;


public class GuiManager {

    NiceClient client;
    String login;
    String password;
    JFrame mainFrame;
    JTable groupTable;
    DefaultTableModel model;
    JTextField loginField;
    JFrame authFrame;
    JPasswordField passwordField;
    JLabel message;
    LanguageSwitcher languageSwitcher;
    ResourceBundle bundle;


    public GuiManager(NiceClient client) {
        this.client = client;
        client.checkConnection();
        languageSwitcher = new LanguageSwitcher();
        languageSwitcher.setLocale(new Locale("ru"));
        bundle = languageSwitcher.getBundle();

        mainFrame();
        authorize();
        languageSwitcher.updateLabels();
    }

    private void authorize() {
        mainFrame.setVisible(false);
        authFrame = getFrame(200, 150, 3);
        languageSwitcher.subscribe(authFrame, "authorization");

        JButton registration = new JButton();
        languageSwitcher.subscribe(registration, "registration");

        JButton register = new JButton();
        languageSwitcher.subscribe(register, "register");

        JButton submit = new JButton();
        languageSwitcher.subscribe(submit,"submit");
        submit.addActionListener(new TryToEnter());

        JButton goBack = new JButton();
        languageSwitcher.subscribe(goBack, "back");

        loginField = new JTextField(10);
        loginField.addActionListener(new TryToEnter());
        passwordField = new JPasswordField(10);
        passwordField.addActionListener(new TryToEnter());

        JLabel loginLabel = new JLabel();
        languageSwitcher.subscribe(loginLabel, "login");

        JLabel passLabel = new JLabel();
        languageSwitcher.subscribe(passLabel, "password");

        JComboBox<String> language = new JComboBox<>(new String[] {"RU", "LV", "EN_AU", "PT"});

        language.setSelectedItem("RU");

        language.addActionListener(e -> {
            Object selectedItem = language.getSelectedItem();
            if ("RU".equals(selectedItem)) languageSwitcher.setLocale(new Locale("ru"));
            else if ("LV".equals(selectedItem)) languageSwitcher.setLocale(new Locale("lv"));
            else if ("PT".equals(selectedItem)) languageSwitcher.setLocale(new Locale("pt"));
            else if ("EN_AU".equals(selectedItem)) languageSwitcher.setLocale(new Locale("en", "AU"));

            languageSwitcher.updateLabels();
        });

        message = new JLabel();

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
        panel.add(language);
        register.setVisible(false);
        goBack.setVisible(false);
        message.setVisible(false);

        registration.addActionListener(e -> {
            registration.setVisible(false);
            submit.setVisible(false);
            register.setVisible(true);
            goBack.setVisible(true);
            message.setVisible(false);
            authFrame.setTitle(bundle.getString("registration"));
            languageSwitcher.subscribe(authFrame, "registration");
        });

        goBack.addActionListener(e -> {
            registration.setVisible(true);
            submit.setVisible(true);
            register.setVisible(false);
            goBack.setVisible(false);
            message.setVisible(false);
            authFrame.setTitle(bundle.getString("authorization"));
            languageSwitcher.subscribe(authFrame, "authorization");
        });

        register.addActionListener(e -> {
            String inputLogin = loginField.getText();
            String inputPass = String.valueOf(passwordField.getPassword());
            try {
                if (client.register(inputLogin, inputPass)) {
                    message.setVisible(true);
                    message.setForeground(new Color(43, 119, 32, 244));
                    message.setText(bundle.getString("register_is_successful"));
                    languageSwitcher.subscribe(message, "register_is_successful");

                    registration.setVisible(true);
                    submit.setVisible(true);
                    register.setVisible(false);
                    goBack.setVisible(false);
                    authFrame.setTitle(bundle.getString("authorization"));
                    languageSwitcher.subscribe(authFrame, "authorization");
                } else {
                    message.setVisible(true);
                    message.setText(bundle.getString("you_are_in_the_base_now"));
                    languageSwitcher.subscribe(message, "you_are_in_the_base_now");
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
                    message.setText(bundle.getString("incorrect_login_or_password"));
                    languageSwitcher.subscribe(message, "incorrect_login_or_password");
                }
            } catch (SocketTimeoutException ex) {
                ex.printStackTrace();
            }
        });

        panel.revalidate();
    }

    private void mainFrame() {
        mainFrame = getFrame(800, 500, 3);
        languageSwitcher.subscribe(mainFrame, "main_frame");
        mainFrame.setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel(new BorderLayout());
        groupTable = getGroupTable();
        leftPanel.add(new JScrollPane(groupTable), BorderLayout.CENTER);
        leftPanel.setBackground(Color.BLUE);
        mainFrame.add(leftPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.RED);
        mainFrame.add(rightPanel, BorderLayout.EAST);
        ReadGroup readAction = new ReadGroup(client, model, login, password, languageSwitcher, bundle);
        JButton addButton = new JButton(readAction);
        languageSwitcher.subscribe(addButton, "add");

        JButton russian = new JButton();
        languageSwitcher.subscribe(russian, "RU");

        JButton portuguese = new JButton();
        languageSwitcher.subscribe(portuguese, "PT");

        JButton latvian = new JButton();
        languageSwitcher.subscribe(latvian, "LV");

        JButton english = new JButton();
        languageSwitcher.subscribe(english, "EN_AUSTRALIAN");

        russian.addActionListener(e -> {
            languageSwitcher.setLocale(new Locale("ru"));
            languageSwitcher.updateLabels();
        });
        portuguese.addActionListener(e -> {
            languageSwitcher.setLocale(new Locale("pt"));
            languageSwitcher.updateLabels();
        });
        latvian.addActionListener(e -> {
            languageSwitcher.setLocale(new Locale("lv"));
            languageSwitcher.updateLabels();
        });
        english.addActionListener(e -> {
            languageSwitcher.setLocale(new Locale("en", "AU"));
            languageSwitcher.updateLabels();
        });
        rightPanel.add(russian);
        rightPanel.add(portuguese);
        rightPanel.add(latvian);
        rightPanel.add(english);

        rightPanel.add(addButton, BorderLayout.SOUTH);
        rightPanel.revalidate();

        JPanel lowerPanel = new UserPanel();
        lowerPanel.setPreferredSize(new Dimension(lowerPanel.getWidth(), 250));
        mainFrame.add(lowerPanel, BorderLayout.SOUTH);
    }

    private JTable getGroupTable() {
        NewTableModel model = new NewTableModel(new Object[] {"Owner", "ID", "Name", "X", "Y", "Students Count", "Average Mark",
            "Form of Education", "Semester", "Admin Name", "Weight", "PassportID", "Eye Color", "Country"}, 0);
        languageSwitcher.subscribe(model);
        JTable table = new JTable(model);
        RowSorter<NewTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setCellSelectionEnabled(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(30);
        try {
            String list = client.launchCommand("getList", login, password);
            UserMagicInteract.addToTable(list, model);
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        }
        this.model = model;
        table.setFillsViewportHeight(true);
        return table;
    }



    public static JFrame getFrame(int width, int height, int closingType) {
        JFrame jFrame = new JFrame() {};
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(closingType);
        jFrame.setBounds(dimension.width/2 - width/2, dimension.height/2 - height/2, width, height);
        return jFrame;
    }

    class TryToEnter extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
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
                    message.setText(bundle.getString("incorrect_login_or_password"));
                    languageSwitcher.subscribe(message, "incorrect_login_or_password");
                }
            } catch (SocketTimeoutException ex) {
                ex.printStackTrace();
            }
        }
    }


}

class NewTableModel extends DefaultTableModel {
    public NewTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
