package com.ytuce.osmroutetracking;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy;
import org.osmdroid.util.MapTileIndex;

public class MapserverTileSource extends OnlineTileSourceBase {

    public static final String[] baseUrl = {EnvironmentVariables.MAPSERVER_LOCALHOST + "/cgi-bin/mapserv.exe?" +
            "mode=tile&" +
            "template=openlayers&" +
            "layers=all&" +
            "map=" + EnvironmentVariables.MAP_FILE_DIRECTORY + "&" +
            "tilemode=gmap&" +
            "trackingid=1434798379"};

    public MapserverTileSource(String pName, int pZoomMinLevel, int pZoomMaxLevel, int pTileSizePixels, String pImageFilenameEnding, String[] pBaseUrl, String pCopyright) {
        super(pName, pZoomMinLevel, pZoomMaxLevel, pTileSizePixels, pImageFilenameEnding, pBaseUrl, pCopyright);
    }


    @Override
    public String getTileURLString(long pMapTileIndex) {

        String tile = MapTileIndex.getX(pMapTileIndex) +
                "+" + MapTileIndex.getY(pMapTileIndex) +
                "+" + MapTileIndex.getZoom(pMapTileIndex);

        return baseUrl[0] + "&tile=" + tile;
    }
}
