package org.seasar.ymir.vili.skeleton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.io.IOUtils;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.InclusionType;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.ViliProjectPreferencesDelta;
import org.t2framework.vili.maven.util.ArtifactUtils;
import org.t2framework.vili.model.maven.Dependency;

public class Configurator extends AbstractConfigurator implements Globals,
        ApplicationPropertiesKeys {
    private static final Map<String, String> stableS2VersionMap;
    static {
        Map<String, String> map = new HashMap<String, String>();
        map.put("1.0.7", "2.4.41");
        stableS2VersionMap = Collections.unmodifiableMap(map);
    }

    @Override
    public void start(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences) {
        initializeProductVersion(project, behavior, preferences);
    }

    void initializeProductVersion(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences) {
        String baseVersionPrefix = behavior.getProperty(KEY_BASEVERSION);
        if (baseVersionPrefix != null) {
            baseVersionPrefix += ".";
        }
        String prerequisite = behavior.getProperty(KEY_PREREQUISITE);
        List<String> list = new ArrayList<String>();
        for (String version : ViliContext.getVili().getArtifactResolver()
                .getVersions(GROUPID, ARTIFACTID, false)) {
            if (ArtifactUtils.compareVersions(version, prerequisite) >= 0) {
                if (baseVersionPrefix == null
                        || version.startsWith(baseVersionPrefix))
                    list.add(version);
            }
        }
        if (list.isEmpty()) {
            // 候補がない場合はprerequisiteのSNAPSHOTバージョンを出すようにする。
            list.add(prerequisite + SUFFIX_SNAPSHOT);
        }
        String[] versions = list.toArray(new String[0]);

        behavior.setTemplateParameterCandidates(PARAM_PRODUCTVERSION, versions);
        if (behavior.getTemplateParameterDefault(PARAM_PRODUCTVERSION).length() == 0
                && versions.length > 0) {
            behavior.setTemplateParameterDefault(PARAM_PRODUCTVERSION,
                    versions[0]);
        }
    }

    @Override
    public InclusionType shouldExpand(String path, String resolvedPath,
            IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters) {
        if (preferences.isUseDatabase()) {
            if (path.equals(PATH_BATCHCUSTOMIZER)) {
                return InclusionType.EXCLUDED;
            }
        } else {
            if (path.equals(PATH_BATCHCUSTOMIZER)) {
                return InclusionType.INCLUDED;
            }
        }

        return InclusionType.UNDEFINED;
    }

    @Override
    public Dependency[] mergePomDependencies(
            Map<Dependency, Dependency> dependencyMap,
            Map<Dependency, Dependency> fragmentDependencyMap,
            IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters) {
        if (PropertyUtils.valueOf(parameters.get(PARAM_USESTABLES24CONTAINER),
                false)) {
            // Seasar2.4をYmir1.0と組み合わせて安定しているバージョンに変更する。
            String s2Version = getStableS2Version(stringValue(parameters
                    .get(PARAM_PRODUCTVERSION)));

            // s2-extension。
            Dependency dependency = dependencyMap.get(new Dependency(
                    GROUPID_SEASAR, ARTIFACTID_S2EXTENSION));
            if (is24Family(dependency)) {
                dependency.setVersion(s2Version);
            }

            // s2-tiger。
            dependency = dependencyMap.get(new Dependency(GROUPID_SEASAR,
                    ARTIFACTID_S2TIGER));
            if (is24Family(dependency)) {
                dependency.setVersion(s2Version);
            }
        }

        // バッチ実行に必要なのでservlet-apiのscopeをcompileに変えておく。
        Dependency dependency = dependencyMap.get(new Dependency(
                GROUPID_SERVLETAPI, ARTIFACTID_SERVLETAPI));
        if (dependency != null) {
            dependency.setScope(SCOPE_COMPILE);
        }

        return super.mergePomDependencies(dependencyMap, fragmentDependencyMap,
                project, behavior, preferences, parameters);
    }

    boolean is24Family(Dependency dependency) {
        if (dependency == null) {
            return false;
        }
        String version = dependency.getVersion();
        if (version == null) {
            return false;
        }
        return version.startsWith("2.4."); //$NON-NLS-1$
    }

    @Override
    public void processAfterExpanded(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        monitor.beginTask("processAfterExpanded", 3); //$NON-NLS-1$
        try {
            saveRootPackageNames(project, preferences.getRootPackageNames());
        } finally {
            monitor.done();
        }
    }

    @Override
    public void notifyPreferenceChanged(IProject project,
            ViliBehavior behavior, ViliProjectPreferences preferences,
            ViliProjectPreferencesDelta delta) {
        if (ViliProjectPreferences.NAME_ROOTPACKAGENAMES
                .equals(delta.getName())) {
            saveRootPackageNames(project, (String[]) delta.getNewValue());
        }
    }

    void saveRootPackageNames(IProject project, String[] rootPackageNames) {
        MapProperties prop = loadApplicationProperties(project);
        prop.setProperty(ROOT_PACKAGE_NAME, PropertyUtils
                .join(rootPackageNames));
        saveApplicationProperties(project, prop);
    }

    MapProperties loadApplicationProperties(IProject project) {
        MapProperties properties = new MapProperties(
                new TreeMap<String, String>());
        IFile file = project.getFile(PATH_APP_PROPERTIES);
        if (file.exists()) {
            InputStream is = null;
            try {
                is = file.getContents();
                properties.load(is);
            } catch (Throwable t) {
                ViliContext.getVili().log("Can't load: " + file, t); //$NON-NLS-1$
                throw new RuntimeException(t);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return properties;
    }

    boolean saveApplicationProperties(IProject project, MapProperties prop) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            prop.store(baos);

            ViliContext.getVili().getProjectBuilder().writeFile(
                    project.getFile(PATH_APP_PROPERTIES),
                    new ByteArrayInputStream(baos.toByteArray()),
                    new NullProgressMonitor());
            return true;
        } catch (Throwable t) {
            ViliContext.getVili().log("Can't save app.properties", t); //$NON-NLS-1$
            return false;
        }
    }

    String stringValue(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return String.valueOf(obj);
        }
    }

    String getStableS2Version(String ymirVersion) {
        if (ymirVersion.endsWith(SUFFIX_SNAPSHOT)) {
            ymirVersion = ymirVersion.substring(0, ymirVersion.length()
                    - SUFFIX_SNAPSHOT.length());
        }
        String s2Version = stableS2VersionMap.get(ymirVersion);
        if (s2Version == null) {
            s2Version = "2.4.20";
        }
        return s2Version;
    }
}
