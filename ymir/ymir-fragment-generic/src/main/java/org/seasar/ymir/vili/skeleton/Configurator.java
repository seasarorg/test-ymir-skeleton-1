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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.io.IOUtils;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.InclusionType;
import org.t2framework.vili.Mold;
import org.t2framework.vili.ProcessContext;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.ViliProjectPreferencesDelta;
import org.t2framework.vili.maven.util.ArtifactUtils;
import org.t2framework.vili.model.maven.Dependency;
import org.t2framework.vili.model.maven.Profile;
import org.t2framework.vili.util.JdtUtils;

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
        initializeSuperclass(project, behavior, preferences);

        if (behavior.getProcessContext() == ProcessContext.MODIFY_PROPERTIES) {
            MapProperties prop = loadApplicationProperties(project);
            if (prop.getProperty(CORE_CHECKBOX_KEY) == null) {
                List<String> list = new ArrayList<String>();
                for (String name : behavior.getTemplateParameters(GROUP_MISC)) {
                    if (!PARAM_CHECKBOXKEY.equals(name)) {
                        list.add(name);
                    }
                }
                behavior.setTemplateParameters(GROUP_MISC, list
                        .toArray(new String[0]));
            }
        }
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
            if (path.equals(PATH_PAGECUSTOMIZER)) {
                return InclusionType.EXCLUDED;
            }
        } else {
            if (path.equals(PATH_PAGECUSTOMIZER)) {
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

        return super.mergePomDependencies(dependencyMap, fragmentDependencyMap,
                project, behavior, preferences, parameters);
    }

    @Override
    public Profile[] mergePomProfiles(Map<Profile, Profile> profileMap,
            Map<Profile, Profile> fragmentProfileMap, IProject project,
            ViliBehavior behavior, ViliProjectPreferences preferences,
            Map<String, Object> parameters) {
        Profile itProfile = new Profile("it");
        if (!profileMap.containsKey(itProfile)) {
            fragmentProfileMap.remove(itProfile);
        }
        Profile releaseProfile = new Profile("release");
        if (!profileMap.containsKey(releaseProfile)) {
            fragmentProfileMap.remove(releaseProfile);
        }

        return super.mergePomProfiles(profileMap, fragmentProfileMap, project,
                behavior, preferences, parameters);
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
            createSuperclass(project, behavior, stringValue(parameters
                    .get(PARAM_SUPERCLASS)), new SubProgressMonitor(monitor, 1));

            addVeNature(project, preferences,
                    new SubProgressMonitor(monitor, 1));

            saveRootPackageNames(project, preferences.getRootPackageNames());
        } finally {
            monitor.done();
        }
    }

    void createSuperclass(IProject project, ViliBehavior behavior,
            String superclass, IProgressMonitor monitor) {
        monitor.beginTask("createSuperclass", 2); //$NON-NLS-1$
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
            String body = ViliContext.getVili().getProjectBuilder()
                    .evaluateTemplate(getClass().getClassLoader(),
                            TEMPLATEPATH_SUPERCLASS, map);
            monitor.worked(1);
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            ViliContext.getVili().getProjectBuilder().writeFile(file, body,
                    new SubProgressMonitor(monitor, 1));
        } catch (CoreException ex) {
            ViliContext.getVili().log("Can't create superclass: " + superclass,
                    ex);
            throw new RuntimeException(ex);
        } finally {
            monitor.done();
        }
    }

    void addVeNature(IProject project, ViliProjectPreferences preferences,
            IProgressMonitor monitor) {
        if (preferences.getPlatform().getBundle(Globals.PLUGINID_VE) != null) {
            try {
                ViliContext.getVili().getProjectBuilder().addNature(project,
                        NATURE_ID_VEPROJECT, monitor);
            } catch (CoreException ex) {
                ViliContext.getVili().log("Can't add Ve nature", ex);
                throw new RuntimeException(ex);
            }
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

    @Override
    public boolean saveParameters(IProject project, Mold mold,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IPersistentPreferenceStore store) {
        MapProperties prop = loadApplicationProperties(project);
        updateApplicationProperties(project, prop, preferences, parameters);
        return saveApplicationProperties(project, prop);
    }

    @Override
    public Map<String, Object> loadParameters(IProject project, Mold mold,
            ViliProjectPreferences preferences) {
        MapProperties prop = loadApplicationProperties(project);
        Map<String, Object> parameters = new HashMap<String, Object>();

        parameters.put(Globals.PARAM_TOKENKEY, prop.getProperty(CORE_TOKEN_KEY,
                "org.seasar.ymir.token"));
        parameters.put(Globals.PARAM_WINDOWKEY, prop.getProperty(
                CORE_WINDOW_KEY, "org.seasar.ymir.window"));
        parameters.put(PARAM_AUTORESETCHECKBOXENABLED, prop
                .getProperty(CORE_CHECKBOX_KEY) != null);
        parameters.put(PARAM_CHECKBOXKEY, prop.getProperty(CORE_CHECKBOX_KEY,
                "org.seasar.ymir.checkbox"));
        parameters.put(PARAM_HISTORYAUTORECORDING, PropertyUtils.valueOf(prop
                .getProperty(CORE_HISTORY_AUTORECORDING), false));
        parameters.put(PARAM_OMITSESSIONID, PropertyUtils.valueOf(prop
                .getProperty(CORE_SESSION_OMITSESSIONID), false));
        parameters.put(PARAM_ACCEPTBROWSERSBACKBUTTON, PropertyUtils.valueOf(
                prop.getProperty(CORE_CONVERSATION_ACCEPTBROWSERSBACKBUTTON),
                false));

        String superclass = prop.getProperty(SUPERCLASS, "");
        parameters.put(PARAM_SPECIFYSUPERCLASS, superclass.length() > 0);
        parameters.put(PARAM_SUPERCLASS, superclass);
        parameters.put(PARAM_AUTOGENERATIONENABLED, PropertyUtils.valueOf(prop
                .getProperty(SOURCECREATOR_ENABLE), true));
        parameters.put(PARAM_CREATEBASECLASSES, PropertyUtils.valueOf(prop
                .getProperty(CREATEBASECLASSES), false));
        parameters
                .put(PARAM_DTOSEARCHPATH, prop.getProperty(DTOSEARCHPATH, ""));
        parameters.put(PARAM_INPLACEEDITORENABLED, PropertyUtils.valueOf(prop
                .getProperty(ENABLEINPLACEEDITOR), true));
        parameters.put(PARAM_CONTROLPANELENABLED, PropertyUtils.valueOf(prop
                .getProperty(ENABLECONTROLPANEL), true));
        parameters.put(PARAM_TRYTOUPDATECLASSESWHENTEMPLATEMODIFIED,
                PropertyUtils.valueOf(prop
                        .getProperty(TRYTOUPDATECLASSESWHENTEMPLATEMODIFIED),
                        true));
        parameters.put(PARAM_SORTELEMENTSBYNAME, PropertyUtils.valueOf(prop
                .getProperty(SORTELEMENTSBYNAME), false));
        parameters.put(PARAM_FORMDTOCREATIONFEATUREENABLED, PropertyUtils
                .valueOf(prop.getProperty(FORM_DTO_CREATION_FEATURE_ENABLED),
                        true));
        parameters.put(PARAM_DAOCREATIONFEATUREENABLED, PropertyUtils.valueOf(
                prop.getProperty(DAO_CREATION_FEATURE_ENABLED), true));
        parameters.put(PARAM_DXOCREATIONFEATUREENABLED, PropertyUtils.valueOf(
                prop.getProperty(DXO_CREATION_FEATURE_ENABLED), true));
        parameters.put(PARAM_CONVERTERCREATIONFEATUREENABLED, PropertyUtils
                .valueOf(prop.getProperty(CONVERTER_CREATION_FEATURE_ENABLED),
                        false));

        parameters.put(PARAM_ECLIPSEENABLED, PropertyUtils.valueOf(prop
                .getProperty(ECLIPSE_ENABLED), false));
        parameters.put(PARAM_RESOURCESYNCHRONIZERURL, prop.getProperty(
                RESOURCE_SYNCHRONIZER_URL, ""));

        HotdeployType hotdeployType = HotdeployType.enumOf(prop
                .getProperty(HOTDEPLOY_TYPE));
        if (hotdeployType == null) {
            hotdeployType = HotdeployType.VOID;
        }
        parameters.put(PARAM_HOTDEPLOYTYPE, hotdeployType.getName());

        parameters.put(PARAM_BEANTABLEENABLED, PropertyUtils.valueOf(prop
                .getProperty(BEANTABLE_ENABLED), false));

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
                ViliContext.getVili().log("Can't load: " + file, t); //$NON-NLS-1$
                throw new RuntimeException(t);
            } finally {
                IOUtils.closeQuietly(is);
            }
        }
        return properties;
    }

    void updateApplicationProperties(IProject project, MapProperties prop,
            ViliProjectPreferences preferences, Map<String, Object> parameters) {
        prop.setProperty(CORE_TOKEN_KEY, stringValue(parameters
                .get(PARAM_TOKENKEY)));
        prop.setProperty(CORE_WINDOW_KEY, stringValue(parameters
                .get(PARAM_WINDOWKEY)));
        if (PropertyUtils.valueOf(parameters
                .get(PARAM_AUTORESETCHECKBOXENABLED), false)) {
            prop.setProperty(CORE_CHECKBOX_KEY, stringValue(parameters
                    .get(PARAM_CHECKBOXKEY)));
        }
        prop.setProperty(CORE_HISTORY_AUTORECORDING, booleanValue(parameters
                .get(PARAM_HISTORYAUTORECORDING)));
        prop.setProperty(CORE_SESSION_OMITSESSIONID, booleanValue(parameters
                .get(PARAM_OMITSESSIONID)));
        prop.setProperty(CORE_CONVERSATION_ACCEPTBROWSERSBACKBUTTON,
                booleanValue(parameters.get(PARAM_ACCEPTBROWSERSBACKBUTTON)));

        String superclass = "";
        if (isTrue(parameters.get(PARAM_SPECIFYSUPERCLASS))) {
            superclass = stringValue(parameters.get(PARAM_SUPERCLASS));
        }
        prop.setProperty(SUPERCLASS, superclass);
        prop.setProperty(CREATEBASECLASSES, booleanValue(parameters
                .get(PARAM_CREATEBASECLASSES)));
        prop.setProperty(SOURCECREATOR_ENABLE, booleanValue(parameters
                .get(PARAM_AUTOGENERATIONENABLED)));
        prop.setProperty(FIELDPREFIX, JdtUtils.getFieldPrefix(project));
        prop.setProperty(FIELDSUFFIX, JdtUtils.getFieldSuffix(project));
        prop.setProperty(FIELDSPECIALPREFIX, JdtUtils
                .getFieldSpecialPrefix(project));
        prop.setProperty(DTOSEARCHPATH, stringValue(parameters
                .get(PARAM_DTOSEARCHPATH)));
        prop.setProperty(ENABLEINPLACEEDITOR, booleanValue(parameters
                .get(PARAM_INPLACEEDITORENABLED)));
        prop.setProperty(ENABLECONTROLPANEL, booleanValue(parameters
                .get(PARAM_CONTROLPANELENABLED)));
        prop.setProperty(TRYTOUPDATECLASSESWHENTEMPLATEMODIFIED,
                booleanValue(parameters
                        .get(PARAM_TRYTOUPDATECLASSESWHENTEMPLATEMODIFIED)));
        prop.setProperty(SORTELEMENTSBYNAME, booleanValue(parameters
                .get(PARAM_SORTELEMENTSBYNAME)));
        prop.setProperty(FORM_DTO_CREATION_FEATURE_ENABLED,
                booleanValue(parameters
                        .get(PARAM_FORMDTOCREATIONFEATUREENABLED)));
        prop.setProperty(DAO_CREATION_FEATURE_ENABLED, booleanValue(parameters
                .get(PARAM_DAOCREATIONFEATUREENABLED)));
        prop.setProperty(DXO_CREATION_FEATURE_ENABLED, booleanValue(parameters
                .get(PARAM_DXOCREATIONFEATUREENABLED)));
        prop.setProperty(CONVERTER_CREATION_FEATURE_ENABLED,
                booleanValue(parameters
                        .get(PARAM_CONVERTERCREATIONFEATUREENABLED)));

        prop.setProperty(ECLIPSE_ENABLED, booleanValue(parameters
                .get(PARAM_ECLIPSEENABLED)));
        if (isTrue(parameters.get(PARAM_ECLIPSEENABLED))) {
            String value = stringValue(parameters
                    .get(PARAM_RESOURCESYNCHRONIZERURL));
            if (value.length() > 0) {
                prop.setProperty(RESOURCE_SYNCHRONIZER_URL, value);
            }
            prop.setProperty(ECLIPSE_PROJECTNAME, preferences.getProjectName());
        }

        HotdeployType hotdeployType = HotdeployType
                .enumOf(stringValue(parameters.get(PARAM_HOTDEPLOYTYPE)));
        prop.setProperty(S2CONTAINER_CLASSLOADING_DISABLEHOTDEPLOY, String
                .valueOf(hotdeployType != HotdeployType.S2));
        prop.setProperty(S2CONTAINER_COMPONENTREGISTRATION_DISABLEDYNAMIC,
                String.valueOf(hotdeployType == HotdeployType.VOID));
        prop.setProperty(HOTDEPLOY_TYPE, hotdeployType.getName());

        prop.setProperty(BEANTABLE_ENABLED, booleanValue(parameters
                .get(PARAM_BEANTABLEENABLED)));
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
