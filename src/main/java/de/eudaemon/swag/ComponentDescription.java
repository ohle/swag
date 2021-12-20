package de.eudaemon.swag;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.io.Serializable;

import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.List;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Window;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;

public class ComponentDescription implements Serializable {
    private static final long serialVersionUID = 2L;

    public final String className;
    public final String simpleClassName;
    public final String name;

    public final String iconKey;

    public final String text;

    public ComponentDescription(
            String className_,
            String simpleClassName_,
            String name_,
            String text_,
            String iconKey_) {
        className = className_;
        simpleClassName = simpleClassName_;
        name = name_;
        text = text_;
        iconKey = iconKey_;
    }

    public static ComponentDescription forComponent(Component component) {
        return new ComponentDescription(
                component.getClass().getName(),
                component.getClass().getSimpleName(),
                component.getName(),
                findTextIfAny(component),
                guessAppropriateIcon(component));
    }

    private static String findTextIfAny(Component component) {
        if (component instanceof Frame) {
            return ((Frame) component).getTitle();
        } else if (component instanceof Dialog) {
            return ((Dialog) component).getTitle();
        }
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

    private static String guessAppropriateIcon(Component c) {
        if (c instanceof JCheckBox || c instanceof Checkbox) {
            return "checkbox";
        } else if (c instanceof JRadioButton) {
            return "radioButton";
        } else if (c instanceof AbstractButton) {
            return "button";
        } else if (c instanceof JTextPane) {
            return "textPane";
        } else if (c instanceof JEditorPane) {
            return "editorPane";
        } else if (c instanceof JFormattedTextField) {
            return "formattedTextField";
        } else if (c instanceof Label || c instanceof JLabel) {
            return "label";
        } else if (c instanceof List || c instanceof JList) {
            return "list";
        } else if (c instanceof JPanel) {
            return "panel";
        } else if (c instanceof JProgressBar) {
            return "progressBar";
        } else if (c instanceof Scrollbar || c instanceof JScrollBar) {
            return "scrollBar";
        } else if (c instanceof ScrollPane || c instanceof JScrollPane) {
            return "scrollPane";
        } else if (c instanceof JToolBar.Separator) {
            return "toolbarSeparator";
        } else if (c instanceof JSeparator) {
            return "separator";
        } else if (c instanceof JSlider) {
            return "slider";
        } else if (c instanceof JSpinner) {
            return "spinner";
        } else if (c instanceof JSplitPane) {
            return "splitPane";
        } else if (c instanceof JTabbedPane) {
            return "tabbedPane";
        } else if (c instanceof JTable) {
            return "table";
        } else if (c instanceof TextArea || c instanceof JTextArea) {
            return "textArea";
        } else if (c instanceof TextField || c instanceof JTextField) {
            return "textField";
        } else if (c instanceof JToolBar) {
            return "toolbar";
        } else if (c instanceof JTree) {
            return "tree";
        } else if (c instanceof Window) {
            return "window";
        } else {
            return "unknown";
        }
    }
}
