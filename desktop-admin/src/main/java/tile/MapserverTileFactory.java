package tile;

import org.jxmapviewer.google.GoogleMapsTileFactoryInfo;

public class MapserverTileFactory extends GoogleMapsTileFactoryInfo {

    public MapserverTileFactory(String name, String baseURL, String key) {
        super(name, baseURL, key);
    }

    @Override
    public String getTileUrl(int x, int y, int zoom) {
        zoom = getTotalMapZoom() - zoom;
        return baseURL + "&tile=" + x + "+" + y + "+" + zoom;
    }
}
