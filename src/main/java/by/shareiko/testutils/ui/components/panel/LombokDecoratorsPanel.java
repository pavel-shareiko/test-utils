package by.shareiko.testutils.ui.components.panel;

import by.shareiko.testutils.properties.ClassDecorator;
import by.shareiko.testutils.properties.TestDataBuilderConfiguration;
import by.shareiko.testutils.ui.components.*;
import com.intellij.psi.PsiClass;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import org.codehaus.plexus.util.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LombokDecoratorsPanel extends JBPanel<LombokDecoratorsPanel> {
    private static final int MAX_COMPONENTS_PER_ROW = 5;

    private final List<ClassDecoratorComponentProvider> decorators = new ArrayList<>();
    private final PsiClass sourceClass;

    public LombokDecoratorsPanel(PsiClass sourceClass) {
        this.sourceClass = sourceClass;
        setLayout(new GridBagLayout());
        setBorder(IdeBorderFactory.createBorder());

        init();
    }

    private void init() {
        decorators.add(new WithAnnotationDecoratorComponentProvider());
        decorators.add(new AllArgsConstructorAnnotationDecoratorComponentProvider());
        decorators.add(new DataAnnotationDecoratorComponentProvider());
        decorators.add(new NoArgsConstructorAnnotationDecoratorComponentProvider(StringUtils.uncapitalise(sourceClass.getName())));

        addDecoratorComponents();
    }

    private void addDecoratorComponents() {
        Map<Boolean, List<ClassDecoratorComponentProvider>> groupedDecorators = decorators.stream()
                .collect(Collectors.groupingBy(ElementDecoratorComponentProvider::isMultiline));

        var gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = JBUI.insets(4);

        addSingleLineComponents(gbc, groupedDecorators.get(false));

        gbc.gridx = 0;
        gbc.gridwidth = MAX_COMPONENTS_PER_ROW;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addMultilineComponents(gbc, groupedDecorators.get(true));
    }

    private void addSingleLineComponents(GridBagConstraints gbc, List<ClassDecoratorComponentProvider> singleLineComponents) {
        if (singleLineComponents == null) {
            return;
        }
        for (var decorator : singleLineComponents) {
            if (gbc.gridx > MAX_COMPONENTS_PER_ROW) {
                gbc.gridx = 0;
                gbc.gridy++;
            }
            add(decorator.getComponent(), gbc);
            gbc.gridx++;
        }
    }

    private void addMultilineComponents(GridBagConstraints gbc, List<ClassDecoratorComponentProvider> multilineComponents) {
        if (multilineComponents == null) {
            return;
        }
        for (var decorator : multilineComponents) {
            gbc.gridy++;
            add(decorator.getComponent(), gbc);
        }
    }

    public List<ClassDecorator> getDecorators() {
        return decorators.stream()
                .filter(ClassDecoratorComponentProvider::isRequested)
                .map(ClassDecoratorComponentProvider::getDecorator)
                .toList();
    }
}
