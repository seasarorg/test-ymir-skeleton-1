package org.seasar.ymir.vili.skeleton.mobylet_fragment_generic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.skirnir.freyja.Element;
import net.skirnir.freyja.TemplateEvaluator;
import net.skirnir.freyja.impl.TemplateEvaluatorImpl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.ymir.vili.skeleton.mobylet_fragment_generic.freyja.Environment;
import org.seasar.ymir.vili.skeleton.mobylet_fragment_generic.freyja.NullExpressionEvaluator;
import org.seasar.ymir.vili.skeleton.mobylet_fragment_generic.freyja.WebXmlContext;
import org.seasar.ymir.vili.skeleton.mobylet_fragment_generic.freyja.WebXmlTagEvaluator;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.InclusionType;
import org.t2framework.vili.Mold;
import org.t2framework.vili.ProjectBuilder;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.maven.util.ArtifactUtils;
import org.t2framework.vili.model.maven.Dependency;

public class Configurator extends AbstractConfigurator implements Globals,
        MobyletPropertyKeys {
    private TemplateEvaluator webXmlEvaluator = new TemplateEvaluatorImpl(
            new WebXmlTagEvaluator(), new NullExpressionEvaluator());

    private boolean freyjaExists;

    private String ymirZptVersion;

    private boolean s2Exists;

    private boolean upgrade;

    @Override
    public void processBeforeExpanding(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        monitor.beginTask("process before expanding", 1);
        try {
            ProjectBuilder projectBuilder = ViliContext.getVili()
                    .getProjectBuilder();

            freyjaExists = projectBuilder.getDependency(project,
                    "net.skirnir.freyja", "freyja", true) != null;
            parameters.put(PARAM_FREYJAEXISTS, freyjaExists);

            Dependency dependency = projectBuilder.getDependency(project,
                    "org.seasar.ymir", "ymir-zpt", true);
            ymirZptVersion = dependency != null ? dependency.getVersion()
                    : null;
            parameters.put(PARAM_YMIRZPTVERSION, ymirZptVersion);
            parameters.put(PARAM_YMIRZPTEXISTS, dependency != null);

            s2Exists = projectBuilder.getDependency(project,
                    "org.seasar.container", "s2-framework", true) != null;
            parameters.put(PARAM_ADDS2EXTENSION, s2Exists);

            upgrade = project.getFile(PATH_PREFS).exists();
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
        } else if (upgrade && path.equals(PATH_README)) {
            // アップグレードの時はREADMEを展開しない。
            return InclusionType.EXCLUDED;
        } else if (path.equals(PATH_MOBYLET_IMAGE_PROPERTIES)) {
            // 既に存在する場合は上書きしない。
            return project.getFile(path).exists() ? InclusionType.EXCLUDED
                    : InclusionType.INCLUDED;
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
                if (freyjaExists) {
                    context.addEnvironment(Environment.FREYJA);
                }
                if (ymirZptVersion != null) {
                    context.addEnvironment(Environment.YMIR_ZPT);
                    if (ArtifactUtils.compareVersions(ymirZptVersion,
                            "1.0.7-SNAPSHOT") >= 0) {
                        context.addEnvironment(Environment.YMIR_ZPT_1_0_7);
                    }
                }
                if (s2Exists) {
                    context.addEnvironment(Environment.SEASAR2);
                }
                context
                        .setLocalImageResizingFeatureEnabled(((Boolean) parameters
                                .get(PARAM_ENABLELOCALIMAGERESIZING))
                                .booleanValue());
                context.setLocalImageUrlPatterns(((String) parameters
                        .get(PARAM_LOCALIMAGEURLPATTERN)).split(","));

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

    @Override
    public boolean saveParameters(IProject project, Mold mold,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IPersistentPreferenceStore store) {
        MapProperties imageProp = loadProperties(project,
                PATH_MOBYLET_IMAGE_PROPERTIES);
        updateProperties(project, imageProp, preferences, parameters);
        return saveProperties(project, PATH_MOBYLET_IMAGE_PROPERTIES, imageProp);
    }

    @Override
    public Map<String, Object> loadParameters(IProject project, Mold mold,
            ViliProjectPreferences preferences) {
        Map<String, Object> parameters = new HashMap<String, Object>();

        MapProperties imageProp = loadProperties(project,
                PATH_MOBYLET_IMAGE_PROPERTIES);

        parameters.put(PARAM_IMAGESOURCELOCALLIMITSIZE, imageProp.getProperty(
                KEY_IMAGE_SOURCE_LOCAL_LIMIT_SIZE, "0"));

        return parameters;
    }

    MapProperties loadProperties(IProject project, String path) {
        MapProperties properties = new MapProperties(
                new TreeMap<String, String>());
        IFile file = project.getFile(path);
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

    void updateProperties(IProject project, MapProperties prop,
            ViliProjectPreferences preferences, Map<String, Object> parameters) {
        prop.setProperty(KEY_IMAGE_SOURCE_LOCAL_LIMIT_SIZE,
                stringValue(parameters.get(PARAM_IMAGESOURCELOCALLIMITSIZE)));
    }

    boolean saveProperties(IProject project, String path, MapProperties prop) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            prop.store(baos);

            ViliContext.getVili().getProjectBuilder().writeFile(
                    project.getFile(path),
                    new ByteArrayInputStream(baos.toByteArray()),
                    new NullProgressMonitor());
            return true;
        } catch (Throwable t) {
            ViliContext.getVili().log("Can't save app.properties", t); //$NON-NLS-1$
            return false;
        }
    }

    private String stringValue(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return String.valueOf(obj);
        }
    }
}
