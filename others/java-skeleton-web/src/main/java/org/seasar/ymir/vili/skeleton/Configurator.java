package org.seasar.ymir.vili.skeleton;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.InclusionType;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliProjectPreferences;

public class Configurator extends AbstractConfigurator implements Globals {
    @Override
    public InclusionType shouldExpand(String path, String resolvedPath,
            IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters) {
        if (path.startsWith(PATHPREFIX_SERVICE_H2)) {
            if ("h2".equals(preferences.getDatabase().getType())) {
                return InclusionType.INCLUDED;
            } else {
                return InclusionType.EXCLUDED;
            }
        }

        return super.shouldExpand(path, resolvedPath, project, behavior,
                preferences, parameters);
    }
}
