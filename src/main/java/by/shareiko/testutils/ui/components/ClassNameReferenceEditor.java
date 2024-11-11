package by.shareiko.testutils.ui.components;

import by.shareiko.testutils.utils.PsiBuilderFinder;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.RecentsManager;
import com.intellij.ui.ReferenceEditorWithBrowseButton;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionListener;
import java.util.List;

public class ClassNameReferenceEditor extends ReferenceEditorWithBrowseButton {
    public static final String RECENTS_KEY = "SelectBuilderDialog.RecentsKey";

    private final Project project;
    private final String chooserTitle;
    private PsiClass selectedClass;

    public ClassNameReferenceEditor(@NotNull Project project, @NotNull GlobalSearchScope resolveScope) {
        super(null, project, text -> {
            PsiPackage defaultPackage = JavaPsiFacade.getInstance(project).findPackage("");
            JavaCodeFragment fragment = JavaCodeFragmentFactory.getInstance(project)
                    .createReferenceCodeFragment(text, defaultPackage, true, true);
            fragment.setVisibilityChecker(JavaCodeFragment.VisibilityChecker.EVERYTHING_VISIBLE);
            fragment.forceResolveScope(resolveScope);
            return PsiDocumentManager.getInstance(project).getDocument(fragment);
        }, getLatestSelectedClass(project));

        this.project = project;
        this.chooserTitle = "Select Builder Interface";
        addActionListener(getClassSelectedListener());
    }

    private static String getLatestSelectedClass(@NotNull Project project) {
        RecentsManager recentsManager = RecentsManager.getInstance(project);
        List<String> recentEntries = recentsManager.getRecentEntries(RECENTS_KEY);

        if (recentEntries == null || recentEntries.isEmpty()) {
            return "";
        }

        String latestSelectedClass = recentEntries.get(0);
        PsiClass aClass = JavaPsiFacade.getInstance(project).findClass(latestSelectedClass, GlobalSearchScope.projectScope(project));
        return PsiBuilderFinder.isValidBuilderInterface(aClass) ? latestSelectedClass : "";
    }

    private ActionListener getClassSelectedListener() {
        return e -> {
            TreeClassChooser chooser = TreeClassChooserFactory.getInstance(ClassNameReferenceEditor.this.project).createWithInnerClassesScopeChooser(
                    chooserTitle,
                    GlobalSearchScope.projectScope(ClassNameReferenceEditor.this.project),
                    PsiBuilderFinder::isValidBuilderInterface,
                    null);

            if (selectedClass != null) {
                chooser.selectDirectory(selectedClass.getContainingFile().getContainingDirectory());
            }
            chooser.showDialog();

            selectedClass = chooser.getSelected();
            if (selectedClass != null) {
                String selectedClassName = selectedClass.getQualifiedName();
                if (selectedClassName == null) {
                    return;
                }

                setText(selectedClassName);
                RecentsManager.getInstance(ClassNameReferenceEditor.this.project).registerRecentEntry(RECENTS_KEY, selectedClass.getQualifiedName());
            }
        };
    }

}