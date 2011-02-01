package org.seasar.ymir.vili.skeleton.action;

import org.eclipse.core.resources.IProject;
import org.seasar.ymir.vili.skeleton.ui.NewBatchWizard;
import org.t2framework.vili.IAction;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.util.WorkbenchUtils;

public class NewBatchAction implements IAction {
    public void run(IProject project, ViliProjectPreferences preferences) {
        WorkbenchUtils.startWizard(new NewBatchWizard(project, preferences
                .getRootPackageName()));
    }
}
