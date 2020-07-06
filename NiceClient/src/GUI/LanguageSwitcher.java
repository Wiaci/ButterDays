package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

public class LanguageSwitcher {

    ResourceBundle bundle;

    HashMap<JLabel, String> labels;
    HashMap<JButton, String> buttons;
    HashMap<JFrame, String> frames;
    HashMap<JRadioButton, String> radioButtons;
    DefaultTableModel model;

    public LanguageSwitcher() {
        labels = new HashMap<>();
        buttons = new HashMap<>();
        frames = new HashMap<>();
        radioButtons = new HashMap<>();
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void subscribe(JLabel label, String key) {
        labels.put(label, key);
    }

    public void subscribe(JButton button, String key) {
        buttons.put(button, key);
    }

    public void subscribe(JFrame frame, String key) {
        frames.put(frame, key);
    }

    public void subscribe(JRadioButton radioButton, String key) {
        radioButtons.put(radioButton, key);
    }

    public void subscribe(DefaultTableModel model) {
        this.model = model;
    }

    public void setLocale(Locale locale) {
        bundle = ResourceBundle.getBundle("strings", locale);
    }

    public void updateLabels() {
        Set<JLabel> labelSet = labels.keySet();
        labelSet.forEach(s -> s.setText(bundle.getString(labels.get(s))));

        Set<JButton> buttonSet = buttons.keySet();
        buttonSet.forEach(s -> s.setText(bundle.getString(buttons.get(s))));

        Set<JFrame> frameSet = frames.keySet();
        frameSet.forEach(s -> s.setTitle(bundle.getString(frames.get(s))));

        Set<JRadioButton> radioButtonSet = radioButtons.keySet();
        radioButtonSet.forEach(s -> s.setText(bundle.getString(radioButtons.get(s))));

        model.setColumnIdentifiers(new Object[] {bundle.getString("owner"), bundle.getString("id"),
                bundle.getString("name"), "X", "Y", bundle.getString("students_count"),
                bundle.getString("average_mark"), bundle.getString("form_of_education"),
                bundle.getString("semester"), bundle.getString("admin_name"), bundle.getString("weight"),
                bundle.getString("passportID"), bundle.getString("eye_color"), bundle.getString("country")});
    }
}
