package GUI;

import ClientServerCommunicaion.NiceClient;
import ClientServerCommunicaion.sourse.*;
import ClientServerCommunicaion.sourse.enums.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserMagicInteract {

    private static ArrayList<String[]> groupData;

    private DefaultTableModel model;
    private Filter filter;
    private AreaPanel area;
    private HashMap<String, java.awt.Color> colorMap;

    public HashMap<String, java.awt.Color> getColorMap() {
        return colorMap;
    }

    public UserMagicInteract(DefaultTableModel model, AreaPanel area) {
        this.model = model;
        this.area = area;
        groupData = new ArrayList<>();
        filter = new Filter(null, 0);
    }

    public static StudyGroup getStudyGroup(JTextField name, JTextField x, JTextField y, JTextField studentsCount,
                                           JTextField averageMark, JTextField adminName, JTextField weight,
                                           JTextField passportID, String formOfEducation, String semester,
                                           String color, String country) {
        FormOfEducation form = !formOfEducation.isEmpty() ? FormOfEducation.valueOf(formOfEducation) : null;
        Semester sem = !semester.isEmpty() ? Semester.valueOf(semester) : null;
        Color eyeColor = !color.isEmpty() ? Color.valueOf(color) : null;
        Country nation = Country.valueOf(country);
        String passID = passportID.getText().isEmpty() ? null : passportID.getText();
        if (checkField(name, s -> !s.isEmpty()) &
                checkField(x, s -> s.matches("-?\\d{1,10}")) &
                checkField(y, s -> s.matches("-?\\d{1,10}")) &
                checkField(studentsCount, s -> s.matches("\\d{1,10}")) &
                checkField(averageMark, s -> s.matches("\\d{0,10}\\.?\\d{1,10}")) &
                checkField(adminName, s -> !s.isEmpty()) &
                checkField(weight, s -> s.matches("\\d{0,10}\\.?\\d{1,10}")) &
                checkField(passportID, s -> (s.isEmpty() || (s.matches(".{5,20}"))))) {
            return new StudyGroup(name.getText(), new Coordinates(Integer.parseInt(x.getText()), Integer.parseInt(y.getText())),
                    Long.parseLong(studentsCount.getText()), Float.parseFloat(averageMark.getText()), form, sem,
                    new Person(adminName.getText(), Float.parseFloat(weight.getText()), passID,
                            eyeColor, nation));
        }
        return null;
    }

    public void setColorMap(HashMap<String, java.awt.Color> colorMap) {
        this.colorMap = colorMap;
    }

    public Filter getFilter() {
        return filter;
    }

    public HashSet<String> getColumnValues(int column) {
        HashSet<String> set = new HashSet<>();
        groupData.forEach(s -> set.add(s[column]));
        return set;
    }

    public void setFilter(Set<String> values, int column) {
        filter = new Filter(values, column);
        updateTable();
    }

    public void sort(final int columnIndex) {
        groupData.sort(Comparator.comparing(o -> o[columnIndex]));
        updateTable();
    }


    public void add(StudyGroup group, String login) {
        String newGroup = group.anotherToString(login);
        String[] gr = newGroup.split(" ");
        groupData.add(gr);
        /*area.add(new GroupDescriber(gr[1], Double.parseDouble(gr[5]) , Double.parseDouble(gr[3]),
                Double.parseDouble(gr[4]), colorMap.get(login)));*/
        updateTable();
    }

    public void add(String[] group) {
        groupData.add(group);
        updateTable();
    }

    public void addOnArea(String[] group) {
        if (group[0].matches("\\s*")) return;
        area.add(new GroupDescriber(group[1], Double.parseDouble(group[5]) , Double.parseDouble(group[3]),
                Double.parseDouble(group[4]), colorMap.get(group[0])));
    }

    public void update(StudyGroup group, String login) {
        String id = Long.toString(group.getId());
        for (int i = 0; i < groupData.size(); i++) {
            if (groupData.get(i)[1].equals(id)) {
                String[] gr = group.anotherToString(login).split(" ");
                groupData.set(i, gr);
                /*area.add(new GroupDescriber(gr[1], Double.parseDouble(gr[5]) , Double.parseDouble(gr[3]),
                        Double.parseDouble(gr[4]), colorMap.get(login)));*/
                break;
            }
        }
        updateTable();
    }

    private void updateList(String list) {
        String[] notes = list.replaceFirst("^\n", "").split("\n");
        groupData.clear();
        for (String note : notes) {
            groupData.add(note.split(" "));
        }
    }

    private static boolean checkField(JTextField field, Predicate<String> predicate) {
        if (predicate.test(field.getText())) {
            System.out.println(field.getText());
            field.setBackground(java.awt.Color.WHITE);
            return true;
        } else {
            field.setBackground(new java.awt.Color(0xFFBCD1));
            return false;
        }
    }

    public void remove(String id) {
        for (int i = 0; i < groupData.size(); i++) {
            if (groupData.get(i)[1].equals(id)) {
                groupData.remove(i);
                area.remove(id);
                break;
            }
        }
        updateTable();
    }

    public void clear(String owner) {
        for (int i = groupData.size()-1; i >= 0; i--) {
            if (groupData.get(i)[0].equals(owner)) {
                area.remove(groupData.get(i)[1]);
                groupData.remove(i);
            }
        }
        updateTable();
    }

    public static String[] getValuesByID(String id) {
        for (String[] group : groupData) {
            if (group[1].equals(id)) return group;
        }
        return new String[14];
    }

    public void updateTable(String list) {
        updateList(list);
        /*if (((String) model.getValueAt(0,0)).isEmpty()) {
            model.removeRow(0);
            groupData.clear();
        }*/
        if (groupData.get(0)[0].isEmpty()) groupData.remove(0);
        if (groupData.size() == 0) System.out.println("Correct");
        updateTable();
    }

    public void getActualData(NiceClient client, String login, String password) throws SocketTimeoutException {
        String list = client.launchCommand("getList", login, password);
        colorMap = client.getColorMap();
        setColorMap(colorMap);
        updateTable(list);
    }

    public void updateTable() {
        while (model.getRowCount() > 0) {
            model.removeRow(model.getRowCount() - 1);
        }
        groupData.forEach(s -> area.add(new GroupDescriber(s[1], Double.parseDouble(s[5]) , Double.parseDouble(s[3]),
                Double.parseDouble(s[4]), colorMap.get(s[0]))));
        List<String[]> filteredList;
        groupData.forEach(this::addOnArea);
        System.out.println(area.toDraw);
        if (filter.getValues() != null) {
            filteredList = groupData.stream()
                .filter(s -> filter.getValues().contains(s[filter.getColumn()]))
                .collect(Collectors.toList());
        } else {
            filteredList = new ArrayList<>(groupData);
        }
        for (String[] group: filteredList) {
            for (int j = 0; j < group.length; j++) {
                if (group[j].equals("null")) group[j] = "";
            }
            model.addRow(group);
        }

    }
}

class Filter {

    private Set<String> values;
    private int column;

    public Set<String> getValues() {
        return values;
    }

    public int getColumn() {
        return column;
    }

    public Filter(Set<String> values, int column) {
        this.values = values;
        this.column = column;
    }
}

//TODO: help : вывести справку по доступным командам
//TODO: info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
//TODO: execute_script file_name : считать и исполнить скрипт из указанного файла.
//TODO: remove_greater {element} : удалить из коллекции все элементы, превышающие заданный
//TODO: average_of_average_mark : вывести среднее значение поля averageMark для всех элементов коллекции
//TODO: count_less_than_form_of_education formOfEducation :
// вывести количество элементов, значение поля formOfEducation которых меньше заданного
//TODO: print_field_ascending_semester_enum : вывести значения поля semesterEnum всех элементов в порядке возрастания
