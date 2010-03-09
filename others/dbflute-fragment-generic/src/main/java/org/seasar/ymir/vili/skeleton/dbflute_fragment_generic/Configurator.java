package org.seasar.ymir.vili.skeleton.dbflute_fragment_generic;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.ymir.vili.skeleton.dbflute_fragment_generic.util.DBFluteUtils;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.InclusionType;
import org.t2framework.vili.Mold;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.model.Database;
import org.t2framework.vili.model.maven.Dependency;

public class Configurator extends AbstractConfigurator implements Globals {
    private boolean oldVersionExists;

    private boolean updateBatFiles;

    private boolean upgradeDbflute;

    private boolean executeGenerateBatch;

    @Override
    public void start(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Mold skeleton, Mold[] fragments) {
        oldVersionExists = DBFluteUtils.oldVersionExists(project);

        MapProperties properties = behavior.getProperties();

        properties.setProperty(PARAM_UPGRADEDBFLUTE_LABEL, MessageFormat
                .format(properties.getProperty(PARAM_UPGRADEDBFLUTE_LABEL),
                        properties.getProperty(PARAM_DBFLUTE_VERSION)));

        Set<String> set = new LinkedHashSet<String>(Arrays.asList(behavior
                .getTemplateParameters()));
        if (oldVersionExists) {
            // 古いバージョンが存在する場合。
            set.remove(PARAM_DBFLUTEPROJECTNAME);
        } else {
            // 新規インストールの場合。
            set.remove(PARAM_UPGRADEDBFLUTE);
            set.remove(PARAM_ISDELETEOLDVERSION);
            set.remove(PARAM_UPDATEBATCHFILES);
            set.remove(PARAM_EXECUTEGENERATEBATCH);

            properties.setProperty(PARAM_DBFLUTEPROJECTNAME_DEFAULT,
                    DBFluteUtils.getDefaultDBFluteProjectName(project,
                            preferences));
        }
        properties.setProperty(ViliBehavior.TEMPLATE_PARAMETERS, PropertyUtils
                .join(set.toArray(new String[0])));
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
                    .get(PARAM_UPDATEBATCHFILES), false);
            executeGenerateBatch = PropertyUtils.valueOf((Boolean) parameters
                    .get(PARAM_EXECUTEGENERATEBATCH), false);

            if (oldVersionExists) {
                parameters.put(PARAM_DBFLUTEPROJECTNAME, DBFluteUtils
                        .getDefaultDBFluteProjectName(project, preferences));
            }

            adjustParameters(project, parameters);

            if (upgradeDbflute) {
                if (PropertyUtils.valueOf((Boolean) parameters
                        .get(PARAM_ISDELETEOLDVERSION), false)) {
                    DBFluteUtils.deleteOldVersion(project,
                            new SubProgressMonitor(monitor, 1));
                } else {
                    monitor.worked(1);
                }
            }

