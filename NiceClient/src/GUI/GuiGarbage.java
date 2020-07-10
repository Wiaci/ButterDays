package GUI;

import ClientServerCommunicaion.NiceClient;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.stream.Collectors;


public class GuiGarbage implements Runnable {

    private NiceClient client;
    private static JFrame mainFrame;
    private JFrame authFrame;
    private JFrame groupFrame;

    private String login;
    private String password;
    private JTable groupTable;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JLabel message;
    private static LanguageSwitcher languageSwitcher = new LanguageSwitcher();
    private ResourceBundle bundle;
    private ReadGroup readAction;
    private UserMagicInteract magic;
    private Point point;
    private AreaPanel area;
    private UserPanel userPanel;
    private DefaultTableModel model;
    private JPanel lowerPanel;

    public GuiGarbage(NiceClient client) {
        this.client = client;
    }

    private void authorize() {
        mainFrame.setVisible(false);
        authFrame = getFrame(200, 200, 3);
        authFrame.setResizable(false);
        languageSwitcher.subscribe(authFrame, "authorization");

        JButton registration = new JButton();
        registration.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        languageSwitcher.subscribe(registration, "registration");

        JButton register = new JButton();
        register.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        languageSwitcher.subscribe(register, "register");

        JButton submit = new JButton();
        submit.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        languageSwitcher.subscribe(submit,"submit");
        submit.addActionListener(new TryToEnter());

        JButton goBack = new JButton();
        goBack.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        languageSwitcher.subscribe(goBack, "back");

        loginField = new JTextField(10);
        loginField.addActionListener(new TryToEnter());
        passwordField = new JPasswordField(10);
        passwordField.addActionListener(new TryToEnter());

        JLabel loginLabel = new JLabel();
        loginLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        languageSwitcher.subscribe(loginLabel, "login");

        JLabel passLabel = new JLabel();
        passLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
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

        message = new JLabel(" ");
        message.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

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

        registration.addActionListener(e -> {
            loginField.setBackground(Color.WHITE);
            loginField.setBackground(Color.WHITE);
            registration.setVisible(false);
            submit.setVisible(false);
            register.setVisible(true);
            goBack.setVisible(true);
            authFrame.setTitle(bundle.getString("registration"));
            languageSwitcher.subscribe(authFrame, "registration");
        });

        goBack.addActionListener(e -> {
            registration.setVisible(true);
            submit.setVisible(true);
            register.setVisible(false);
            goBack.setVisible(false);
            message.setText(" ");
            authFrame.setTitle(bundle.getString("authorization"));
            languageSwitcher.subscribe(authFrame, "authorization");
        });

        register.addActionListener(e -> {
            String inputLogin = loginField.getText();
            String inputPass = String.valueOf(passwordField.getPassword());
            try {
                if (!inputLogin.matches("\\s*") && !inputPass.matches("\\s*") &&
                        client.register(inputLogin, inputPass)) {
                    message.setForeground(new Color(43, 119, 32, 244));
                    message.setText(bundle.getString("register_is_successful"));
                    languageSwitcher.subscribe(message, "register_is_successful");

                    registration.setVisible(true);
                    submit.setVisible(true);
                    register.setVisible(false);
                    goBack.setVisible(false);
                    authFrame.setTitle(bundle.getString("authorization"));
                    languageSwitcher.subscribe(authFrame, "authorization");
                } else if (!inputLogin.matches("\\s*") && !inputPass.matches("\\s*")) {
                    message.setText(bundle.getString("you_are_in_the_base_now"));
                    languageSwitcher.subscribe(message, "you_are_in_the_base_now");
                }
            } catch (SocketTimeoutException ex) {
                connectionLost();
            }
            authFrame.pack();
        });

        submit.addActionListener(e -> {
            String inputLogin = loginField.getText();
            String inputPass = String.valueOf(passwordField.getPassword());
            try {
                if (client.authorize(inputLogin, inputPass)) {
                    login = inputLogin;
                    password = inputPass;
                     magic = new UserMagicInteract(model, area, client, login, password);
                    readAction.setLoginAndPassword(login, password, magic);
                    magic.getActualData();
                    area.setInformation(groupTable, login, readAction, groupFrame, mainFrame);
                    ButtonPanel rightPanel = new ButtonPanel(languageSwitcher,
                            readAction, magic, client, login, password, mainFrame);
                    lowerPanel.add(rightPanel, BorderLayout.EAST);
                    userPanel.setColor(magic.getColorMap().get(login));
                    userPanel.setNewName(login);
                    userPanel.repaint();
                } else {
                    message.setForeground(new Color(183, 24, 26, 244));
                    message.setText(bundle.getString("incorrect_login_or_password"));
                    languageSwitcher.subscribe(message, "incorrect_login_or_password");
                }
            } catch (SocketTimeoutException ex) {
                connectionLost();
            }
        });

        panel.revalidate();
    }

