package org.seasar.ymir.skeleton;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.ymir.skeleton.util.DBFluteUtils;
import org.seasar.ymir.vili.AbstractConfigurator;
import org.seasar.ymir.vili.Activator;
import org.seasar.ymir.vili.InclusionType;
import org.seasar.ymir.vili.ViliBehavior;
import org.seasar.ymir.vili.ViliProjectPreferences;

public class Configurator extends AbstractConfigurator implements Globals {
    private boolean updateBatFiles;

    private boolean upgradeDbflute;

    @Override
    public void start(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences) {
        MapProperties properties = behavior.getProperties();

        Set<String> set = new LinkedHashSet<String>(Arrays.asList(behavior
                .getTemplateParameters()));
        if (DBFluteUtils.oldVersionExists(project)) {
            // 古いバージョンが存在する場合。
            set.remove(PARAM_DBFLUTEPROJECTNAME);
        } else {
            // 新規インストールの場合。
            set.remove(PARAM_UPGRADEDBFLUTE);
            set.remove(PARAM_ISDELETEOLDVERSION);
            set.remove(PARAM_UPDATEBATCHFILES);

            properties.setProperty(PARAM_DBFLUTEPROJECTNAME_DEFAULT,
                    DBFluteUtils.getDefaultDBFluteProjectName(project,
                            preferences));
        }
        properties.setProperty(ViliBehavior.TEMPLATE_PARAMETERS, PropertyUtils
                .join(set.toArray(new String[0])));

        behavior.notifyPropertiesChanged();
    }

    @Override
    public void processBeforeExpanding(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        monitor.beginTask("Process before expanding", 1);
        try {
            upgradeDbflute = PropertyUtils.valueOf((Boolean) parameters
                    .get(PARAM_UPGRADEDBFLUTE), true);
            updateBatFiles = PropertyUtils.valueOf((Boolean) parameters
                    .get(PARAM_UPDATEBATCHFILES), true);

            if (DBFluteUtils.oldVersionExists(project)) {
                parameters.put(PARAM_DBFLUTEPROJECTNAME, DBFluteUtils
                        .getDefaultDBFluteProjectName(project, preferences));
            }

            if (upgradeDbflute) {
                Boolean isDeleteOldVersion = PropertyUtils.valueOf(
                        (Boolean) parameters.get(PARAM_ISDELETEOLDVERSION),
                        true);
                if (PropertyUtils.valueOf(isDeleteOldVersion, false)) {
                    DBFluteUtils.deleteOldVersion(project,
                            new SubProgressMonitor(monitor, 1));
                } else {
                    monitor.worked(1);
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
        if (!upgradeDbflute && !path.equals(PATH_YMIRDAODICON)) {
            return InclusionType.EXCLUDED;
        }

        if (!exists) {
            if (path.equals(PATH_YMIRDAODICON)) {
                boolean ymirProject;
                try {
                    ymirProject = project.hasNature(NATURE_ID_YMIRPROJECT);
                } catch (CoreException ex) {
                    Activator.log(ex);
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
