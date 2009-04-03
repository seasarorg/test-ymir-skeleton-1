package org.seasar.ymir.skeleton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.ymir.vili.AbstractConfigurator;
import org.seasar.ymir.vili.Activator;
import org.seasar.ymir.vili.InclusionType;
import org.seasar.ymir.vili.Mold;
import org.seasar.ymir.vili.ViliBehavior;
import org.seasar.ymir.vili.ViliProjectPreferences;
import org.seasar.ymir.vili.maven.util.ArtifactUtils;
import org.seasar.ymir.vili.model.maven.Dependency;
import org.seasar.ymir.vili.util.JdtUtils;

public class Configurator extends AbstractConfigurator implements Globals {
    @Override
    public void start(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences) {
        initializeProductVersion(project, behavior, preferences);
        initializeSuperclass(project, behavior, preferences);
    }

    void initializeSuperclass(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences) {
        behavior.setTemplateParameterDefault(PARAM_SUPERCLASS, preferences
                .getRootPackageName()
                + ".web.PageBase");
    }

    void initializeProductVersion(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences) {
        String baseVersionPrefix = behavior.getProperty(KEY_BASEVERSION);
        if (baseVersionPrefix != null) {
            baseVersionPrefix += ".";
        }
        String prerequisite = behavior.getProperty(KEY_PREREQUISITE);
        List<String> list = new ArrayList<String>();
        for (String version : Activator.getArtifactResolver().getVersions(
                GROUPID, ARTIFACTID, false)) {
            if (ArtifactUtils.compareVersions(version, prerequisite) >= 0) {
                if (baseVersionPrefix == null
                        || version.startsWith(baseVersionPrefix))
                    list.add(version);
            }
        }
        if (list.isEmpty()) {
            // 候補がない場合はprerequisiteのSNAPSHOTバージョンを出すようにする。
            list.add(prerequisite + "-SNAPSHOT");
        }
        String[] versions = list.toArray(new String[0]);

        behavior.setTemplateParameterCandidates(PARAM_PRODUCTVERSION, versions);
        if (behavior.getTemplateParameterDefault(PARAM_PRODUCTVERSION) == null
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
            if (path.equals(PATH_PAGECUSTOMIZER)) {
                return InclusionType.EXCLUDED;
            } else if (path.equals(PATH_XADATASOURCE)) {
                return InclusionType.INCLUDED;
            }
        } else {
            if (path.equals(PATH_PAGECUSTOMIZER)) {
                return InclusionType.INCLUDED;
            } else if (path.equals(PATH_XADATASOURCE)) {
                return InclusionType.EXCLUDED;
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
        // Seasar2.4をYmir1.0と組み合わせて安定しているバージョンに変更する。

        // s2-extension。
        Dependency dependency = dependencyMap.get(new Dependency(
                GROUPID_SEASAR, ARTIFACTID_S2EXTENSION));
        if (is24Family(dependency)) {
            dependency.setVersion(STABLE_VERSION_24);
        }

        // s2-tiger。
        dependency = dependencyMap.get(new Dependency(GROUPID_SEASAR,
                ARTIFACTID_S2TIGER));
        if (is24Family(dependency)) {
            dependency.setVersion(STABLE_VERSION_24);
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
        return version.startsWith("2.4.");
    }

    @Override
    public void processAfterExpanded(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        monitor.beginTask("", 2);
        try {
            createSuperclass(project, behavior, stringValue(parameters
                    .get(PARAM_SUPERCLASS)), new SubProgressMonitor(monitor, 1));
            addYmirNature(project, new SubProgressMonitor(monitor, 1));
        } finally {
            monitor.done();
        }
    }

    void createSuperclass(IProject project, ViliBehavior behavior,
            String superclass, IProgressMonitor monitor) {
        monitor.beginTask("", 2); //$NON-NLS-1$
        try {
            if (superclass == null) {
                return;
            }
            IFile file = project.getFile(Globals.PATH_SRC_MAIN_JAVA + "/" //$NON-NLS-1$
                    + superclass.replace('.', '/').concat(".java")); //$NON-NLS-1$
            if (file.exists()) {
                return;
            }

            String packageName;
            String classShortName;
            int dot = superclass.lastIndexOf(PACKAGE_DELIMITER);
            if (dot < 0) {
                packageName = ""; //$NON-NLS-1$
                classShortName = superclass;
            } else {
                packageName = superclass.substring(0, dot);
                classShortName = superclass.substring(dot + 1);
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(CREATESUPERCLASS_KEY_PACKAGENAME, packageName);
            map.put(CREATESUPERCLASS_KEY_CLASSSHORTNAME, classShortName);
            String body = Activator.getProjectBuilder().evaluateTemplate(
                    behavior.getClassLoader(), TEMPLATEPATH_SUPERCLASS, map);
            monitor.worked(1);
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            Activator.getProjectBuilder().writeFile(file, body,
                    new SubProgressMonitor(monitor, 1));
        } catch (CoreException ex) {
            Activator.log("Can't create superclass: " + superclass, ex);
            throw new RuntimeException(ex);
        } finally {
            monitor.done();
        }
    }

    void addYmirNature(IProject project, IProgressMonitor monitor) {
        try {
            Activator.getProjectBuilder().addNature(project,
                    NATURE_ID_YMIRPROJECT, monitor);
        } catch (CoreException ex) {
            Activator.log("Can't add Ymir nature", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean saveParameters(IProject project, Mold mold,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IPersistentPreferenceStore store) {
        MapProperties prop = loadApplicationProperties(project);
        updateApplicationProperties(prop, preferences, parameters);
        return saveApplicationProperties(project, prop);
    }

    @Override
    public Map<String, Object> loadParameters(IProject project, Mold mold,
            ViliProjectPreferences preferences) {
        MapProperties prop = loadApplicationProperties(project);
        Map<String, Object> parameters = new HashMap<String, Object>();
        for (@SuppressWarnings("unchecked")
        Enumeration<String> enm = prop.propertyNames(); enm.hasMoreElements();) {
            String name = enm.nextElement();
            parameters.put(name, prop.getProperty(name));
        }
        return parameters;
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
                Activator.log("Can't load: " + file, t); //$NON-NLS-1$
                throw new RuntimeException(t);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return properties;
    }

    void updateApplicationProperties(MapProperties prop,
            ViliProjectPreferences preferences, Map<String, Object> parameters) {
        prop.setProperty(ApplicationPropertiesKeys.ROOT_PACKAGE_NAME,
                preferences.getRootPackageName());
        String value = stringValue(parameters.get(PARAM_SUPERCLASS));
        if (value.length() > 0) {
            prop.setProperty(ApplicationPropertiesKeys.SUPERCLASS, value);
        }
        prop.setProperty(ApplicationPropertiesKeys.SOURCECREATOR_ENABLE,
                booleanValue(parameters.get(PARAM_AUTOGENERATIONENABLED)));
        prop.setProperty(ApplicationPropertiesKeys.FIELDPREFIX, JdtUtils
                .getFieldPrefix());
        prop.setProperty(ApplicationPropertiesKeys.FIELDSUFFIX, JdtUtils
                .getFieldSuffix());
        prop.setProperty(ApplicationPropertiesKeys.FIELDSPECIALPREFIX, JdtUtils
                .getFieldSpecialPrefix());
        prop.setProperty(ApplicationPropertiesKeys.ENABLEINPLACEEDITOR,
                booleanValue(parameters.get(PARAM_INPLACEEDITORENABLED)));
        prop.setProperty(ApplicationPropertiesKeys.ENABLECONTROLPANEL,
                booleanValue(parameters.get(PARAM_CONTROLPANELENABLED)));
        prop.setProperty(ApplicationPropertiesKeys.USING_FREYJA_RENDER_CLASS,
                booleanValue(parameters.get(PARAM_USINGFREYJARENDERCLASS)));
        prop.setProperty(ApplicationPropertiesKeys.BEANTABLE_ENABLED,
                booleanValue(parameters.get(PARAM_BEANTABLEENABLED)));
        prop.setProperty(
                ApplicationPropertiesKeys.FORM_DTO_CREATION_FEATURE_ENABLED,
                booleanValue(parameters
                        .get(PARAM_FORMDTOCREATIONFEATUREENABLED)));
        prop.setProperty(
                ApplicationPropertiesKeys.CONVERTER_CREATION_FEATURE_ENABLED,
                booleanValue(parameters
                        .get(PARAM_CONVERTERCREATIONFEATUREENABLED)));
        prop.setProperty(
                ApplicationPropertiesKeys.DAO_CREATION_FEATURE_ENABLED,
                booleanValue(parameters.get(PARAM_DAOCREATIONFEATUREENABLED)));
        prop.setProperty(
                ApplicationPropertiesKeys.DXO_CREATION_FEATURE_ENABLED,
                booleanValue(parameters.get(PARAM_DXOCREATIONFEATUREENABLED)));
        prop
                .setProperty(
                        ApplicationPropertiesKeys.TRYTOUPDATECLASSESWHENTEMPLATEMODIFIED,
                        booleanValue(parameters
                                .get(PARAM_TRYTOUPDATECLASSESWHENTEMPLATEMODIFIED)));

        prop.setProperty(ApplicationPropertiesKeys.ECLIPSE_ENABLED,
                booleanValue(parameters.get(PARAM_ECLIPSEENABLED)));
        if (isTrue(parameters.get(PARAM_ECLIPSEENABLED))) {
            value = stringValue(parameters.get(PARAM_RESOURCESYNCHRONIZERURL));
            if (value.length() > 0) {
                prop.setProperty(
                        ApplicationPropertiesKeys.RESOURCE_SYNCHRONIZER_URL,
                        value);
            }
            prop.setProperty(ApplicationPropertiesKeys.ECLIPSE_PROJECTNAME,
                    preferences.getProjectName());
        }

        HotdeployType hotdeployType = getHotdeployType(parameters
                .get(PARAM_HOTDEPLOYTYPE));
        prop
                .setProperty(
                        ApplicationPropertiesKeys.S2CONTAINER_CLASSLOADING_DISABLEHOTDEPLOY,
                        String.valueOf(hotdeployType != HotdeployType.S2));
        prop
                .setProperty(
                        ApplicationPropertiesKeys.S2CONTAINER_COMPONENTREGISTRATION_DISABLEDYNAMIC,
                        String.valueOf(hotdeployType == HotdeployType.VOID));
        prop.setProperty(ApplicationPropertiesKeys.HOTDEPLOY_TYPE,
                hotdeployType.getName());
    }

    boolean saveApplicationProperties(IProject project, MapProperties prop) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            prop.store(baos);

            Activator.getProjectBuilder().writeFile(
                    project.getFile(PATH_APP_PROPERTIES),
                    new ByteArrayInputStream(baos.toByteArray()),
                    new NullProgressMonitor());
            return true;
        } catch (Throwable t) {
            Activator.log("Can't save app.properties", t); //$NON-NLS-1$
            return false;
        }
    }

    HotdeployType getHotdeployType(Object value) {
        if ("s2".equals(value)) {
            return HotdeployType.S2;
        } else if ("javaRebel".equals(value)) {
            return HotdeployType.JAVAREBEL;
        } else {
            return HotdeployType.VOID;
        }
    }

    String stringValue(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return String.valueOf(obj);
        }
    }

    String booleanValue(Object obj) {
        if (obj == null) {
            return String.valueOf(false);
        } else {
            return String.valueOf(obj);
        }
    }

    boolean isTrue(Object obj) {
        if (obj == null) {
            return false;
        } else {
            return ((Boolean) obj).booleanValue();
        }
    }
}
