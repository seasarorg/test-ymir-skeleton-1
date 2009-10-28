package org.seasar.ymir.vili.skeleton.dbflute_fragment_generic.action;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.seasar.ymir.vili.skeleton.dbflute_fragment_generic.util.DBFluteUtils;
import org.seasar.ymir.vili.skeleton.dbflute_fragment_generic.util.WorkbenchUtils;
import org.t2framework.vili.IAction;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;

abstract public class AbstractRunAction implements IAction {
    public void run(IProject project, ViliProjectPreferences preferences) {
        String dbfluteRoot = DBFluteUtils.getDBFluteClientRoot(project);
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
            DBFluteUtils.execute(file, false);
        } catch (Throwable t) {
            ViliContext.getVili().log(t);
            WorkbenchUtils.showMessage(MessageFormat.format(
                    "{0}を実行できませんでした。詳細はエラーログを参照して下さい。", getProgramName()));
        }
    }

    protected boolean isConfirmBeforeExecution() {
        return false;
    }

    boolean confirm() {
        return MessageDialog.openConfirm(WorkbenchUtils.getShell(), "Vili",
                getCofirmationMessage());
    }

    protected String getCofirmationMessage() {
        return "実行してもよろしいですか？";
    }

    abstract public String getProgramName();
}
