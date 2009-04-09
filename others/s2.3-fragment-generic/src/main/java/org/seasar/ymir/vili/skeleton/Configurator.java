package org.seasar.ymir.vili.skeleton;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.Activator;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.maven.util.ArtifactUtils;

public class Configurator extends AbstractConfigurator {
    private static final String KEY_PRODUCTVERSION = "productVersion";

    private static final String KEY_BASEVERSION = "product.baseVersion";

    private static final String KEY_PREREQUISITE = "product.prerequisite";

    private static final String GROUPID = "org.seasar.container";

    private static final String ARTIFACTID = "s2-extension";

    @Override
    public void start(IProject project, ViliBehavior behavior,
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

        behavior.setTemplateParameterCandidates(KEY_PRODUCTVERSION, versions);
        if (behavior.getTemplateParameterDefault(KEY_PRODUCTVERSION).length() == 0
                && versions.length > 0) {
            behavior.setTemplateParameterDefault(KEY_PRODUCTVERSION,
                    versions[0]);
        }
    }
}