            Properties prop = loadAppProperties(project);
            if (PropertyUtils.valueOf(prop
                    .getProperty(Globals.PROP_ECLIPSE_ENABLE), false)) {
                parameters.put(PARAM_RESOURCESYNCHRONIZERURL, prop.getProperty(
                        Globals.PROP_RESOURCESYNCHRONIZERURL,
                        Globals.DEFAULT_PARAM_RESOURCESYNCHRONIZERURL));
            }
        } finally {
            monitor.done();
        }
    }

    Properties loadAppProperties(IProject project) {
        Properties prop = new Properties();
        IFile file = project.getFile(Globals.PATH_APP_PROPERTIES);
        if (file.exists()) {
            InputStream is = null;
            try {
                is = file.getContents();
                prop.load(is);
            } catch (Throwable t) {
                throw new RuntimeException("Can't load "
                        + Globals.PATH_APP_PROPERTIES, t);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return prop;
    }

    private void adjustParameters(IProject project,
            Map<String, Object> parameters) {
        Properties prop = readBuildProperties(project);
        Map<String, Object> basicInfoMap = DBFluteUtils.readDfprop(project,
                NAME_BASICINFOMAPDFPROP);

        Database original = (Database) parameters.get(PARAM_DATABASE);
        Database database;
        if (original != null) {
            database = (Database) original.clone();
        } else {
            database = new Database("", "", "", "", "", "", null);
        }

        String type = prop.getProperty("torque.database");
        if (type == null) {
            type = (String) basicInfoMap.get("database");
        }
        if (type != null) {
            database.setType(type);
        }

        parameters.put(PARAM_DATABASE, database);

        String packageBase = prop.getProperty("torque.packageBase");
        if (packageBase == null) {
            packageBase = (String) basicInfoMap.get("packageBase");
            if (packageBase == null) {
                packageBase = parameters.get("rootPackageName") + ".dbflute";
            }
        }
        parameters.put(PARAM_PACKAGEBASE, packageBase);
    }

    private Properties readBuildProperties(IProject project) {
        try {
            Properties prop = new Properties();
            IFile file = DBFluteUtils.getBuildProperties(project);
            if (file != null && file.exists()) {
                InputStream is = null;
                try {
                    is = file.getContents();
                    prop.load(is);
                } catch (IOException ex) {
                    ViliContext.getVili().log(ex);
                    throw new RuntimeException(ex);
                } finally {
                    IOUtils.closeQuietly(is);
                }
            }

            return prop;
        } catch (CoreException ex) {
            ViliContext.getVili().log(ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public InclusionType shouldExpand(String path, String resolvedPath,
            IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters) {
        boolean exists = project.getFile(resolvedPath).exists()
                || project.getFolder(resolvedPath).exists();
        if (!upgradeDbflute) {
            return InclusionType.EXCLUDED;
        }

        if (oldVersionExists) {
            if (path.endsWith("/" + NAME_REPLACESCHEMASQL)) {
                // DBFluteをアップグレードする際にはreplace-schema.sqlを追加・上書きしない。
                return InclusionType.EXCLUDED;
            } else if (path.startsWith(PATHPREFIX_BUILDPROPERTIES)) {
                // DBFluteをアップグレードする際にはbuild.propertiesを追加・上書きしない。
                return InclusionType.EXCLUDED;
            }
        }

        if (path.equals(PATH_DBFLUTEDICON)) {
            // dbflute.diconが存在しないなら生成しておく。
            if (!exists) {
                return InclusionType.INCLUDED;
            } else {
                return InclusionType.EXCLUDED;
            }
        }

        if (!exists) {
            if (path.equals(PATH_YMIRDAODICON)) {
                boolean veProject;
                try {
                    veProject = project.hasNature(NATURE_ID_VEPROJECT);
                } catch (CoreException ex) {
                    ViliContext.getVili().log(ex);
                    throw new RuntimeException(ex);
                }
                if (!veProject) {
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
        monitor.beginTask("Process after expanded", 1);
        try {
            if (!upgradeDbflute) {
                return;
            }

            // build-XXX.propertiesをbuild.propertiesにリネームする。
            renameBuildProperties(project, new SubProgressMonitor(monitor, 1));

            String projectRoot = DBFluteUtils.getDBFluteClientRoot(project);
            String extension = DBFluteUtils.getBatchExtension();

            if (executeGenerateBatch) {
                DBFluteUtils.execute(project.getFile(projectRoot + "/"
                        + BATCH_JDBC + extension), true);
                DBFluteUtils.execute(project.getFile(projectRoot + "/"
                        + BATCH_GENERATE + extension), true);
                DBFluteUtils.execute(project.getFile(projectRoot + "/"
                        + BATCH_DOC + extension), true);
                DBFluteUtils.execute(project.getFile(projectRoot + "/"
                        + BATCH_SQL2ENTITY + extension), true);
            }
        } catch (Throwable t) {
            ViliContext.getVili().log("プロジェクトの初期化ができませんでした", t);
            throw new RuntimeException(t);
        } finally {
            monitor.done();
        }
    }

    public Dependency[] mergePomDependencies(
            Map<Dependency, Dependency> dependencyMap,
            Map<Dependency, Dependency> fragmentDependencyMap,
            IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters) {
        Dependency dependency = dependencyMap.get(new Dependency(GROUPID_YMIR,
                ARTIFACTID_YMIR));
        if (dependency != null) {
            Dependency ymirDBFlute = new Dependency(GROUPID_YMIR,
                    ARTIFACTID_YMIR_DBFLUTE, dependency.getVersion());
            fragmentDependencyMap.put(ymirDBFlute, ymirDBFlute);
        }
        return null;
    }

    private void renameBuildProperties(IProject project,
            IProgressMonitor monitor) {
        monitor.beginTask("Rename " + NAME_BUILDPROPERTIES, 1);
        try {
            IFile oldFile = DBFluteUtils.getOldBuildProperties(project);
            IFile newFile = DBFluteUtils.getBuildProperties(project);
            if (oldFile != null && oldFile.exists() && newFile != null
                    && !newFile.exists()) {
                oldFile.move(newFile.getFullPath(), true, true,
                        new SubProgressMonitor(monitor, 1));
            }
        } catch (CoreException ex) {
            ViliContext.getVili().log(ex);
            throw new RuntimeException(ex);
        } finally {
            monitor.done();
        }
    }
}
