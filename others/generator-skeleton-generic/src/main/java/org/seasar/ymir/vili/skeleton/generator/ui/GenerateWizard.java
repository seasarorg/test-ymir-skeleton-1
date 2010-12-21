package org.seasar.ymir.vili.skeleton.generator.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.seasar.ymir.vili.skeleton.generator.GeneratorUtils;
import org.seasar.ymir.vili.skeleton.generator.IGenerator;
import org.seasar.ymir.vili.skeleton.generator.IParameters;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.util.WorkbenchUtils;

public class GenerateWizard extends Wizard {
    private List<Class<IGenerator<?>>> generatorClasses;

    private String targetProjectName;

    private String targetRootPackageName;

    private SelectGeneratorPage selectGeneratorPage;

    private SpecifyParametersPage specifyParametersPage;

    public GenerateWizard(List<Class<IGenerator<?>>> generatorClasses,
            String targetProjectName, String targetRootPackageName) {
        this.generatorClasses = generatorClasses;
        this.targetProjectName = targetProjectName;
        this.targetRootPackageName = targetRootPackageName;

        setWindowTitle("ソースの自動生成");
    }

    @Override
    public void addPages() {
        selectGeneratorPage = new SelectGeneratorPage();
        addPage(selectGeneratorPage);
        specifyParametersPage = new SpecifyParametersPage();
        addPage(specifyParametersPage);
    }

    @Override
    public boolean performFinish() {
        final IProject targetProject = ResourcesPlugin.getWorkspace().getRoot()
                .getProject(targetProjectName);
        @SuppressWarnings("unchecked")
        final IGenerator<IParameters> generator = (IGenerator<IParameters>) GeneratorUtils
                .newInstance(selectGeneratorPage.getSelectedGeneratorClass());
        generator.initialize(targetProject.getLocation().toOSString(),
                targetRootPackageName);
        final IParameters parameters = specifyParametersPage.getParameters();
        ProgressMonitorDialog dialog = new ProgressMonitorDialog(WorkbenchUtils
                .getShell());
        try {
            dialog.run(true, true, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {
                    generator.generate(parameters);
                    try {
                        targetProject.refreshLocal(IResource.DEPTH_INFINITE,
                                monitor);
                    } catch (CoreException ex) {
                        ViliContext.getVili().log(ex);
                    }
                }
            });
        } catch (InterruptedException ignore) {
        } catch (Throwable t) {
            ViliContext.getVili().log(t);
            WorkbenchUtils.showMessage("エラーが発生しました。詳細はエラーログを参照して下さい。");
        }

        return true;
    }

    public List<Class<IGenerator<?>>> getGeneratorClasses() {
        return generatorClasses;
    }

    public Class<IGenerator<?>> getSelectedGeneratorClass() {
        return selectGeneratorPage.getSelectedGeneratorClass();
    }
}
