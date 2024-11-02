package by.shareiko.testutils.classref;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class QualifiedClassReferenceList {
    private final List<QualifiedClassReference> references = new ArrayList<>();

    public void add(@NotNull QualifiedClassReference reference) {
        this.references.add(reference);
    }

    public boolean hasReferenceTo(String className) {
        for (var reference : references) {
            if (reference.getClassName().equals(className)) {
                return true;
            }
        }

        return false;
    }
}
