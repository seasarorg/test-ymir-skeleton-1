package org.seasar.ymir.skeleton.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.seasar.ymir.skeleton.util.DBFluteUtils;
import org.seasar.ymir.skeleton.util.WorkbenchUtils;
import org.seasar.ymir.vili.Activator;
import org.seasar.ymir.vili.IAction;
import org.seasar.ymir.vili.ViliProjectPreferences;

abstract public class AbstractRunAction implements IAction {
    public void run(IProject project, ViliProjectPreferences preferences) {
        String dbfluteRoot = DBFluteUtils.getDBFluteRoot(project);
        if (dbfluteRoot == null) {
            WorkbenchUtils.showMessage("DBFluteのプロジェクトディレクトリが見つかりませんでした。");
            return;
        }

        IFile file = project.getFile(dbfluteRoot + "/" + getProgramName()
                + DBFluteUtils.getBatchExtension());

        if (!file.exists()) {
            WorkbenchUtils.showMessage("実行ファイル（" + getProgramName()
                    + ")が見つかりませんでした。");
            return;
        }

        if (isConfirmBeforeExecution()) {
            if (!confirm()) {
                return;
            }
        }

        try {
            DBFluteUtils.execute(file);
        } catch (Throwable t) {
            Activator.log(t);
            WorkbenchUtils.showMessage("アクションを実行できませんでした。詳細はエラーログを参照して下さい。");
        }
    }

    protected boolean isConfirmBeforeExecution() {
        return false;
    }

    boolean confirm() {
        return MessageDialog.openConfirm(WorkbenchUtils.getShell(), "Vili",
                "実行してもよろしいですか？");
    }

    abstract public String getProgramName();
}
