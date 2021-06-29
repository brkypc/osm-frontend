package gui;

import javax.swing.*;
import java.util.List;

public class TrackingJList<T> extends JList<T> {

    public void setModel(List<T> resultSet) {

        DefaultListModel<T> defaultListModel = new DefaultListModel<>();

        for (T trackingModel : resultSet) {
            defaultListModel.addElement(trackingModel);
        }

        setModel(defaultListModel);
    }
}
