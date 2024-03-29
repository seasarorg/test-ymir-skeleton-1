package org.seasar.ymir.vili.skeleton.dbflute_fragment_generic.util;

import static org.seasar.ymir.vili.skeleton.dbflute_fragment_generic.Globals.PATH_MYDBFLUTE;
import static org.seasar.ymir.vili.skeleton.dbflute_fragment_generic.Globals.PREFIX_DBFLUTE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.seasar.dbflute.infra.dfprop.DfPropFile;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.ymir.vili.skeleton.dbflute_fragment_generic.Globals;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;

public class DBFluteUtils {
    private static final String KEY_OS_NAME = "os.name";

    private static final String WINDOWS = "windows";

    private static final String EXTENSION_WINDOWS = ".bat";

    private static final String EXTENSION_OTHERS = ".sh";

    private DBFluteUtils() {
    }

    public static String getDBFluteClientRoot(IProject project) {
        if (project != null) {
            try {
                for (IResource member : project.members()) {
                    if (member.getName().startsWith(PREFIX_DBFLUTE)) {
                        return member.getName();
                    }
                }
            } catch (CoreException ex) {
                ViliContext.getVili().log(ex);
            }
        }
        return null;
    }

    public static String getBatchExtension() {
        if (System.getProperty(KEY_OS_NAME).toLowerCase().indexOf(WINDOWS) >= 0) {
            return EXTENSION_WINDOWS;
        } else {
            return EXTENSION_OTHERS;
        }
    }

    public static int execute(IFile file, boolean waitFor) throws IOException {
        if (!file.exists()) {
            return 0;
        }

        ProcessBuilder pb;
        if (file.getName().endsWith(EXTENSION_WINDOWS)) {
            // Windowsの場合。
            pb = new ProcessBuilder("cmd", "/c", file.getName());
        } else {
            // それ以外の場合。
            pb = new ProcessBuilder("/bin/sh", file.getName());
        }
        pb.directory(file.getLocation().toFile().getParentFile());
        pb.environment().put("answer", "y");
        Process process = pb.start();
        OutputStream os = null;
        try {
            os = process.getOutputStream();
            os.write(" ".getBytes());
        } finally {
            IOUtils.closeQuietly(os);
        }

        IConsoleManager manager = ConsolePlugin.getDefault()
                .getConsoleManager();
        final MessageConsole console = new MessageConsole("Vili Console ["
                + file.getName() + "]", null);
        manager.addConsoles(new IConsole[] { console });
        manager.showConsoleView(console);

        IProcess eclipseProcess = DebugPlugin.newProcess(new Launch(
                new NullLaunchConfiguration(), ILaunchManager.RUN_MODE, null),
                process, "Vili Process");
        eclipseProcess.getStreamsProxy().getOutputStreamMonitor().addListener(
                new IStreamListener() {
                    public void streamAppended(String text,
                            IStreamMonitor monitor) {
                        MessageConsoleStream stream = console
                                .newMessageStream();
                        //                        stream.setColor(new Color(Workbench.getInstance()
                        //                                .getDisplay(), 0, 0, 255));
                        stream.print(text);
                    }
                });
        eclipseProcess.getStreamsProxy().getErrorStreamMonitor().addListener(
                new IStreamListener() {
                    public void streamAppended(String text,
                            IStreamMonitor monitor) {
                        MessageConsoleStream stream = console
                                .newMessageStream();
                        //                        stream.setColor(new Color(Workbench.getInstance()
                        //                                .getDisplay(), 255, 0, 0));
                        stream.print(text);
                    }
                });

        if (waitFor) {
            try {
                return process.waitFor();
            } catch (InterruptedException ignore) {
            }
        }

        return 0;
    }

    public static boolean oldVersionExists(IProject project) {
        return project != null && project.getFolder(PATH_MYDBFLUTE).exists();
    }

    public static final String getDefaultDBFluteProjectName(IProject project,
            ViliProjectPreferences preferences) {
        String name = preferences.getProjectName();
        String root = getDBFluteClientRoot(project);
        if (root != null) {
            name = root.substring(PREFIX_DBFLUTE.length());
        }
        return name;
    }

    public static void deleteOldVersion(IProject project,
            IProgressMonitor monitor) {
        IFolder mydbflute = project.getFolder(Globals.PATH_MYDBFLUTE);
        if (!mydbflute.exists()) {
            return;
        }

        try {
            IResource[] members = mydbflute.members();
            monitor.beginTask("Delete old resources", members.length);
            try {
                for (IResource member : members) {
                    member.delete(true, new SubProgressMonitor(monitor, 1));
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }
                }
            } finally {
                monitor.done();
            }
        } catch (CoreException ex) {
            ViliContext.getVili().log(ex);
            throw new RuntimeException(ex);
        }
    }

    public static IFile getOldBuildProperties(IProject project) {
        try {
            String root = getDBFluteClientRoot(project);
            if (root == null) {
                return null;
            }

            IFolder client = project.getFolder(root);
            if (!client.exists()) {
                return null;
            }

            for (IResource member : client.members()) {
                String name = member.getName();
                if ((name.startsWith("build-") && name.endsWith(".properties"))
                        && member.getType() == IResource.FILE) {
                    return (IFile) member;
                }
            }

            return null;
        } catch (CoreException ex) {
            ViliContext.getVili().log(ex);
            throw new RuntimeException(ex);
        }
    }

    public static IFile getBuildProperties(IProject project) {
        String root = getDBFluteClientRoot(project);
        if (root == null) {
            return null;
        }

        return project.getFile(root + "/" + Globals.NAME_BUILDPROPERTIES);
    }

    public static Map<String, Object> readDfprop(IProject project, String name) {
        String root = getDBFluteClientRoot(project);
        if (root == null) {
            return new LinkedHashMap<String, Object>();
        }

        return readDfprop(project.getFile(root + "/dfprop/" + name));
    }

    public static Map<String, Object> readDfprop(IFile file) {
        if (!file.exists()) {
            return new LinkedHashMap<String, Object>();
        }

        InputStream is = null;
        try {
            is = file.getContents();
            return new DfPropFile().readMap(is);
        } catch (CoreException ex) {
            throw new RuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }
}
