package de.eudaemon.swag;

import java.util.StringJoiner;

import java.io.Serializable;

public class PlacementInfo implements Serializable {

    static final long serialVersionUID = 1L;
    public final Object constraints;
    public final Object index;
    public final StackTraceElement[] stackTrace;

    public PlacementInfo(Object constraints_, int index_, StackTraceElement[] stackTrace_) {
        constraints = constraints_;
        index = index_;
        stackTrace = stackTrace_;
    }

    @Override
    public String toString() {
        StringBuilder trace = new StringBuilder();
        trace.append(stackTrace[0]);
        for (int i = 1; i < stackTrace.length; i++) {
            trace.append("    " + stackTrace[i]);
        }
        return new StringJoiner(", ", PlacementInfo.class.getSimpleName() + "[", "]")
                .add("constraints=" + constraints)
                .add("index=" + index)
                .add("stackTrace:" + trace)
                .toString();
    }
}
