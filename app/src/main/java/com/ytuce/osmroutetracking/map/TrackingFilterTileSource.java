package com.ytuce.osmroutetracking.map;

import com.ytuce.osmroutetracking.utility.EnvironmentVariables;

import java.util.List;

public class TrackingFilterTileSource extends MapserverTileSource {

    private final List<Integer> trackingIds;

    TrackingFilterTileSource(
            String pName,
            int pZoomMinLevel,
            int pZoomMaxLevel,
            int pTileSizePixels,
            String pImageFilenameEnding,
            String[] pBaseUrl,
            String pCopyright,
            List<Integer> trackingIds) {

        super(pName, pZoomMinLevel, pZoomMaxLevel, pTileSizePixels,
                pImageFilenameEnding, pBaseUrl, pCopyright);

        this.trackingIds = trackingIds;
    }

    public void addClient(int trackingId) {
        trackingIds.add(trackingId);
        updateUrl();
    }

    public void removeClient(int trackingId) {

        for (int i = 0; i < trackingIds.size(); i++) {
            if (trackingIds.get(i) == trackingId) {
                trackingIds.remove(i);
            }
        }

        updateUrl();
    }

    private void updateUrl() {
        String parameters = idsToString(trackingIds);
        setBaseUrl(new String[]{createBaseUrl(parameters)});
    }

    public static String createBaseUrl(String parameters) {
        return EnvironmentVariables.MAPSERVER_LOCALHOST + "/cgi-bin/mapserv.exe?" +
                "mode=tile&" +
                "template=openlayers&" +
                "layers=all&" +
                "map=" + EnvironmentVariables.TRACKING_FILTER_MAP_FILE_DIRECTORY + "&" +
                "tilemode=gmap&" +
                "trck=" + parameters;
    }
}
