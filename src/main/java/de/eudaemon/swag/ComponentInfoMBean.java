package de.eudaemon.swag;

import java.util.Collection;

import java.awt.image.RenderedImage;

import javax.management.NotificationEmitter;

public interface ComponentInfoMBean extends NotificationEmitter {
    ComponentDescription getDescription(int hashCode);

    PlacementInfo getPlacementInfo(int hashCode);

    SizeInfos getSizeInfos(int hashCode);

    int getParent(int hashCode);

    Collection<Integer> getChildren(int hashCode);

    RenderedImage getSnapshot(int hashCode);
}
