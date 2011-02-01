package org.seasar.ymir.vili.skeleton.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;
import org.seasar.ymir.vili.skeleton.generator.BatchGenerator;
import org.seasar.ymir.vili.skeleton.generator.GeneratedResult;
import org.seasar.ymir.vili.skeleton.generator.BatchGenerator.Parameters;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.util.WorkbenchUtils;

public class NewBatchWizard extends Wizard {
    private IProject project;

    private String rootPackageName;

    private SpecifyParametersPage specifyParametersPage;

    public NewBatchWizard(IProject project, String rootPackageName) {
        this.project = project;
        this.rootPackageName = rootPackageName;

        setWindowTitle("バッチの追加");
    }

    @Override
    public void addPages() {
        specifyParametersPage = new SpecifyParametersPage();
        addPage(specifyParametersPage);
    }

    @Override
    public boolean performFinish() {
        final BatchGenerator generator = new BatchGenerator();
        generator.initialize(project.getLocation().toOSString(),
                rootPackageName);

        final Parameters parameters = new Parameters();
        parameters.setBatchName(specifyParametersPage.getBatchName());
        parameters.setGenerateTestClass(specifyParametersPage
                .shouldGenerateTestClass());
        parameters.setGenerateBat(specifyParametersPage.shouldGenerateBat());
        parameters.setGenerateSh(specifyParametersPage.shouldGenerateSh());

        try {
            IRunnableWithProgress op = new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("リソースの生成", 2);
                    try {
                        GeneratedResult result = generator.generate(parameters);
                        monitor.worked(1);

                        project.refreshLocal(IResource.DEPTH_INFINITE,
                                new SubProgressMonitor(monitor, 1));

                        if (result != null) {
                            List<String> paths = result
                                    .getActiveResourcePaths();
                            Collections.reverse(paths);
                            for (final String path : paths) {
                                PlatformUI.getWorkbench().getDisplay()
                                        .syncExec(new Runnable() {
                                            public void run() {
                                                WorkbenchUtils
                                                        .openResource(project
                                                                .getFile(path));
                                            }
                                        });
                            }
                        }
                    } catch (CoreException ex) {
                        ViliContext.getVili().log(ex);
                    } finally {
                        monitor.done();
                    }
                }
            };

            getContainer().run(true, true, op);
        } catch (InterruptedException ignore) {
        } catch (Throwable t) {
            ViliContext.getVili().log(t);
            WorkbenchUtils.showMessage("エラーが発生しました。詳細はエラーログを参照して下さい。");
        }

        return true;
    }
}
