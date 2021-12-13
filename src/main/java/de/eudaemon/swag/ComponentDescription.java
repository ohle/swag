package de.eudaemon.swag;

import java.io.Serializable;

import java.awt.Component;

public class ComponentDescription implements Serializable {
    private static final int serialVersionUID = 1;

    public final String className;
    public final String simpleClassName;
    public final String name;

    public ComponentDescription(String className_, String simpleClassName_, String name_) {
        className = className_;
        simpleClassName = simpleClassName_;
        name = name_;
    }

    public static ComponentDescription forComponent(Component component) {
        return new ComponentDescription(
                component.getClass().getName(),
                component.getClass().getSimpleName(),
                component.getName());
    }
}
