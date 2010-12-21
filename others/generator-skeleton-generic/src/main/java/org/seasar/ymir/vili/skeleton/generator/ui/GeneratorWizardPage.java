package org.seasar.ymir.vili.skeleton.generator.ui;

import org.eclipse.jface.wizard.WizardPage;

abstract public class GeneratorWizardPage extends WizardPage {
    protected GeneratorWizardPage(String pageName) {
        super(pageName);
    }

    protected GenerateWizard getGeneratorWizard() {
        return (GenerateWizard) getWizard();
    }
}
