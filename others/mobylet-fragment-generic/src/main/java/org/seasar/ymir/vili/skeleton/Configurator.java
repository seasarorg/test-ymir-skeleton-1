package org.seasar.ymir.vili.skeleton;

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
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.ymir.vili.skeleton.freyja.NullExpressionEvaluator;
import org.seasar.ymir.vili.skeleton.freyja.WebXmlContext;
import org.seasar.ymir.vili.skeleton.freyja.WebXmlTagEvaluator;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.model.maven.Dependency;
import org.t2framework.vili.model.maven.Exclusion;
import org.t2framework.vili.model.maven.Exclusions;

public class Configurator extends AbstractConfigurator implements Globals {
    private TemplateEvaluator webXmlEvaluator = new TemplateEvaluatorImpl(
            new WebXmlTagEvaluator(), new NullExpressionEvaluator());

    @Override
    public Dependency[] mergePomDependencies(
            Map<Dependency, Dependency> dependencyMap,
            Map<Dependency, Dependency> fragmentDependencyMap,
            IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters) {
        // Freyjaを使っている場合はmobylet-taglibsをdependencyから外す。
        if (isUsingFreyja(dependencyMap)) {
            fragmentDependencyMap.remove(new Dependency("org.seasar.mobylet",
                    "mobylet-taglibs"));
        }
        return null;
    }

    private boolean isUsingFreyja(Map<Dependency, Dependency> dependencyMap) {
        if (dependencyMap.containsKey(new Dependency("net.skirnir.freyja",
                "freyja"))) {
            return true;
        }

        if (dependencyMap.containsKey(new Dependency("org.seasar.ymir",
                "ymir-zpt"))) {
            return true;
        }

        Dependency dependency = dependencyMap.get(new Dependency(
                "org.seasar.ymir", "ymir-extension"));
        if (dependency != null) {
            Exclusions exclusions = dependency.getExclusions();
            if (exclusions != null) {
                for (Exclusion exclusion : exclusions.getExclusions()) {
                    if ("org.seasar.ymir".equals(exclusion.getGroupId())
                            && "ymir-zpt".equals(exclusion.getArtifactId())) {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void processAfterExpanded(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        monitor.beginTask("Add entries for Mobylet to web.xml", 1);
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