    private void mainFrame() {

        mainFrame = getFrame(1350, 727, 3);
        mainFrame.setLocation(0,0);

        mainFrame.setVisible(false);
        mainFrame.setResizable(false);
        languageSwitcher.subscribe(mainFrame, "main_frame");
        mainFrame.setLayout(new BorderLayout());

        lowerPanel = new JPanel();
        lowerPanel.setPreferredSize(new Dimension(lowerPanel.getWidth(), 400));
        lowerPanel.setLayout(new BorderLayout());
        mainFrame.add(lowerPanel, BorderLayout.CENTER);

        readAction = new ReadGroup(client, languageSwitcher, bundle);
        groupFrame = readAction.getGroupFrame();

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

        userPanel = new UserPanel();
        userPanel.setLayout(new BorderLayout());
        userPanel.setBackground(Color.WHITE);
        userPanel.setPreferredSize(new Dimension(240, 400));
        lowerPanel.add(userPanel, BorderLayout.WEST);

        JPanel languagePanel = new JPanel(new GridLayout(2,2));
        languagePanel.add(russian);
        languagePanel.add(latvian);
        languagePanel.add(portuguese);
        languagePanel.add(english);
        userPanel.add(languagePanel, BorderLayout.SOUTH);


        area = new AreaPanel(languageSwitcher);
        lowerPanel.add(area, BorderLayout.CENTER);

        JPanel upperPanel = new JPanel(new BorderLayout());
        groupTable = getGroupTable();
        JScrollPane scrollPane = new JScrollPane(groupTable);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getWidth(), 350));
        upperPanel.add(scrollPane);
        upperPanel.setBackground(Color.BLUE);
        mainFrame.add(upperPanel, BorderLayout.NORTH);
    }

    public JTable getGroupTable() {
        model = new NewTableModel(new Object[]{"Owner", "ID", "Name", "X", "Y", "Students Count", "Average Mark",
                "Form of Education", "Semester", "Admin Name", "Weight", "PassportID", "Eye Color", "Country"}, 0);
        languageSwitcher.subscribe(model);
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setCellSelectionEnabled(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItemUpdate = new JMenuItem();
        languageSwitcher.subscribe(menuItemUpdate, "update_group");

        menuItemUpdate.addActionListener(s -> {
            int selectedRow = table.getSelectedRow();
            String user = (String) table.getValueAt(selectedRow, 0);
            if (user.equals(login)) {
                readAction.setUpdateMode(new String[] {(String) table.getValueAt(selectedRow, 2),
                                (String) table.getValueAt(selectedRow, 3),
                                (String) table.getValueAt(selectedRow, 4),
                                (String) table.getValueAt(selectedRow, 5),
                                (String) table.getValueAt(selectedRow, 6),
                                (String) table.getValueAt(selectedRow, 9),
                                (String) table.getValueAt(selectedRow, 10),
                                (String) table.getValueAt(selectedRow, 11),
                                (String) table.getValueAt(selectedRow, 7),
                                (String) table.getValueAt(selectedRow, 8),
                                (String) table.getValueAt(selectedRow, 12),
                                (String) table.getValueAt(selectedRow, 13)},
                        (String) table.getValueAt(selectedRow, 1));
                groupFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(mainFrame, languageSwitcher.getBundle().getString("no_access"),
                        languageSwitcher.getBundle().getString("warning"), JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem menuItemRemove = new JMenuItem();
        languageSwitcher.subscribe(menuItemRemove, "remove_group");
        menuItemRemove.addActionListener(s -> {
            int selectedRow = table.getSelectedRow();
            String user = (String) table.getValueAt(selectedRow, 0);
            if (user.equals(login)) {
                try {
                    client.launchCommand("remove_by_id " + table.getValueAt(selectedRow, 1),
                            login, password);
                    magic.remove((String) table.getValueAt(selectedRow, 1));
                } catch (SocketTimeoutException e) {
                    connectionLost();
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, languageSwitcher.getBundle().getString("no_access"),
                        languageSwitcher.getBundle().getString("warning"), JOptionPane.WARNING_MESSAGE);
            }
        });

        JMenuItem menuItemClear = new JMenuItem();
        languageSwitcher.subscribe(menuItemClear, "remove_all");

        menuItemClear.addActionListener(s -> {
            int answer = JOptionPane.showConfirmDialog(
                    mainFrame, languageSwitcher.getBundle().getString("sure"), "", JOptionPane.YES_NO_OPTION);
            if (answer == 0) {
                try {
                    client.launchCommand("clear", login, password);
                    magic.clear(login);
                } catch (SocketTimeoutException e) {
                    connectionLost();
                }
            }
        });

        popupMenu.add(menuItemUpdate);
        popupMenu.add(menuItemRemove);
        popupMenu.add(menuItemClear);

        table.setComponentPopupMenu(popupMenu);

        JPopupMenu headerPopup = new JPopupMenu();

        JMenuItem sort = new JMenuItem();
        languageSwitcher.subscribe(sort, "sort");

        sort.addActionListener(e -> {
            int column = table.getTableHeader().columnAtPoint(point);
            magic.sort(column);
        });

        JMenuItem filter = new JMenuItem();
        languageSwitcher.subscribe(filter, "filter...");

        filter.addActionListener(e -> {
            int column = table.getTableHeader().columnAtPoint(point);
            filter(column);
        });

        headerPopup.add(sort);
        headerPopup.add(filter);

        table.getTableHeader().addMouseListener(new TableHeaderMouseListener(point));
        table.getTableHeader().setComponentPopupMenu(headerPopup);

        table.addMouseListener(new TableMouseListener(table));
        table.setFillsViewportHeight(true);
        return table;
    }

    public void filter(int column) {
        JFrame filterFrame = getFrame(200, 500, JFrame.HIDE_ON_CLOSE);
        filterFrame.setResizable(false);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        filterFrame.add(panel);
        filterFrame.setTitle(languageSwitcher.getBundle().getString("filter"));
        HashSet<String> values = magic.getColumnValues(column);
        ArrayList<JCheckBox> checkBoxes = new ArrayList<>();

        values.forEach(s -> checkBoxes.add(new JCheckBox(s)));

        Filter filter = magic.getFilter();

        if (filter.getValues() != null && column == filter.getColumn()) {
            checkBoxes.stream()
                    .filter(s -> filter.getValues().contains(s.getText()))
                    .forEach(s -> s.setSelected(true));
        }

        checkBoxes.stream()
                .sorted(Comparator.comparing(AbstractButton::getText))
                .forEach(panel::add);
        JCheckBox all = new JCheckBox(languageSwitcher.getBundle().getString("all"));
        panel.add(all);

        JButton submit = new JButton(languageSwitcher.getBundle().getString("submit"));
        panel.add(submit);

        filterFrame.pack();

        submit.addActionListener(e -> {
            if (all.isSelected()) {
                magic.setFilter(null, 0);
            } else {
                magic.setFilter(checkBoxes.stream()
                        .filter(AbstractButton::isSelected)
                        .map(AbstractButton::getText)
                        .collect(Collectors.toSet()), column);
            }
        });
    }

    public static JFrame getFrame(int width, int height, int closingType) {
        JFrame jFrame = new JFrame() {};
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        //jFrame.setResizable(false);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(closingType);
        jFrame.setBounds(dimension.width/2 - width/2, dimension.height/2 - height/2, width, height);
        return jFrame;
    }

    public static void connectionLost() {
        JOptionPane.showMessageDialog(mainFrame, languageSwitcher.getBundle().getString("connection_lost"),
                languageSwitcher.getBundle().getString("warning"), JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void run() {
        client.checkConnection();
        languageSwitcher.setLocale(new Locale("ru"));
        bundle = languageSwitcher.getBundle();
        point = new Point();

        mainFrame();
        authorize();
        languageSwitcher.updateLabels();
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
                connectionLost();
            }
        }
    }
}

class TableMouseListener extends MouseAdapter {

    private JTable table;

    public TableMouseListener(JTable table) {
        this.table = table;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        Point point = event.getPoint();
        int currentRow = table.rowAtPoint(point);
        try {
            table.setRowSelectionInterval(currentRow, currentRow);
        } catch (IllegalArgumentException e) {
            System.out.println("Кликнули мимо таблицы. И ничего плохого в этом нет!");
        }
    }
}

class TableHeaderMouseListener extends MouseAdapter {

    Point point;

    public TableHeaderMouseListener(Point point) {
        this.point = point;
    }

    public void mousePressed(MouseEvent event) {
        point.setLocation(event.getPoint());
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

