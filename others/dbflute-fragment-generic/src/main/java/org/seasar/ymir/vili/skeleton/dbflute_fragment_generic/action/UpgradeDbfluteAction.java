package org.seasar.ymir.vili.skeleton.dbflute_fragment_generic.action;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.seasar.ymir.vili.skeleton.dbflute_fragment_generic.Globals;
import org.seasar.ymir.vili.skeleton.dbflute_fragment_generic.util.WorkbenchUtils;
import org.t2framework.vili.IAction;
import org.t2framework.vili.Mold;
import org.t2framework.vili.MoldType;
import org.t2framework.vili.MoldTypeMismatchException;
import org.t2framework.vili.ProcessContext;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.ViliVersionMismatchException;

public class UpgradeDbfluteAction implements IAction {
    private static final String GROUP_ID = "org.seasar.ymir.vili.skeleton";

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
                        mold[0] = ViliContext.getVili().getMoldResolver()
                                .resolveMold(
                                        GROUP_ID,
                                        ARTIFACT_ID,
                                        null,
                                        MoldType.FRAGMENT,
                                        preferences.getViliVersion(),
                                        ViliContext.getVili()
                                                .getPreferenceStore()
                                                .getBoolean(
                                                        "useSnapshotFragment"),
                                        project, ProcessContext.TEMPORARY,
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
                mold[0].getBehavior().getProperties().setProperty(
                        Globals.PARAM_UPGRADEDBFLUTE_DEFAULT,
                        String.valueOf(true));
                ViliContext.getVili().getProjectBuilder()
                        .createAddFragmentsWizardDialog(
                                WorkbenchUtils.getShell(), project, mold[0])
                        .open();
            }
        } catch (InterruptedException ignore) {
        } catch (Throwable t) {
            ViliContext.getVili().log(t);
            WorkbenchUtils
                    .showMessage("エラーが発生したため、DBFluteのアップグレード処理を完了できませんでした。詳細はエラーログを参照して下さい。");
        }
    }
}
