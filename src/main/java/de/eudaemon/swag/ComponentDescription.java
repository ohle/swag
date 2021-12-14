package de.eudaemon.swag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.io.Serializable;

import java.awt.Component;

public class ComponentDescription implements Serializable {
    private static final int serialVersionUID = 1;

    public final String className;
    public final String simpleClassName;
    public final String name;

    public final String text;

    public ComponentDescription(
            String className_, String simpleClassName_, String name_, String text_) {
        className = className_;
        simpleClassName = simpleClassName_;
        name = name_;
        text = text_;
    }

    public static ComponentDescription forComponent(Component component) {
        return new ComponentDescription(
                component.getClass().getName(),
                component.getClass().getSimpleName(),
                component.getName(),
                findTextIfAny(component));
    }

    private static String findTextIfAny(Component component) {
        // There's no generic interface for a component that has a displayed text, but a lot
        // of component subtypes have a getText() method; so find and use it reflectively.
        try {
            Method getText = component.getClass().getMethod("getText");
            Object returnValue = getText.invoke(component);
            return (String) returnValue;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e_) {
            return null;
        }
    }
}
