package gui;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.WMSTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import tile.MapserverTileFactory;

import javax.swing.*;

public class MainScreen {

    private static final String baseUrl =
            "http://localhost/cgi-bin/mapserv.exe?mode=tile&template=openlayers&layers=all&map=D:/ms4w/apps/osm/basemaps/osm-google.map&tilemode=gmap";

    public static void main(String[] args) {

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

        JFrame frame = new JFrame("JXMapviewer2 Example 1");
        frame.getContentPane().add(mapViewer);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
