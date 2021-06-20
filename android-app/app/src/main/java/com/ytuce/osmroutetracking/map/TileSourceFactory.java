package com.ytuce.osmroutetracking.map;

import android.util.Log;

import java.util.List;

public class TileSourceFactory {

    public static final String CLIENT_FILTER = "clnt";
    public static final String TRACKING_FILTER = "trck";

    public TileSourceFactory() {
    }

    public MapserverTileSource build(String type, List<Integer> ids) {

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

    public MapserverTileSource build() {
        return build("", null);
    }

    public MapserverTileSource build(String type) {
        return build(type, null);
    }
}
