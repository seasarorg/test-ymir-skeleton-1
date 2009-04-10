package org.seasar.ymir.vili.skeleton.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.seasar.ymir.vili.skeleton.util.DBFluteUtils;
import org.seasar.ymir.vili.skeleton.util.WorkbenchUtils;
import org.t2framework.vili.IAction;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;

public class ViewSchemaAction implements IAction {
    private static final String PREFIX_PROJECT_SCHEMA_HTML = "project-schema-";

    public void run(IProject project, ViliProjectPreferences preferences) {
        try {
            String root = DBFluteUtils.getDBFluteClientRoot(project);
            if (root == null) {
                // DBFluteクライアントディレクトリが見つからなかった。
                return;
            }

            IFolder doc = project.getFolder(root + "/output/doc");
            if (!doc.exists()) {
                // docフォルダが見つからなかった。
                return;
            }

            for (IResource member : doc.members()) {
                if (member.getName().startsWith(PREFIX_PROJECT_SCHEMA_HTML)
                        && member.getType() == IResource.FILE) {
                    WorkbenchUtils.openResource((IFile) member);
                    return;
                }
            }
        } catch (CoreException ex) {
            ViliContext.getVili().log(ex);
        }
    }
}
