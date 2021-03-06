package de.eudaemon.swag;

import java.lang.reflect.InvocationTargetException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import java.util.concurrent.atomic.AtomicReference;

import java.util.function.Supplier;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Window;

import java.awt.event.KeyEvent;

import java.awt.image.BufferedImage;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class ComponentInfo extends NotificationBroadcasterSupport implements ComponentInfoMBean {

    private final Map<Component, PlacementInfo> additionTraces;
    private final Map<Integer, Component> taggedComponents = new HashMap<>();

    public ComponentInfo(KeyStroke keyStroke) {
        additionTraces = SwagAgent.additionTraces;
        installHotkeyListener(keyStroke);
    }

    private static <T> T invokeInEDT(Supplier<T> supplier) {
        AtomicReference<T> ref = new AtomicReference<>();
        try {
            EventQueue.invokeAndWait(() -> ref.set(supplier.get()));
            return ref.get();
        } catch (InterruptedException | InvocationTargetException e_) {
            System.err.println("Error retrieving data from event thread:");
            e_.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<Integer> getRoots() {
        return invokeInEDT(
                () ->
                        Arrays.stream(Window.getWindows())
                                .peek(this::tag)
                                .map(Objects::hashCode)
                                .collect(Collectors.toSet()));
    }

    @Override
    public ComponentDescription getDescription(int hashCode) {
        return invokeInEDT(
                () ->
                        Optional.ofNullable(taggedComponents.get(hashCode))
                                .map(ComponentDescription::forComponent)
                                .orElse(null));
    }

    @Override
    public PlacementInfo getPlacementInfo(int hashCode) {
        return invokeInEDT(
                () ->
                        Optional.ofNullable(taggedComponents.get(hashCode))
                                .flatMap(c -> Optional.ofNullable(additionTraces.get(c)))
                                .orElse(null));
    }

    @Override
    public SizeInfos getSizeInfos(int hashCode) {
        return invokeInEDT(
                () ->
                        Optional.ofNullable(taggedComponents.get(hashCode))
                                .map(SizeInfos::forComponent)
                                .orElse(null));
    }

    @Override
    public Collection<ComponentProperty> getAllProperties(int hashCode) {
        return invokeInEDT(
                () ->
                        Optional.ofNullable(taggedComponents.get(hashCode))
                                .map(ComponentProperty::collectForComponent)
                                .orElse(null));
    }

    @Override
    public Collection<ChildBounds> getVisibleChildrenBounds(int hashCode) {
        return invokeInEDT(
                () -> {
                    Component[] children =
                            Optional.ofNullable(taggedComponents.get(hashCode))
                                    .filter(Container.class::isInstance)
                                    .map(Container.class::cast)
                                    .map(Container::getComponents)
                                    .orElse(new Component[] {});
                    return Arrays.stream(children)
                            .filter(Component::isVisible)
                            .peek(this::tag)
                            .map(ChildBounds::fromComponent)
                            .collect(Collectors.toList());
                });
    }

    @Override
    public int getParent(int hashCode) {
        return invokeInEDT(
                () -> {
                    Optional<Component> parent =
                            Optional.ofNullable(taggedComponents.get(hashCode))
                                    .map(Component::getParent);
                    parent.ifPresent(this::tag);
                    return parent.map(Object::hashCode).orElse(-1);
                });
    }

    @Override
    public int getRoot(int hashCode) {
        return invokeInEDT(
                () -> {
                    Optional<Component> root =
                            Optional.ofNullable(taggedComponents.get(hashCode))
                                    .map(SwingUtilities::getRoot);
                    root.ifPresent(this::tag);
                    return root.map(Objects::hashCode).orElse(-1);
                });
    }

    @Override
    public Collection<Integer> getChildren(int hashCode) {
        return invokeInEDT(
                () ->
                        Optional.ofNullable(taggedComponents.get(hashCode))
                                .filter(Container.class::isInstance)
                                .map(Container.class::cast)
                                .map(Container::getComponents)
                                .map(Arrays::stream)
                                .orElse(Stream.of())
                                .peek(this::tag)
                                .map(Objects::hashCode)
                                .collect(Collectors.toList()));
    }

    @Override
    public SerializableImage getSnapshot(int hashCode) {
        if (!taggedComponents.containsKey(hashCode)) {
            return null;
        }
        Component component = taggedComponents.get(hashCode);
        return invokeInEDT(
                () -> {
                    int w = component.getWidth();
                    int h = component.getHeight();
                    if (w <= 0 || h <= 0) {
                        return null;
                    }
                    BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
                    component.paint(image.createGraphics());
                    return new SerializableImage(image);
                });
    }

    private void tag(Component component) {
        taggedComponents.put(component.hashCode(), component);
    }

    private void installHotkeyListener(KeyStroke keyStroke) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(
                        e -> {
                            boolean modifiersMatch =
                                    ((e.getModifiersEx() & keyStroke.getModifiers()) > 0)
                                            || (e.getModifiers() == 0
                                                    && keyStroke.getModifiers() == 0);
                            if (e.getKeyCode() == keyStroke.getKeyCode()
                                    && modifiersMatch
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
