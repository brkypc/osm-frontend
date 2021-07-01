package tile;

import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;

public class OneThreadTileFactory extends DefaultTileFactory {

    public OneThreadTileFactory(TileFactoryInfo info) {
        super(info);
        setThreadPoolSize(1);
    }
}
