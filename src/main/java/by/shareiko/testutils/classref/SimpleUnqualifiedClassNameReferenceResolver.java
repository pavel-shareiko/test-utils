package by.shareiko.testutils.classref;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleUnqualifiedClassNameReferenceResolver implements UnqualifiedClassNameReferenceResolver {
    private static final String QUALIFIED_CLASS_NAME_REGEXP = "([a-z](?:[a-zA-Z0-9_]+\\.)+)([A-Z][a-z0-9]*)";
    private static final String CLASS_NAME_REGEXP = "\\b([A-Z][a-zA-Z0-9]*)\\b";
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile(CLASS_NAME_REGEXP);
    private static final Pattern QUALIFIED_CLASS_NAME_PATTERN = Pattern.compile(QUALIFIED_CLASS_NAME_REGEXP);

    @Override
    public ClassReferenceResolution resolve(Project project, PsiClass sourceClass, String initializerText) {
        Set<String> simpleClassNames = collectUnqualifiedClassIdentifiers(initializerText);

        PsiShortNamesCache psiNamesCache = PsiShortNamesCache.getInstance(project);
        ClassReferenceResolution resolution = new ClassReferenceResolution(initializerText, simpleClassNames);

        for (String simpleName : simpleClassNames) {
            PsiClass[] foundClasses = psiNamesCache.getClassesByName(simpleName, GlobalSearchScope.allScope(project));
            if (foundClasses.length == 1) {
                resolution.addResolvedClassReference(new ResolvedClassReference(simpleName, foundClasses[0]));
            } else if (foundClasses.length > 1) {
                for (PsiClass foundClass : foundClasses) {
                    if (foundClass.getQualifiedName() != null && isStandardJavaLibraryClass(foundClass)) {
                        resolution.addResolvedClassReference(new ResolvedClassReference(simpleName, foundClass));
                        break;
                    }
                }
            }
        }

        return resolution;
    }

    private static @NotNull Set<String> collectUnqualifiedClassIdentifiers(String initializerText) {
        Matcher matcher = CLASS_NAME_PATTERN.matcher(initializerText);

        QualifiedClassReferenceList qualifiedReferences = collectQualifiedClassReferences(initializerText);
        Set<String> simpleClassNames = new HashSet<>();
        while (matcher.find()) {
            String className = matcher.group(1);
            if (qualifiedReferences.hasReferenceTo(className)) {
                continue;
            }
            simpleClassNames.add(className);
        }
        return simpleClassNames;
    }

    private static QualifiedClassReferenceList collectQualifiedClassReferences(String initializerText) {
        Matcher matcher = QUALIFIED_CLASS_NAME_PATTERN.matcher(initializerText);

        QualifiedClassReferenceList references = new QualifiedClassReferenceList();
        while (matcher.find()) {
            references.add(QualifiedClassReference.of(matcher.group(1), matcher.group(2)));
        }

        return references;
    }

    private static boolean isStandardJavaLibraryClass(PsiClass foundClass) {
        return foundClass.getQualifiedName().startsWith("java.util");
    }
}
