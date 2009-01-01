package org.seasar.ymir.skeleton;

import static org.seasar.ymir.skeleton.util.DBFluteUtils.PREFIX_DBFLUTE;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.ymir.skeleton.util.DBFluteUtils;
import org.seasar.ymir.vili.AbstractConfigurator;
import org.seasar.ymir.vili.Activator;
import org.seasar.ymir.vili.InclusionType;
import org.seasar.ymir.vili.ViliBehavior;
import org.seasar.ymir.vili.ViliProjectPreferences;

public class Configurator extends AbstractConfigurator {
    private static final String PARAM_DBFLUTEPROJECTNAME = "dbfluteProjectName";

    private static final String PARAM_ISDELETEOLDTABLECLASS = "isDeleteOldTableClass";

    private static final String PARAM_ISDELETEOLDVERSION = "isDeleteOldVersion";

    private static final String PARAM_UPDATEBATCHFILES = "updateBatchFiles";

    private static final String SUFFIX_BAT = ".bat";

    private static final String SUFFIX_SH = ".sh";

    private static final String NATURE_ID_YMIRPROJECT = "org.seasar.ymir.eclipse.ymirProjectNature";

    private static final String PATHPREFIX_DBFLUTE = "dbflute_${dbfluteProjectName}/";

    private static final String PATH_PROJECT_BAT = PATHPREFIX_DBFLUTE
            + "_project.bat";

    private static final String PATH_PROJECT_SH = PATHPREFIX_DBFLUTE
            + "_project.sh";

    private static final String PATH_MYDBFLUTE = "mydbflute";

    private static final String PATH_YMIRDAODICON = "src/main/resources/ymir-dao.dicon";

    private static final String BATCH_INITIALIZE = "_initialize";

    private static final String PATHPREFIX_MYDBFLUTE_DBFLUTE = PATH_MYDBFLUTE
            + "/dbflute-";

    private static final String PARAM_DBFLUTE_VERSION = "dbflute.version";

    private static final String PARAM_ISDELETEOLDVERSION_DEFAULT = "template.parameter.isDeleteOldVersion.default";

    private boolean updateBatFiles;

    @Override
    public void start(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences) {
        MapProperties properties = behavior.getProperties();

        Set<String> set = new LinkedHashSet<String>(Arrays.asList(behavior
                .getTemplateParameters()));
        if (oldVersionExists(project)) {
            // 古いバージョンが存在する場合。
            set.remove(PARAM_DBFLUTEPROJECTNAME);
            set.remove(PARAM_ISDELETEOLDTABLECLASS);

            properties
                    .setProperty(
                            PARAM_ISDELETEOLDVERSION_DEFAULT,
                            String
                                    .valueOf(!project
                                            .getFolder(
                                                    PATHPREFIX_MYDBFLUTE_DBFLUTE
                                                            + properties
                                                                    .getProperty(PARAM_DBFLUTE_VERSION))
                                            .exists()));
        } else {
            // 新規インストールの場合。
            set.remove(PARAM_ISDELETEOLDVERSION);
            set.remove(PARAM_UPDATEBATCHFILES);
            properties.setProperty(
                    "template.parameter.dbfluteProjectName.default",
                    getDefaultDBFluteProjectName(project, preferences));

        }
        properties.setProperty(ViliBehavior.TEMPLATE_PARAMETERS, PropertyUtils
                .join(set.toArray(new String[0])));

        behavior.notifyPropertiesChanged();
    }

    String getDefaultDBFluteProjectName(IProject project,
            ViliProjectPreferences preferences) {
        String name = preferences.getProjectName();
        String root = DBFluteUtils.getDBFluteRoot(project);
        if (root != null) {
            name = root.substring(PREFIX_DBFLUTE.length());
        }
        return name;
    }

    private boolean oldVersionExists(IProject project) {
        return project != null && project.getFolder(PATH_MYDBFLUTE).exists();
    }

    @Override
    public void processBeforeExpanding(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        monitor.beginTask("Process before expanding", 1);
        try {
            if (oldVersionExists(project)) {
                parameters.put(PARAM_DBFLUTEPROJECTNAME,
                        getDefaultDBFluteProjectName(project, preferences));
            }

            Boolean isDeleteOldVersion = (Boolean) parameters
                    .get(PARAM_ISDELETEOLDVERSION);
            if (PropertyUtils.valueOf(isDeleteOldVersion, false)) {
                try {
                    deleteOldVersion(project,
                            new SubProgressMonitor(monitor, 1));
                } catch (CoreException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                monitor.worked(1);
            }

            updateBatFiles = PropertyUtils.valueOf((Boolean) parameters
                    .get(PARAM_UPDATEBATCHFILES), false);
        } finally {
            monitor.done();
        }
    }

    void deleteOldVersion(IProject project, IProgressMonitor monitor)
            throws CoreException {
        IFolder mydbflute = project.getFolder("mydbflute");
        if (!mydbflute.exists()) {
            return;
        }

        IResource[] members = mydbflute.members();
        monitor.beginTask("Delete old resources", members.length);
        try {
            for (IResource member : members) {
                member.delete(true, new SubProgressMonitor(monitor, 1));
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
            }
        } finally {
            monitor.done();
        }
    }

    @Override
    public InclusionType shouldExpand(String path, String resolvedPath,
            IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters) {
        boolean exists = project.getFile(resolvedPath).exists()
                || project.getFolder(resolvedPath).exists();
        if (!exists) {
            if (path.equals(PATH_YMIRDAODICON)) {
                boolean ymirProject;
                try {
                    ymirProject = project.hasNature(NATURE_ID_YMIRPROJECT);
                } catch (CoreException ex) {
                    throw new RuntimeException(ex);
                }
                if (!ymirProject) {
                    return InclusionType.EXCLUDED;
                }
                return InclusionType.UNDEFINED;
            }
        }

        if (path.startsWith(PATHPREFIX_DBFLUTE)) {
            if (path.equals(PATH_PROJECT_BAT) || path.equals(PATH_PROJECT_SH)) {
                return InclusionType.INCLUDED;
            }

            if (updateBatFiles
                    && (path.endsWith(SUFFIX_BAT) || path.endsWith(SUFFIX_SH))) {
                return InclusionType.INCLUDED;
            }

            if (exists) {
                return InclusionType.EXCLUDED;
            }
        }
        return InclusionType.UNDEFINED;
    }

    @Override
    public void processAfterExpanded(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        try {
            DBFluteUtils.execute(project
                    .getFile(DBFluteUtils.getDBFluteRoot(project) + "/"
                            + BATCH_INITIALIZE
                            + DBFluteUtils.getBatchExtension()));
        } catch (Throwable t) {
            Activator.log("プロジェクトの初期化ができませんでした", t);
            throw new RuntimeException(t);
        }
    }
}
