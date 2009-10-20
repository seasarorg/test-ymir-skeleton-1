package org.seasar.ymir.vili.skeleton.mobylet_fragment_generic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import net.skirnir.freyja.Element;
import net.skirnir.freyja.TemplateEvaluator;
import net.skirnir.freyja.impl.TemplateEvaluatorImpl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.ymir.vili.skeleton.mobylet_fragment_generic.freyja.NullExpressionEvaluator;
import org.seasar.ymir.vili.skeleton.mobylet_fragment_generic.freyja.WebXmlContext;
import org.seasar.ymir.vili.skeleton.mobylet_fragment_generic.freyja.WebXmlTagEvaluator;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.InclusionType;
import org.t2framework.vili.ProjectBuilder;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.model.maven.Dependency;

public class Configurator extends AbstractConfigurator implements Globals {
    private TemplateEvaluator webXmlEvaluator = new TemplateEvaluatorImpl(
            new WebXmlTagEvaluator(), new NullExpressionEvaluator());

    @Override
    public void processBeforeExpanding(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        monitor.beginTask("process before expanding", 1);
        try {
            ProjectBuilder projectBuilder = ViliContext.getVili()
                    .getProjectBuilder();

            parameters.put(PARAM_FREYJAEXISTS, projectBuilder.getDependency(
                    project, "net.skirnir.freyja", "freyja", true) != null);

            Dependency dependency = projectBuilder.getDependency(project,
                    "org.seasar.ymir", "ymir-zpt", true);
            parameters.put(PARAM_YMIRZPTVERSION,
                    dependency != null ? dependency.getVersion() : null);
            parameters.put(PARAM_YMIRZPTEXISTS, dependency != null);

            parameters
                    .put(PARAM_ADDS2EXTENSION, projectBuilder.getDependency(
                            project, "org.seasar.container", "s2-framework",
                            true) != null);
        } catch (CoreException ex) {
            ViliContext.getVili().getLog().log(ex.getStatus());
            throw new RuntimeException(ex);
        } finally {
            monitor.done();
        }
    }

    @Override
    public InclusionType shouldExpand(String path, String resolvedPath,
            IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters) {
        if (path.equals(PATH_APP_DICON)) {
            return PropertyUtils.valueOf(parameters.get(PARAM_ADDS2EXTENSION),
                    true) ? InclusionType.INCLUDED : InclusionType.EXCLUDED;
        }

        return super.shouldExpand(path, resolvedPath, project, behavior,
                preferences, parameters);
    }

    @Override
    public void processAfterExpanded(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        monitor.beginTask("process after expanded", 1);
        try {
            // web.xmlにMobyletのためのエントリを追加する。
            IFile file = project.getFile(PATH_WEB_XML);
            if (file.exists()) {
                String charset = file.getCharset();

                Element[] elements;
                InputStream is = null;
                try {
                    is = file.getContents();
                    elements = webXmlEvaluator.parse(new InputStreamReader(is,
                            charset));
                } finally {
                    IOUtils.closeQuietly(is);
                }

                WebXmlContext context = (WebXmlContext) webXmlEvaluator
                        .newContext();
                webXmlEvaluator.evaluate(context, elements);
                if (!context.isMobyletFound()) {
                    context.setMode(WebXmlContext.Mode.MODIFY);
                    String evaluated = webXmlEvaluator.evaluate(context,
                            elements);

                    file.setContents(new ByteArrayInputStream(evaluated
                            .getBytes(charset)), true, true,
                            new SubProgressMonitor(monitor, 1));
                } else {
                    monitor.worked(1);
                }
            }
        } catch (CoreException ex) {
            ViliContext.getVili().getLog().log(ex.getStatus());
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            ViliContext.getVili().log(ex);
            throw new RuntimeException(ex);
        } finally {
            monitor.done();
        }
    }
}
