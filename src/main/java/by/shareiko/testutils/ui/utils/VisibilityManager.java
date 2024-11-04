package by.shareiko.testutils.ui.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class VisibilityManager {
    public static final BooleanSupplier ALWAYS_VISIBLE = () -> true;

    @NotNull
    private final BooleanSupplier visibilityCondition;
    @NotNull
    private final JComponent component;
    @NotNull
    private final List<VisibilityManager> children = new ArrayList<>();
    @Nullable
    private VisibilityManager parent;

    private VisibilityManager(@NotNull JComponent component, @NotNull BooleanSupplier visibilityCondition) {
        this.component = component;
        this.visibilityCondition = visibilityCondition;
    }

    @Contract("_ -> new")
    public static @NotNull VisibilityManager create(JComponent root) {
        return create(root, ALWAYS_VISIBLE);
    }

    @Contract("_, _ -> new")
    public static @NotNull VisibilityManager create(JComponent root, BooleanSupplier visibilityCondition) {
        return new VisibilityManager(root, visibilityCondition);
    }

    public VisibilityManager withChild(JComponent child) {
        return withChild(child, getComponentDependantCondition(this));
    }

    public VisibilityManager withChild(JComponent child, BooleanSupplier visibilityCondition) {
        VisibilityManager childManager = new VisibilityManager(child, visibilityCondition);
        childManager.parent = this;
        this.children.add(childManager);
        return childManager;
    }

    private BooleanSupplier getComponentDependantCondition(VisibilityManager parent) {
        if (parent == null) {
            return ALWAYS_VISIBLE;
        }

        if (parent.component instanceof JCheckBox cb) {
            return () -> cb.isVisible() && cb.isSelected();
        }
        return parent.component::isVisible;
    }

    public void install() {
        var root = getRoot();
        root.updateVisibility();

        var childrenQueue = new LinkedList<VisibilityManager>();
        childrenQueue.push(root);
        while (!childrenQueue.isEmpty()) {
            var current = childrenQueue.poll();
            current.registerChangeListener();

            if (!current.children.isEmpty()) {
                current.children.forEach(childrenQueue::push);
            }
        }
    }

    private void registerChangeListener() {
        if (this.component instanceof JCheckBox cb) {
            cb.addItemListener((e) -> updateVisibility());
        }
    }

    private VisibilityManager getRoot() {
        if (this.parent == null) {
            return this;
        }

        var current = this;
        while (current.parent != null) {
            current = current.parent;
        }

        return current;
    }

    public void updateVisibility() {
        boolean visible = isVisible();
        this.component.setVisible(visible);
        this.children.forEach(VisibilityManager::updateVisibility);
    }

    public boolean isVisible() {
        boolean isSelfVisible = this.visibilityCondition.getAsBoolean();
        boolean isParentVisible = isParentVisible();

        return isSelfVisible && isParentVisible;
    }

    public boolean isParentVisible() {
        if (parent == null) {
            return true;
        }

        return parent.isVisible();
    }
}