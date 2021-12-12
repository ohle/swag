package de.eudaemon.swag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;

import java.awt.event.KeyEvent;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import javax.swing.SwingUtilities;

public class ComponentInfo extends NotificationBroadcasterSupport implements ComponentInfoMBean {

    private final Map<Component, StackTraceElement[]> additionTraces;
    private final Map<Integer, Component> taggedComponents = new HashMap<>();

    public ComponentInfo() {
        additionTraces = SwagAgent.additionTraces;
        installHotkeyListener();
    }

    @Override
    public StackTraceElement[] getStackTrace(int hashCode) {
        return additionTraces.get(taggedComponents.get(hashCode));
    }

    @Override
    public Dimension getSize(int hashCode) {
        return taggedComponents.get(hashCode).getSize();
    }

    private void installHotkeyListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(
                        e -> {
                            if (e.getKeyCode() == KeyEvent.VK_F12
                                    && e.getID() == KeyEvent.KEY_RELEASED) {
                                Notification notification = createComponentUnderMouseNotification();
                                sendNotification(notification);
                                return true;
                            } else {
                                return false;
                            }
                        });
    }

    private Notification createComponentUnderMouseNotification() {
        Notification notification = new Notification("Hotkey", ComponentInfo.this, 1);
        Optional<Component> component = getComponentUnderMouse();
        if (component.isPresent()) {
            taggedComponents.put(component.get().hashCode(), component.get());
            notification.setUserData(component.hashCode());
        }
        return notification;
    }

    private Optional<Component> getComponentUnderMouse() {
        Point location = MouseInfo.getPointerInfo().getLocation();
        Optional<Window> window =
                Arrays.stream(Window.getWindows())
                        .filter(w -> w.getMousePosition() != null)
                        .findFirst();
        if (window.isPresent()) {
            SwingUtilities.convertPointFromScreen(location, window.get());
            return Optional.ofNullable(
                    SwingUtilities.getDeepestComponentAt(window.get(), location.x, location.y));
        } else {
            return Optional.empty();
        }
    }
}
