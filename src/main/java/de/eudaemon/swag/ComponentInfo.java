package de.eudaemon.swag;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;

import java.awt.event.KeyEvent;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import javax.swing.SwingUtilities;

public class ComponentInfo extends NotificationBroadcasterSupport implements ComponentInfoMBean {

    private final Map<Integer, StackTraceElement[]> additionTraces;

    public ComponentInfo() {
        additionTraces = SwagAgent.additionTraces;
        installHotkeyListener();
    }

    @Override
    public StackTraceElement[] getStackTrace(int hashCode) {
        return additionTraces.get(hashCode);
    }

    private void installHotkeyListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(
                        e -> {
                            if (e.getKeyCode() == KeyEvent.VK_F12
                                    && e.getID() == KeyEvent.KEY_RELEASED) {
                                Notification notification =
                                        new Notification("Hotkey", ComponentInfo.this, 1);
                                notification.setUserData(getComponentUnderMouse().hashCode());
                                sendNotification(notification);
                                return true;
                            } else {
                                return false;
                            }
                        });
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
