package org.seasar.ymir.vili.skeleton.generator;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliProjectPreferences;

public class Configurator extends AbstractConfigurator implements Globals {
    @Override
    public void processBeforeExpanding(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        parameters.put(PARAM_TARGETPROJECTSUFFIX,
                getProjectSuffix((String) parameters
                        .get(PARAM_TARGETPROJECTNAME)));
    }

    private String getProjectSuffix(String projectName) {
        if (projectName == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String tkn : projectName.split("[-\\.]")) {
            sb.append(capitalize(tkn));
        }
        return sb.toString();
    }

    private String capitalize(String string) {
        if (string == null || string.length() == 0) {
            return string;
        } else {
            return String.valueOf(Character.toUpperCase(string.charAt(0)))
                    + string.substring(1);
        }
    }
}
