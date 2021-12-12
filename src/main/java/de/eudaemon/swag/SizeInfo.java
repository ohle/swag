package de.eudaemon.swag;

import java.util.StringJoiner;

import java.io.Serializable;

import java.awt.Dimension;

public class SizeInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public final Dimension size;
    public final boolean set;

    public SizeInfo(Dimension size_, boolean set_) {
        size = size_;
        set = set_;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SizeInfo.class.getSimpleName() + "[", "]")
                .add("size=" + size)
                .add("set=" + set)
                .toString();
    }
}
