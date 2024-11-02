package by.shareiko.testutils.ui.components.render;

import by.shareiko.testutils.utils.PsiModuleUtils;
import by.shareiko.testutils.utils.PsiVirtualFileUtils;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;

public class SourceRootComboBoxRenderer extends ColoredListCellRenderer<VirtualFile> {

    private final Project project;

    public SourceRootComboBoxRenderer(Project project) {
        this.project = project;
    }

    @Override
    protected void customizeCellRenderer(JList<? extends VirtualFile> list, VirtualFile file, int index, boolean selected, boolean hasFocus) {
        if (file != null) {
            Module module = PsiModuleUtils.getFileModule(project, file);
            String moduleName = module == null ? "unknown" : module.getName();

            Color fgColor = selected ? UIUtil.getListSelectionForeground(true) : UIUtil.getListForeground();
            setForeground(fgColor);

            append("[" + moduleName + "] ", SimpleTextAttributes.REGULAR_ATTRIBUTES);
            append(getRelativePath(file), SimpleTextAttributes.GRAYED_ATTRIBUTES);

            if (PsiVirtualFileUtils.isTestsSourceRoot(project, file)) {
                setIcon(IconLoader.getIcon("/modules/testRoot.svg", getClass()));
            } else {
                setIcon(IconLoader.getIcon("/modules/sourceRoot.svg", getClass()));
            }
        }
    }

    private String getRelativePath(VirtualFile file) {
        String projectBasePath = project.getBasePath();

        if (projectBasePath == null) {
            return file.getPath();
        }

        // "..." - path from project root
        return "..." + file.getPath().replace(projectBasePath, "");
    }
}

