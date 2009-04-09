package org.seasar.ymir.vili.skeleton;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.ymir.vili.skeleton.util.DBFluteUtils;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.Activator;
import org.t2framework.vili.InclusionType;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.model.Database;

public class Configurator extends AbstractConfigurator implements Globals {
    private boolean oldVersionExists;

    private boolean updateBatFiles;

    private boolean upgradeDbflute;

    private boolean executeGenerateBatch;

    @Override
    public void start(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences) {
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

        Database original = (Database) parameters.get(PARAM_DATABASE);
        Database database;
        if (original != null) {
            database = (Database) original.clone();
        } else {
            database = new Database("", "", "", "", "", "", null);
        }

        String type = prop.getProperty("torque.database");
        if (type != null) {
            database.setType(type);
        }

        parameters.put(PARAM_DATABASE, database);

        String packageBase = prop.getProperty("torque.packageBase");
        if (packageBase == null) {
            packageBase = parameters.get("rootPackageName") + ".dbflute";
        }
        parameters.put(PARAM_PACKAGEBASE, packageBase);
    }

    private Properties readBuildProperties(IProject project) {
        try {
            Properties prop = new Properties();
            String root = DBFluteUtils.getDBFluteClientRoot(project);
            if (root == null) {
                return prop;
            }

            IFolder client = project.getFolder(root);
            if (!client.exists()) {
                return prop;
            }

            for (IResource member : client.members()) {
                String name = member.getName();
                if (name.startsWith("build-") && name.endsWith(".properties")
                        && member.getType() == IResource.FILE) {
                    InputStream is = null;
                    try {
                        is = ((IFile) member).getContents();
                        prop.load(is);
                    } catch (IOException ex) {
                        Activator.log(ex);
                        throw new RuntimeException(ex);
                    } finally {
                        IOUtils.closeQuietly(is);
                    }
                    return prop;
                }
            }

            return prop;
        } catch (CoreException ex) {
            Activator.log(ex);
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

        if (oldVersionExists && path.endsWith("/" + NAME_REPLACESCHEMASQL)) {
            // DBFluteをアップグレードする際にはreplace-schema.sqlを追加・上書きしない。
            return InclusionType.EXCLUDED;
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
        monitor.beginTask("Process after expanded", 1);
        try {
            if (!upgradeDbflute) {
                return;
            }

            String projectRoot = DBFluteUtils.getDBFluteClientRoot(project);
            String extension = DBFluteUtils.getBatchExtension();
            DBFluteUtils.execute(project.getFile(projectRoot + "/"
                    + BATCH_INITIALIZE + extension), true);

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
            Activator.log("プロジェクトの初期化ができませんでした", t);
            throw new RuntimeException(t);
        } finally {
            monitor.done();
        }
    }
}
