package gui;

import api.Consumer;
import api.TrackingModel;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import tile.MapserverTileFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainScreen {

    private static final String baseUrl =
            "http://localhost/cgi-bin/mapserv.exe?mode=tile&template=openlayers&layers=all&map=D:/ms4w/apps/osm/basemaps/osm-google.map&tilemode=gmap";

    private Consumer consumer;

    public MainScreen() {
        consumer = new Consumer();
    }

    public static void main(String[] args) {
        MainScreen screen = new MainScreen();
        screen.show();
    }

    private void setMapView(JFrame frame) {
        JXMapViewer mapViewer = new JXMapViewer();

        // TileFactoryInfo tileFactoryInfo = new OSMTileFactoryInfo("mapserver", baseUrl);
        TileFactoryInfo tileFactoryInfo = new MapserverTileFactory("asd", baseUrl, "1");
        // ((WMSTileFactoryInfo) tileFactoryInfo).setTileFormat("png");
        DefaultTileFactory defaultTileFactory = new DefaultTileFactory(tileFactoryInfo);
        mapViewer.setTileFactory(defaultTileFactory);

        defaultTileFactory.setThreadPoolSize(8);

        GeoPosition istanbul = new GeoPosition(41.015137, 28.979530);

        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(istanbul);
        mapViewer.setPanEnabled(true);
        mapViewer.setSize(800, 600);
        mapViewer.setPreferredSize(new Dimension(800, 600));

        frame.add(mapViewer, BorderLayout.EAST);
    }

    private void setSideMenu(JFrame frame) {
        JPanel sideMenuPanel = new JPanel();
        sideMenuPanel.setSize(400, 600);
        sideMenuPanel.setMaximumSize(new Dimension(400, 600));
        sideMenuPanel.setPreferredSize(new Dimension(400, 600));
        sideMenuPanel.setLayout(new BoxLayout(sideMenuPanel, BoxLayout.Y_AXIS));

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setSize(400, 130);
        controlPanel.setMaximumSize(new Dimension(400, 130));
        controlPanel.setPreferredSize(new Dimension(400, 130));

        JList<TrackingModel> list = new JList<>();
        list.setCellRenderer(new TrackingRenderer());
        list.setSize(370, 470);
        list.setMaximumSize(new Dimension(370, 470));
        list.setPreferredSize(new Dimension(370, 470));

        JButton showAllRoutes = new JButton("Tüm rotalar");
        JButton showUserRoutes = new JButton("Bir kişinin rotaları");
        JButton showRoutesNearToPoint = new JButton("Bir noktaya en yakın rotalar");
        JButton showRoutesNearToPointTimeInterval = new JButton("Zaman aralığında noktaya en yakın rotalar");

        showUserRoutes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog("Client ID:");
                int clientId = Integer.parseInt(input);

                List<TrackingModel> resultSet = consumer.getPointsOfClient(clientId);

                DefaultListModel<TrackingModel> defaultListModel = new DefaultListModel<>();

                for (TrackingModel trackingModel : resultSet) {
                    defaultListModel.addElement(trackingModel);
                    System.out.println(trackingModel.toString());
                }
                list.setModel(defaultListModel);
            }
        });

        controlPanel.add(showAllRoutes);
        controlPanel.add(showUserRoutes);
        controlPanel.add(showRoutesNearToPoint);
        controlPanel.add(showRoutesNearToPointTimeInterval);

        sideMenuPanel.add(controlPanel);
        sideMenuPanel.add(new JScrollPane(list));

        frame.add(sideMenuPanel, BorderLayout.WEST);
    }

    public void show() {
        JFrame frame = new JFrame("JXMapviewer2 Example 1");
        frame.setSize(1200, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMapView(frame);
        setSideMenu(frame);

        frame.setVisible(true);
    }
}
