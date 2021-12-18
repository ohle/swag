package de.eudaemon.swag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import java.util.function.Function;

import java.io.Serializable;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

public class ComponentProperty implements Serializable {
    private static final long serialVersionUID = 1L;
    public final String category;
    public final String key;
    public final String valueDescription;

    public ComponentProperty(String category_, String key_, String valueDescription_) {
        category = category_;
        key = key_;
        valueDescription = valueDescription_;
    }

    private static class ListBuilder<T extends Component> {
        final List<ComponentProperty> list = new ArrayList<>();

        String category = "None";

        final T component;

        private ListBuilder(T component_) {
            component = component_;
        }

        ListBuilder<T> category(String category_) {
            category = category_;
            return this;
        }

        ListBuilder<T> add(String name, Function<T, ?> accessor) {
            Object value = accessor.apply(component);
            list.add(
                    new ComponentProperty(
                            category, name, value == null ? "null" : value.toString()));
            return this;
        }

        public <A> void addArray(String name, Function<T, ? extends A[]> accessor) {
            list.add(
                    new ComponentProperty(
                            category, name, Arrays.toString(accessor.apply(component))));
        }

        <NT extends Component> ListBuilder<NT> assumeType(Class<NT> clazz) {
            ListBuilder<NT> specialized = new ListBuilder<>(clazz.cast(component));
            specialized.list.addAll(list);
            return specialized;
        }
    }

    public static Collection<ComponentProperty> collectForComponent(Component c) {
        ListBuilder<?> b = new ListBuilder<>(c);
        b.category("Placement");
        b.add("location", Component::getLocation);
        b.add("alignmentX", Component::getAlignmentX);
        b.add("alignmentY", Component::getAlignmentY);
        b.add("baselineResizeBehavior", Component::getBaselineResizeBehavior);
        b.add("bounds", Component::getBounds);

        b.category("Appearance");
        b.add("opaque", Component::isOpaque);
        b.add("visible", Component::isVisible);
        b.add("showing", Component::isShowing);
        b.add("background", Component::getBackground);
        b.add("backgroundSet", Component::isBackgroundSet);
        b.add("colorModel", Component::getColorModel);
        b.add("componentOrientation", Component::getComponentOrientation);
        b.add("font", Component::getFont);
        b.add("fontSet", Component::isFontSet);
        b.add("foreground", Component::getForeground);
        b.add("foregroundSet", Component::isForegroundSet);

        b.category("Listeners");
        b.addArray("componentListeners", Component::getComponentListeners);
        b.addArray("hierarchyListeners", Component::getHierarchyListeners);
        b.addArray("hierarchyBoundsListeners", Component::getHierarchyBoundsListeners);
        b.addArray("keyListeners", Component::getKeyListeners);
        b.addArray("mouseListeners", Component::getMouseListeners);
        b.addArray("mouseMotionListeners", Component::getMouseMotionListeners);
        b.addArray("propertyChangeListeners", Component::getPropertyChangeListeners);

        b.category("Focus");
        b.add("focusOwner", Component::isFocusOwner);
        b.add("focusable", Component::isFocusable);
        b.add("focusTraversalKeysEnabled", Component::getFocusTraversalKeysEnabled);
        b.addArray("focusListeners", Component::getFocusListeners);

        if (c instanceof JComponent) {
            ListBuilder<JComponent> jb = b.assumeType(JComponent.class);
            jb.category("Appearance");
            jb.add("border", JComponent::getBorder);
            jb.add("insets", JComponent::getInsets);

            jb.category("Focus");
            jb.add("requestFocusEnabled", JComponent::isRequestFocusEnabled);

            jb.category("Listeners");
            jb.addArray("vetoableChangeListeners", JComponent::getVetoableChangeListeners);
            jb.addArray("ancestorListeners", JComponent::getAncestorListeners);

            b = jb;
        }

        if (c instanceof AbstractButton) {
            ListBuilder<AbstractButton> bb = b.assumeType(AbstractButton.class);
            bb.category("Button");
            bb.add("action", AbstractButton::getAction);
            bb.add("model", AbstractButton::getModel);
            bb.addArray("actionListeners", AbstractButton::getActionListeners);

            bb.category("Listeners");
            bb.addArray("actionListeners", AbstractButton::getActionListeners);

            bb.category("Appearance");
            bb.add("margin", AbstractButton::getMargin);
            bb.add("verticalAlignment", AbstractButton::getVerticalAlignment);
            bb.add("horizontalAlignment", AbstractButton::getHorizontalAlignment);
            bb.add("verticalTextPosition", AbstractButton::getVerticalTextPosition);
            bb.add("horizontalTextPosition", AbstractButton::getHorizontalTextPosition);
        }
        if (c instanceof Container) {
            ListBuilder<Container> cb = b.assumeType(Container.class).category("Container");
            cb.add("layout", Container::getLayout);
            cb.add("componentCount", Container::getComponentCount);
            cb.addArray("components", Container::getComponents);
            cb.add("insets", Container::getInsets);

            cb.category("Listeners");
            cb.addArray("containerListeners", Container::getContainerListeners);

            cb.category("Focus");
            cb.add("focusCycleRoot", Container::isFocusCycleRoot);
            cb.add("focusTraversalPolicy", Container::getFocusTraversalPolicy);
            b = cb;
        }

        if (c instanceof Window) {
            ListBuilder<Window> wb = b.assumeType(Window.class);
            wb.category("Window");
            wb.add("active", Window::isActive);
            wb.add("alwaysOnTop", Window::isAlwaysOnTop);
            wb.add("alwaysOnTopSupported", Window::isAlwaysOnTopSupported);

            wb.category("Focus");
            wb.add("autoRequestFocus", Window::isAutoRequestFocus);
            wb.add("mostRecentFocusOwner", Window::getMostRecentFocusOwner);
            b = wb;
        }
        return b.list;
    }
}
