package tile;

import java.util.ArrayList;
import java.util.List;

public class TileFactoryInfoBuilder {

    public static final int TYPE_DEFAULT = 100;
    public static final int TYPE_FILTER_CLIENT = 101;
    public static final int TYPE_FILTER_TRACKING = 102;

    private static final String baseUrl = "http://localhost/cgi-bin/mapserv.exe?mode=tile&template=openlayers&layers=all&tilemode=gmap";
    private static final String defaultMapFile = "map=D:/ms4w/apps/osm/basemaps/osm-google.map";
    private static final String clientFilterMapFile = "map=D:/ms4w/apps/osm/basemaps/osm-google-filter-client.map";
    private static final String trackingFilterMapFile = "map=D:/ms4w/apps/osm/basemaps/osm-google-filter-tracking.map";

    private int type;
    private List<Integer> parameters;

    public TileFactoryInfoBuilder() {
        type = TYPE_DEFAULT;
        parameters = new ArrayList<>();
    }

    public TileFactoryInfoBuilder setType(int type) {
        this.type = type;
        return this;
    }

    public TileFactoryInfoBuilder setParameters(List<Integer> parameters) {
        this.parameters = parameters;
        return this;
    }

    public MapserverTileFactory build() {
        StringBuilder url = new StringBuilder(baseUrl);
        switch (type) {
            case TYPE_DEFAULT:
                url.append('&').append(defaultMapFile);
                break;
            case TYPE_FILTER_CLIENT:
                url.append('&').append(clientFilterMapFile);
                if (parameters.size() > 0) {
                    url.append("&clnt=");
                    for (Integer parameter : parameters) {
                        url.append(parameter).append(',');
                    }
                    url.append(0);
                }
                break;
            case TYPE_FILTER_TRACKING:
                url.append('&').append(trackingFilterMapFile);
                if (parameters.size() > 0) {
                    url.append("&trck=");
                    for (Integer parameter : parameters) {
                        url.append(parameter).append(',');
                    }
                    url.append(0);
                }
                break;
        }
        return new MapserverTileFactory("info", url.toString(), "1");
    }

}
