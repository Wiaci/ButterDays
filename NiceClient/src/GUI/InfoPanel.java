package GUI;

import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel {

    private final int NUMBER_OF_STAB_WOUNDS = 28;
    private JLabel[] labels;

    public InfoPanel(LanguageSwitcher languageSwitcher) {

        GridLayout layout = new GridLayout(14, 2);
        layout.setHgap(5);
        setLayout(layout);

        labels = new JLabel[NUMBER_OF_STAB_WOUNDS];
        for (int i = 0; i < NUMBER_OF_STAB_WOUNDS; i++) {
            labels[i] = new JLabel();
        }
        for (int i = 0; i < NUMBER_OF_STAB_WOUNDS / 2; i++) {
            add(labels[i]);
            add(labels[i+14]);
        }

        languageSwitcher.subscribe(labels[0], "owner");
        languageSwitcher.subscribe(labels[1], "id");
        languageSwitcher.subscribe(labels[2], "name");
        labels[3].setText("X");
        labels[4].setText("Y");
        languageSwitcher.subscribe(labels[5], "students_count");
        languageSwitcher.subscribe(labels[6], "average_mark");
        languageSwitcher.subscribe(labels[7], "form_of_education");
        languageSwitcher.subscribe(labels[8], "semester");
        languageSwitcher.subscribe(labels[9], "admin_name");
        languageSwitcher.subscribe(labels[10], "weight");
        languageSwitcher.subscribe(labels[11], "passportID");
        languageSwitcher.subscribe(labels[12], "eye_color");
        languageSwitcher.subscribe(labels[13], "country");
    }

    public void imageValues(String[] values) {
        for (int i = 14; i < NUMBER_OF_STAB_WOUNDS; i++) {
            labels[i].setText(values[i - 14]);
        }
    }
}
