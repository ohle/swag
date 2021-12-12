package de.eudaemon.swag;

import javax.management.NotificationEmitter;

public interface ComponentInfoMBean extends NotificationEmitter {
    PlacementInfo getPlacementInfo(int hashCode);

    SizeInfos getSizeInfos(int hashCode);
}
