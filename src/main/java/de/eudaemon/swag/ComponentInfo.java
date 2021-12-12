package de.eudaemon.swag;

import java.util.Map;

import java.awt.Component;

public class ComponentInfo implements ComponentInfoMBean {

    private final Map<Component, StackTraceElement[]> additionTraces;

    public ComponentInfo() {
        additionTraces = SwagAgent.additionTraces;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        System.out.println("ComponentInfo.getStackTrace");
        System.out.println("additionTraces.size() = " + additionTraces.size());
        return additionTraces.values().iterator().next();
    }
}
