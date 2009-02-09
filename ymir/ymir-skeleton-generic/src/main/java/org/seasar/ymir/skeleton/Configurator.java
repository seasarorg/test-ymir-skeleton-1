package org.seasar.ymir.skeleton;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.ymir.vili.AbstractConfigurator;
import org.seasar.ymir.vili.Activator;
import org.seasar.ymir.vili.ViliBehavior;
import org.seasar.ymir.vili.ViliProjectPreferences;
import org.seasar.ymir.vili.maven.util.ArtifactUtils;

public class Configurator extends AbstractConfigurator {
    private static final String KEY_PRODUCTVERSION_CANDIDATES = "template.parameter.productVersion.candidates";

    private static final String KEY_PRODUCTVERSION_DEFAULT = "template.parameter.productVersion.default";

    private static final String KEY_YMIR_PREREQUISITE = "ymir.prerequisite";

    private static final String GROUPID_YMIR = "org.seasar.ymir";

    private static final String ARTIFACTID_YMIR = "ymir-extension";

    @Override
    public void start(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences) {
        MapProperties properties = behavior.getProperties();

        String prerequisite = behavior.getProperties().getProperty(
                KEY_YMIR_PREREQUISITE);
        List<String> list = new ArrayList<String>();
        for (String version : Activator.getArtifactResolver().getVersions(
                GROUPID_YMIR, ARTIFACTID_YMIR, false)) {
            if (ArtifactUtils.compareVersions(version, prerequisite) >= 0) {
                list.add(version);
            }
        }
        String[] versions = list.toArray(new String[0]);

        properties.setProperty(KEY_PRODUCTVERSION_CANDIDATES, PropertyUtils
                .join(versions));
        if (versions.length > 0) {
            properties.setProperty(KEY_PRODUCTVERSION_DEFAULT, versions[0]);
        }
        behavior.notifyPropertiesChanged();
    }
}
