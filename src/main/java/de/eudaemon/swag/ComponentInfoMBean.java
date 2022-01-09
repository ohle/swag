package de.eudaemon.swag;

import java.util.Collection;

import javax.management.NotificationEmitter;

public interface ComponentInfoMBean extends NotificationEmitter {
    Collection<Integer> getRoots();

    ComponentDescription getDescription(int hashCode);

    PlacementInfo getPlacementInfo(int hashCode);

    SizeInfos getSizeInfos(int hashCode);

    Collection<ComponentProperty> getAllProperties(int hashCode);

    Collection<ChildBounds> getVisibleChildrenBounds(int hashCode);

    int getParent(int hashCode);

    int getRoot(int hashCode);

    Collection<Integer> getChildren(int hashCode);

    SerializableImage getSnapshot(int hashCode);
}
