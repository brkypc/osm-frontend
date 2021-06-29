package gui;

import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import java.util.HashSet;
import java.util.Set;

public class AddressLocationPainter extends WaypointPainter<Waypoint> {

    JXMapKit mapKit;

    public AddressLocationPainter(JXMapKit mapKit) {
        this.mapKit = mapKit;
    }

    @Override
    public Set<Waypoint> getWaypoints() {

        Set<Waypoint> set = new HashSet<>();
        if (mapKit.getAddressLocation() != null)
        {
            set.add(new DefaultWaypoint(mapKit.getAddressLocation()));
        }
        else
        {
            set.add(new DefaultWaypoint(0, 0));
        }
        return set;
    }
}
