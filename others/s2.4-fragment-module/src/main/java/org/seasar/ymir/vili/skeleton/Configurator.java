package org.seasar.ymir.vili.skeleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.InclusionType;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.maven.util.ArtifactUtils;

public class Configurator extends AbstractConfigurator {
    private static final String KEY_PRODUCTVERSION = "productVersion";

    private static final String KEY_BASEVERSION = "product.baseVersion";

    private static final String KEY_PREREQUISITE = "product.prerequisite";

    private static final String GROUPID = "org.seasar.container";

    private static final String ARTIFACTID = "s2-extension";

    private static final String PATH_JDBCXADATASOURCE_DICON = "src/test/resources/jdbc+xaDataSource.dicon";

    private static final Set<String> UNSAFE_VERSIONS = Collections
            .unmodifiableSet(new HashSet<String>(Arrays.asList(new String[] {
                "2.4.35", "2.4.36" })));

    @Override
    public void start(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences) {
        String baseVersionPrefix = behavior.getProperty(KEY_BASEVERSION);
        if (baseVersionPrefix != null) {
            baseVersionPrefix += ".";
        }
        String prerequisite = behavior.getProperty(KEY_PREREQUISITE);
        List<String> list = new ArrayList<String>();
        for (String version : ViliContext.getVili().getArtifactResolver()
                .getVersions(GROUPID, ARTIFACTID, false)) {
            if (ArtifactUtils.compareVersions(version, prerequisite) < 0) {
                continue;
            }
            if (baseVersionPrefix != null
                    && !version.startsWith(baseVersionPrefix)) {
                continue;
            }
            if (UNSAFE_VERSIONS.contains(version)) {
                continue;
            }

            list.add(version);
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

    @Override
    public InclusionType shouldExpand(String path, String resolvedPath,
            IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters) {
        if (path.equals(PATH_JDBCXADATASOURCE_DICON)) {
            return preferences.isUseDatabase() ? InclusionType.INCLUDED
                    : InclusionType.EXCLUDED;
        } else {
            return super.shouldExpand(path, resolvedPath, project, behavior,
                    preferences, parameters);
        }
    }
}
