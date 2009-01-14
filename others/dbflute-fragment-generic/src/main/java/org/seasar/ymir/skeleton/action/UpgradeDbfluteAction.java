package org.seasar.ymir.skeleton.action;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.seasar.ymir.skeleton.util.WorkbenchUtils;
import org.seasar.ymir.vili.Activator;
import org.seasar.ymir.vili.IAction;
import org.seasar.ymir.vili.Mold;
import org.seasar.ymir.vili.MoldType;
import org.seasar.ymir.vili.MoldTypeMismatchException;
import org.seasar.ymir.vili.ViliProjectPreferences;
import org.seasar.ymir.vili.ViliVersionMismatchException;

public class UpgradeDbfluteAction implements IAction {
    private static final String GROUP_ID = "org.seasar.ymir.skeleton";

    private static final String ARTIFACT_ID = "dbflute-fragment-generic";

    public void run(final IProject project,
            final ViliProjectPreferences preferences) {
        final Mold[] mold = new Mold[1];
        try {
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(
                    WorkbenchUtils.getShell());
            dialog.run(true, true, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor)
                        throws InvocationTargetException, InterruptedException {
                    try {
                        mold[0] = Activator.getMoldResolver().resolveMold(
                                GROUP_ID,
                                ARTIFACT_ID,
                                null,
                                MoldType.FRAGMENT,
                                preferences.getViliVersion(),
                                Activator.getPreferenceStore().getBoolean(
                                        "useSnapshotFragment"), project,
                                monitor);
                        if (monitor.isCanceled()) {
                            throw new InterruptedException();
                        }
                    } catch (MoldTypeMismatchException ignore) {
                    } catch (ViliVersionMismatchException ex) {
                        throw new RuntimeException("Can't happen!", ex);
                    }
                }
            });

            if (mold[0] == null) {
                WorkbenchUtils
                        .showMessage("アップグレード可能なDBFluteフラグメントが見つかりませんでした。");
            } else {
                Activator.getProjectBuilder().createAddFragmentsWizardDialog(
                        WorkbenchUtils.getShell(), project, mold[0]).open();
            }
        } catch (InterruptedException ignore) {
        } catch (Throwable t) {
            Activator.log(t);
            WorkbenchUtils
                    .showMessage("エラーが発生したため、DBFluteのアップグレード処理を完了できませんでした。詳細はエラーログを参照して下さい。");
        }
    }
}
