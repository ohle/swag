package de.eudaemon.swag;

import java.util.StringJoiner;

import java.io.Serializable;

import java.awt.Component;
import java.awt.Dimension;

public class SizeInfos implements Serializable {

    private static final long serialVersionUID = 1L;
    public final Dimension actualSize;
    public final SizeInfo minimumSize;
    public final SizeInfo maximumSize;
    public final SizeInfo preferredSize;

    public SizeInfos(
            Dimension actualSize_,
            SizeInfo minimumSize_,
            SizeInfo maximumSize_,
            SizeInfo preferredSize_) {
        actualSize = actualSize_;
        minimumSize = minimumSize_;
        maximumSize = maximumSize_;
        preferredSize = preferredSize_;
    }

    public static SizeInfos forComponent(Component component) {
        return new SizeInfos(
                component.getSize(),
                new SizeInfo(component.getMinimumSize(), component.isMinimumSizeSet()),
                new SizeInfo(component.getMaximumSize(), component.isMinimumSizeSet()),
                new SizeInfo(component.getPreferredSize(), component.isPreferredSizeSet()));
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SizeInfos.class.getSimpleName() + "[", "]")
                .add("actualSize=" + actualSize)
                .add("minimumSize=" + minimumSize)
                .add("maximumSize=" + maximumSize)
                .add("preferredSize=" + preferredSize)
                .toString();
    }
}
