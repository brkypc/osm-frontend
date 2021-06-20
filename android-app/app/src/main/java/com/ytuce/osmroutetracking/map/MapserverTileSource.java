package com.ytuce.osmroutetracking.map;

import com.ytuce.osmroutetracking.utility.EnvironmentVariables;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.MapTileIndex;

import java.util.List;

public class MapserverTileSource extends OnlineTileSourceBase {

    private String[] baseUrl;

    MapserverTileSource(String pName, int pZoomMinLevel, int pZoomMaxLevel, int pTileSizePixels, String pImageFilenameEnding, String[] pBaseUrl, String pCopyright) {
        super(pName, pZoomMinLevel, pZoomMaxLevel, pTileSizePixels, pImageFilenameEnding, pBaseUrl, pCopyright);

        baseUrl = new String[2];
        baseUrl[0] = pBaseUrl[0];
    }


    @Override
    public String getTileURLString(long pMapTileIndex) {

        String tile = MapTileIndex.getX(pMapTileIndex) +
                "+" + MapTileIndex.getY(pMapTileIndex) +
                "+" + MapTileIndex.getZoom(pMapTileIndex);

        return baseUrl[0] + "&tile=" + tile;
    }

    public static String idsToString(List<Integer> ids) {

        StringBuilder commaSeparated = new StringBuilder();

        for (Integer id : ids) {
            commaSeparated.append(id).append(",");
        }

        if (ids.size() > 0) {
            commaSeparated.deleteCharAt(commaSeparated.length() - 1);
        }

        return commaSeparated.toString();
    }

    public void setBaseUrl(String[] baseUrl) {
        this.baseUrl = baseUrl;
    }

    public static String createBaseUrl() {
        return EnvironmentVariables.MAPSERVER_LOCALHOST + "/cgi-bin/mapserv.exe?" +
                "mode=tile&" +
                "template=openlayers&" +
                "layers=all&" +
                "map=" + EnvironmentVariables.MAP_FILE_DIRECTORY + "&" +
                "tilemode=gmap";
    }
}
