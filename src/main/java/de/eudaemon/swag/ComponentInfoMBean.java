package de.eudaemon.swag;

import java.util.Collection;

import javax.management.NotificationEmitter;

public interface ComponentInfoMBean extends NotificationEmitter {
    PlacementInfo getPlacementInfo(int hashCode);

    SizeInfos getSizeInfos(int hashCode);

    int getParent(int hashCode);

    Collection<Integer> getChildren(int hashCode);
}
