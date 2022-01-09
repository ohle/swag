package de.eudaemon.swag;

import java.io.Serializable;

import java.awt.Component;
import java.awt.Rectangle;

public class ChildBounds implements Serializable {

    private static final long serialVersionUID = 1L;
    public final int childId;
    public final Rectangle bounds;

    public ChildBounds(int childId_, Rectangle bounds_) {
        childId = childId_;
        bounds = bounds_;
    }

    public static ChildBounds fromComponent(Component component) {
        return new ChildBounds(component.hashCode(), component.getBounds());
    }
}
