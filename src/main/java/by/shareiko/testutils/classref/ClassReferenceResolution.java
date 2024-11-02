package by.shareiko.testutils.classref;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClassReferenceResolution {
    private final List<ResolvedClassReference> resolvedClassReferences = new ArrayList<>();
    private final String initializerText;
    private final Set<String> classIdentifiers;

    public ClassReferenceResolution(String initializerText, Set<String> simpleClassNames) {
        this.initializerText = initializerText;
        this.classIdentifiers = simpleClassNames;
    }

    public void addResolvedClassReference(final ResolvedClassReference resolvedClassReference) {
        resolvedClassReferences.add(resolvedClassReference);
    }

    public List<ResolvedClassReference> getResolvedClassReferences() {
        return resolvedClassReferences;
    }

    public int getResolvedReferencesCount() {
        return resolvedClassReferences.size();
    }

    public List<String> getUnresolvedClassReferences() {
        Set<String> classIdentifiers = getClassIdentifiers();
        List<String> unresolvedClassReferences = new ArrayList<>();
        for (String classIdentifier : classIdentifiers) {
            if (!isResolved(classIdentifier)) {
                unresolvedClassReferences.add(classIdentifier);
            }
        }

        return unresolvedClassReferences;
    }

    public String getInitializerText() {
        return initializerText;
    }

    public Set<String> getClassIdentifiers() {
        return classIdentifiers;
    }

    public boolean isResolved(String classIdentifier) {
        return getResolvedClassReferences().stream()
                .map(ResolvedClassReference::referenceText)
                .anyMatch(r -> r.equals(classIdentifier));
    }
}
