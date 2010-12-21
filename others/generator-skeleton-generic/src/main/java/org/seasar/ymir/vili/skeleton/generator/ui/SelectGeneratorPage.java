package org.seasar.ymir.vili.skeleton.generator.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.seasar.ymir.vili.skeleton.generator.IGenerator;
import org.seasar.ymir.vili.skeleton.generator.annotation.GUI;

public class SelectGeneratorPage extends GeneratorWizardPage {
    private Combo generator;

    private Text description;

    public SelectGeneratorPage() {
        super("SelectGeneratorPage");
        setTitle("対象の選択");
        setDescription("自動生成する対象を選択して下さい。");
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());
        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);

        generator = new Combo(composite, SWT.READ_ONLY);
        generator.setLayoutData(new GridData(GridData.FILL_BOTH));
        for (Class<IGenerator<?>> generatorClass : getGeneratorWizard()
                .getGeneratorClasses()) {
            GUI gui = generatorClass.getAnnotation(GUI.class);
            generator.add(gui.displayName());
        }

        description = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL
                | SWT.WRAP | SWT.READ_ONLY);
        description.setLayoutData(new GridData(GridData.FILL_BOTH));

        generator.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                updateDescriptionText();
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });

        generator.select(0);
        updateDescriptionText();

        setControl(composite);
    }

    private void updateDescriptionText() {
        description.setText(getSelectedGeneratorClass()
                .getAnnotation(GUI.class).description());
    }

    public Class<IGenerator<?>> getSelectedGeneratorClass() {
        return getGeneratorWizard().getGeneratorClasses().get(
                generator.getSelectionIndex());
    }

    @Override
    public boolean isPageComplete() {
        return true;
    }
}
