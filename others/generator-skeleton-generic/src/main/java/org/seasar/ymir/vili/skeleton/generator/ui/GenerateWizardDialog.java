package org.seasar.ymir.vili.skeleton.generator.ui;

import java.util.List;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.seasar.ymir.vili.skeleton.generator.IGenerator;

public class GenerateWizardDialog extends WizardDialog {
    public GenerateWizardDialog(Shell parentShell,
            List<Class<IGenerator<?>>> generatorClasses,
            String targetProjectName) {
        super(parentShell, new GenerateWizard(generatorClasses,
                targetProjectName));
    }
}
