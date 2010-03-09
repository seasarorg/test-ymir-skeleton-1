package org.seasar.ymir.vili.skeleton.ysp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import net.skirnir.freyja.Element;
import net.skirnir.freyja.TemplateEvaluator;
import net.skirnir.freyja.impl.TemplateEvaluatorImpl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.ymir.vili.skeleton.ysp.freyja.NullExpressionEvaluator;
import org.seasar.ymir.vili.skeleton.ysp.freyja.WebXmlContext;
import org.seasar.ymir.vili.skeleton.ysp.freyja.WebXmlTagEvaluator;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.model.maven.Dependency;

public class Configurator extends AbstractConfigurator implements Globals {
    private TemplateEvaluator webXmlEvaluator = new TemplateEvaluatorImpl(
            new WebXmlTagEvaluator(), new NullExpressionEvaluator());

    private static final String GROUPID_YMIR = "org.seasar.ymir";

    private static final String ARTIFACTID_YMIR = "ymir-extension";

    @Override
    public void processBeforeExpanding(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        monitor.beginTask("Configure", 3);
        InputStream is = null;
        try {
            Dependency dependency = ViliContext.getVili().getProjectBuilder()
                    .getDependency(project, GROUPID_YMIR, ARTIFACTID_YMIR);
            if (dependency != null) {
                parameters.put(KEY_PRODUCTVERSION, dependency.getVersion());
            }
            monitor.worked(1);

            IFile jaFile = project.getFile(PATH_MESSAGES_JA_XPROPERTIES);
            if (jaFile.exists()) {
                MapProperties jaProp = new MapProperties();
                is = jaFile.getContents();
                jaProp.load(is, "UTF-8");
                is.close();
                is = null;

                @SuppressWarnings("unchecked")
                MapProperties prop = new MapProperties(new TreeMap());
                IFile file = project.getFile(PATH_MESSAGES_XPROPERTIES);
                if (file.exists()) {
                    is = file.getContents();
                    prop.load(is, "UTF-8");
                    is.close();
                    is = null;
                }

                for (Enumeration<?> enm = jaProp.propertyNames(); enm
                        .hasMoreElements();) {
                    String name = (String) enm.nextElement();
                    prop.setProperty(name, jaProp.getProperty(name));
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                prop.store(baos, "UTF-8");
                is = new ByteArrayInputStream(baos.toByteArray());

                if (file.exists()) {
                    file.setContents(is, true, true, new SubProgressMonitor(
                            monitor, 1));
                } else {
                    file.create(is, true, new SubProgressMonitor(monitor, 1));
                }

                jaFile.delete(true, true, new SubProgressMonitor(monitor, 1));
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            IOUtils.closeQuietly(is);
            monitor.done();
        }
    }

    @Override
    public void processAfterExpanded(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        monitor.beginTask("process after expanded", 1);
        try {
            // web.xmlにScaffoldingのためのエントリを追加する。
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

                context.setMode(WebXmlContext.Mode.MODIFY);
                String evaluated = webXmlEvaluator.evaluate(context, elements);

                file.setContents(new ByteArrayInputStream(evaluated
                        .getBytes(charset)), true, true,
                        new SubProgressMonitor(monitor, 1));
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
