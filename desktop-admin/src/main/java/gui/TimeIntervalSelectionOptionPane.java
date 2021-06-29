package gui;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeIntervalSelectionOptionPane {

    public static Pair<Long, Long> show() {

        JTextField startTimeTextField = new JTextField();
        JTextField endTimeTextField = new JTextField();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        panel.add(new JLabel("Başlangıç zamanı: "));
        panel.add(startTimeTextField);
        panel.add(new JLabel("Bitiş zamanı: "));
        panel.add(endTimeTextField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Girdi formatı: yyyy-MM-dd hh:mm",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            try {
                Date startDate = dateFormat.parse(startTimeTextField.getText());
                Date endDate = dateFormat.parse(endTimeTextField.getText());

                return new Pair<>(startDate.getTime(), endDate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return new Pair<>(-1L, -1L);
    }

}
