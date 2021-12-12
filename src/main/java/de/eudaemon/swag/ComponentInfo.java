package de.eudaemon.swag;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.awt.Component;
import java.awt.Container;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;

import java.awt.event.KeyEvent;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import javax.swing.SwingUtilities;

public class ComponentInfo extends NotificationBroadcasterSupport implements ComponentInfoMBean {

    private final Map<Component, PlacementInfo> additionTraces;
    private final Map<Integer, Component> taggedComponents = new HashMap<>();

    public ComponentInfo() {
        additionTraces = SwagAgent.additionTraces;
        installHotkeyListener();
    }

    @Override
    public PlacementInfo getPlacementInfo(int hashCode) {
        return Optional.ofNullable(taggedComponents.get(hashCode))
                .flatMap(c -> Optional.ofNullable(additionTraces.get(c)))
                .orElse(null);
    }

    @Override
    public SizeInfos getSizeInfos(int hashCode) {
        return Optional.ofNullable(taggedComponents.get(hashCode))
                .map(SizeInfos::forComponent)
                .orElse(null);
    }

    @Override
    public int getParent(int hashCode) {
        Optional<Component> parent =
                Optional.ofNullable(taggedComponents.get(hashCode)).map(Component::getParent);
        parent.ifPresent(this::tag);
        return parent.map(Object::hashCode).orElse(-1);
    }

    @Override
    public Collection<Integer> getChildren(int hashCode) {
        return Optional.ofNullable(taggedComponents.get(hashCode))
                .filter(Container.class::isInstance)
                .map(Container.class::cast)
                .map(Container::getComponents)
                .map(Arrays::stream)
                .orElse(Stream.of())
                .peek(this::tag)
                .map(Objects::hashCode)
                .collect(Collectors.toList());
    }

    private void tag(Component component) {
        taggedComponents.put(component.hashCode(), component);
    }

    private void installHotkeyListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(
                        e -> {
                            if (e.getKeyCode() == KeyEvent.VK_F12
                                    && e.getID() == KeyEvent.KEY_RELEASED) {
                                System.out.println("ComponentInfo.installHotkeyListener");
                                Notification notification = createComponentUnderMouseNotification();
                                sendNotification(notification);
                                System.out.println("ComponentInfo.installHotkeyListener 2");
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
            tag(component.get());
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
