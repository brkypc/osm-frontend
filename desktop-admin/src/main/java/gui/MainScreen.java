package gui;

import api.Consumer;
import api.TrackingModel;
import javafx.util.Pair;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.input.MapClickListener;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;
import tile.MapserverTileFactory;
import tile.TileFactoryInfoBuilder;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;

public class MainScreen {

    private final Consumer consumer;
    private JXMapKit mapKit;

    private static boolean timeSelection = false;
    private static Pair<Long, Long> timeInterval;

    public MainScreen() {
        consumer = new Consumer();
    }

    public static void main(String[] args) {
        MainScreen screen = new MainScreen();
        screen.show();
    }

    private void setMapView(JFrame frame) {

        mapKit = new JXMapKit() {
            @Override
            public void setTileFactory(TileFactory fact) {

                int zoom = getMainMap().getZoom();
                GeoPosition currentPosition = getCenterPosition();

                super.setTileFactory(fact);

                setZoom(zoom);
                setCenterPosition(currentPosition);
            }
        };
        TileFactoryInfo info = new TileFactoryInfoBuilder().setType(TileFactoryInfoBuilder.TYPE_DEFAULT).build();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapKit.setTileFactory(tileFactory);

        GeoPosition istanbul = new GeoPosition(41.015137, 28.979530);

        mapKit.setZoom(11);
        mapKit.setCenterPosition(istanbul);

        mapKit.setSize(800, 600);
        mapKit.setPreferredSize(new Dimension(800, 600));

        frame.add(mapKit, BorderLayout.EAST);
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

        TrackingJList<TrackingModel> list = new TrackingJList<>();
        list.setCellRenderer(new TrackingRenderer());
        list.setSize(370, 470);
        list.setMaximumSize(new Dimension(370, 470));
        list.setPreferredSize(new Dimension(370, 470));

        JButton showAllRoutes = new JButton("Tüm rotalar");
        JButton showUserRoutes = new JButton("Bir kişinin rotaları");
        JButton showRoutesNearToPoint = new JButton("Bir noktaya en yakın rotalar");
        JButton showRoutesNearToPointTimeInterval = new JButton("Zaman aralığında noktaya en yakın rotalar");

        ListSelectionListener trackingSelectionListener = e -> {

            List<TrackingModel> selectedItems = list.getSelectedValuesList();
            List<Integer> parameters = new ArrayList<>();
            for (TrackingModel selectedItem : selectedItems) {
                parameters.add(selectedItem.getTrackingid());
            }

            TileFactoryInfo info = new TileFactoryInfoBuilder().setType(TileFactoryInfoBuilder.TYPE_FILTER_TRACKING)
                    .setParameters(parameters).build();
            DefaultTileFactory tileFactory = new DefaultTileFactory(info);
            mapKit.setTileFactory(tileFactory);
        };

        ListSelectionListener clientSelectionListener = e -> {

            List<TrackingModel> selectedItems = list.getSelectedValuesList();
            List<Integer> parameters = new ArrayList<>();
            for (TrackingModel selectedItem : selectedItems) {
                parameters.add(selectedItem.getClientid());
            }

            TileFactoryInfo info = new TileFactoryInfoBuilder().setType(TileFactoryInfoBuilder.TYPE_FILTER_CLIENT)
                    .setParameters(parameters).build();
            DefaultTileFactory tileFactory = new DefaultTileFactory(info);
            mapKit.setTileFactory(tileFactory);
        };

        MouseAdapter pointSelectionListener = new MapClickListener(mapKit.getMainMap()) {
            @Override
            public void mapClicked(GeoPosition location) {
                System.out.println("clicked");

                mapKit.setAddressLocation(location);
                mapKit.setAddressLocationShown(true);

                if (timeSelection)  {
                    if (timeInterval.getKey() != -1 && timeInterval.getValue() != -1) {
                        list.setModel(consumer.getRoutesCloseToPointTimeInterval(
                                location.getLatitude(), location.getLongitude(),
                                timeInterval.getKey(), timeInterval.getValue()));
                    }
                } else {
                    list.setModel(consumer.getRoutesCloseToPoint(location.getLatitude(), location.getLongitude()));
                }
            }
        };

        showAllRoutes.addActionListener(e -> {

            timeSelection = false;

            // empty list
            DefaultListModel<TrackingModel> defaultListModel = new DefaultListModel<>();
            list.setModel(defaultListModel);

            list.removeListSelectionListener(clientSelectionListener);
            list.removeListSelectionListener(trackingSelectionListener);

            TileFactoryInfo info  = new TileFactoryInfoBuilder().setType(TileFactoryInfoBuilder.TYPE_DEFAULT).build();
            DefaultTileFactory tileFactory = new DefaultTileFactory(info);
            mapKit.setTileFactory(tileFactory);

            mapKit.getMainMap().removeMouseListener(pointSelectionListener);
            mapKit.setAddressLocationShown(false);
        });

        showUserRoutes.addActionListener(e -> {

            timeSelection = false;

            String input = JOptionPane.showInputDialog("Client ID:");
            int clientId = Integer.parseInt(input);

            list.setModel(consumer.getPointsOfClient(clientId));

            // TODO get clicked trackings and show them on the map
            list.removeListSelectionListener(clientSelectionListener);
            list.addListSelectionListener(trackingSelectionListener);

            TileFactoryInfo info = new TileFactoryInfoBuilder().setType(TileFactoryInfoBuilder.TYPE_FILTER_TRACKING)
                    .build();
            DefaultTileFactory tileFactory = new DefaultTileFactory(info);
            mapKit.setTileFactory(tileFactory);

            mapKit.getMainMap().removeMouseListener(pointSelectionListener);
            mapKit.setAddressLocationShown(false);
        });

        showRoutesNearToPoint.addActionListener(e -> {

            timeSelection = false;

            JOptionPane.showMessageDialog(frame, "Haritadan seçim yapın");

            list.removeListSelectionListener(clientSelectionListener);
            list.addListSelectionListener(trackingSelectionListener);

            TileFactoryInfo info = new TileFactoryInfoBuilder().setType(TileFactoryInfoBuilder.TYPE_FILTER_TRACKING)
                    .build();
            DefaultTileFactory tileFactory = new DefaultTileFactory(info);
            mapKit.setTileFactory(tileFactory);

            mapKit.getMainMap().addMouseListener(pointSelectionListener);
        });

        showRoutesNearToPointTimeInterval.addActionListener(e -> {

            timeSelection = true;

            timeInterval = TimeIntervalSelectionOptionPane.show();

            if (timeInterval.getKey() != -1 && timeInterval.getValue() != -1) {
                list.removeListSelectionListener(clientSelectionListener);
                list.addListSelectionListener(trackingSelectionListener);

                TileFactoryInfo info = new TileFactoryInfoBuilder().setType(TileFactoryInfoBuilder.TYPE_FILTER_TRACKING)
                        .build();
                DefaultTileFactory tileFactory = new DefaultTileFactory(info);
                mapKit.setTileFactory(tileFactory);

                mapKit.getMainMap().addMouseListener(pointSelectionListener);
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
