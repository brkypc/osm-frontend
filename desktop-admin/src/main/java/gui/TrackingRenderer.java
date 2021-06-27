package gui;

import api.TrackingModel;
import utility.TimeHelper;

import javax.swing.*;
import java.awt.*;

public class TrackingRenderer extends JPanel implements ListCellRenderer<TrackingModel> {

    public TrackingRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends TrackingModel> list, TrackingModel value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel label = new JLabel();
        label.setText("<html>" +
                "Client ID: " + value.getClientid() + "&nbsp;&nbsp;&nbsp;&nbsp;" +
                "Route ID: " + value.getTrackingid() + "<br>" +
                "Time: " + TimeHelper.timestampToDate(value.getTime()) +
                "</html>");

        panel.add(label);

        if (isSelected) {
            panel.setBackground(list.getSelectionBackground());
            panel.setForeground(list.getSelectionForeground());
        } else {
            panel.setBackground(getBackground());
            panel.setForeground(getForeground());
        }

        return panel;
    }
}
