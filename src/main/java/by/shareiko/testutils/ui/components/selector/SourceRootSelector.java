package by.shareiko.testutils.ui.components.selector;

import by.shareiko.testutils.ui.components.render.SourceRootComboBoxRenderer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SourceRootSelector extends ComboBox<VirtualFile> {

    public SourceRootSelector(Project project) {
        setRenderer(new SourceRootComboBoxRenderer(project));
        populateSourceRootComboBox(project);
    }

    private void populateSourceRootComboBox(Project project) {
        List<VirtualFile> roots = getSourceRoots(project);

        DefaultComboBoxModel<VirtualFile> model = new DefaultComboBoxModel<>(roots.toArray(new VirtualFile[0]));
        setModel(model);
    }

    private static @NotNull List<VirtualFile> getSourceRoots(Project project) {
        ModuleManager manager = ModuleManager.getInstance(project);
        ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
        Module[] modules = manager.getModules();

        List<VirtualFile> roots = new ArrayList<>();
        for (Module module : modules) {
            ModuleRootManager root = ModuleRootManager.getInstance(module);
            List<VirtualFile> sourceRoots = Arrays.stream(root.getSourceRoots())
                    .filter(r -> projectFileIndex.isInSource(r)
                            && projectFileIndex.isInTestSourceContent(r))
                    .toList();
            roots.addAll(sourceRoots);
        }

        return roots;
    }
}

