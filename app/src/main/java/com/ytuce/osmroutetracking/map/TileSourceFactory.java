package com.ytuce.osmroutetracking.map;

import java.util.List;

public class TileSourceFactory {

    public static final String CLIENT_FILTER = "clnt";
    public static final String TRACKING_FILTER = "trck";

    private String type;
    private List<Integer> ids;

    public TileSourceFactory() {
        type = "";
        ids = null;
    }

    public TileSourceFactory setType(String type) {
        this.type = type;
        return this;
    }

    public TileSourceFactory setIds(List<Integer> ids) {
        this.ids = ids;
        return this;
    }

    public MapserverTileSource build() {

        String parameters = "";

        if (ids != null) {
            parameters = MapserverTileSource.idsToString(ids);
        }

        if (type.equals(CLIENT_FILTER)) {
            return new ClientFilterTileSource(
                    "mapserver",
                    0,
                    18,
                    256,
                    ".png",
                    new String[]{ClientFilterTileSource.createBaseUrl(parameters)},
                    "YTU CE",
                    ids);
        } else if (type.equals(TRACKING_FILTER)) {
            return new TrackingFilterTileSource(
                    "mapserver",
                    0,
                    18,
                    256,
                    ".png",
                    new String[]{TrackingFilterTileSource.createBaseUrl(parameters)},
                    "YTU CE",
                    ids
            );
        } else {
            return new MapserverTileSource(
                    "mapserver",
                    0,
                    18,
                    256,
                    ".png",
                    new String[]{MapserverTileSource.createBaseUrl()},
                    "YTU CE"
            );
        }
    }
}
