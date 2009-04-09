package org.seasar.ymir.skeleton.ysp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.io.IOUtils;
import org.t2framework.vili.AbstractConfigurator;
import org.t2framework.vili.ViliBehavior;
import org.t2framework.vili.ViliProjectPreferences;

public class Configurator extends AbstractConfigurator {
    private static final String PATH_MESSAGES_JA_XPROPERTIES = "src/main/resources/messages_ja.xproperties";

    private static final String PATH_MESSAGES_XPROPERTIES = "src/main/resources/messages.xproperties";

    @Override
    public void processBeforeExpanding(IProject project, ViliBehavior behavior,
            ViliProjectPreferences preferences, Map<String, Object> parameters,
            IProgressMonitor monitor) {
        monitor.beginTask("Configure", 2);
        InputStream is = null;
        try {
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
}
