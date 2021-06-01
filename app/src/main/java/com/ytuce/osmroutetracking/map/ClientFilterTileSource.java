package com.ytuce.osmroutetracking.map;

import com.ytuce.osmroutetracking.utility.EnvironmentVariables;

import java.util.List;

public class ClientFilterTileSource extends MapserverTileSource {

    private final List<Integer> clientIds;

    ClientFilterTileSource(
            String pName,
            int pZoomMinLevel,
            int pZoomMaxLevel,
            int pTileSizePixels,
            String pImageFilenameEnding,
            String[] pBaseUrl,
            String pCopyright,
            List<Integer> clientIds) {

        super(pName, pZoomMinLevel, pZoomMaxLevel, pTileSizePixels,
                pImageFilenameEnding, pBaseUrl, pCopyright);

        this.clientIds = clientIds;
    }

    public void addClient(int clientId) {
        clientIds.add(clientId);
        updateUrl();
    }

    public void removeClient(int clientId) {

        for (int i = 0; i < clientIds.size(); i++) {
            if (clientIds.get(i) == clientId) {
                clientIds.remove(i);
            }
        }

        updateUrl();
    }

    private void updateUrl() {
        String parameters = idsToString(clientIds);
        setBaseUrl(new String[]{createBaseUrl(parameters)});
    }

    public static String createBaseUrl(String parameters) {
        return EnvironmentVariables.MAPSERVER_LOCALHOST + "/cgi-bin/mapserv.exe?" +
                "mode=tile&" +
                "template=openlayers&" +
                "layers=all&" +
                "map=" + EnvironmentVariables.CLIENT_FILTER_MAP_FILE_DIRECTORY + "&" +
                "tilemode=gmap&" +
                "clnt=" + parameters;
    }
}
