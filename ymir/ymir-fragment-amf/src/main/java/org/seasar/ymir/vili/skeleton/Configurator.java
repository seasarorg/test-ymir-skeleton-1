package org.seasar.ymir.vili.skeleton;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.model.maven.Dependency;

public class Configurator extends AbstractConfigurator {
    private static final String KEY_PRODUCTVERSION = "productVersion";

    private static final String GROUPID_YMIR = "org.seasar.ymir";

    private static final String ARTIFACTID_YMIR = "ymir-extension";

    @Override
    public void processBeforeExpanding(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        try {
            Dependency dependency = ViliContext.getVili().getProjectBuilder()
                    .getDependency(project, GROUPID_YMIR, ARTIFACTID_YMIR);
            if (dependency != null) {
                parameters.put(KEY_PRODUCTVERSION, dependency.getVersion());
            }
        } catch (CoreException ex) {
            ViliContext.getVili().log(ex);
            throw new RuntimeException(ex);
        }
    }
}
