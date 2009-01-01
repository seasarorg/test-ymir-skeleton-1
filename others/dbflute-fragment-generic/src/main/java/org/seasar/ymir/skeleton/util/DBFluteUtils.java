package org.seasar.ymir.skeleton.util;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.ymir.vili.Activator;

public class DBFluteUtils {
    public static final String PREFIX_DBFLUTE = "dbflute_";

    private static final String KEY_OS_NAME = "os.name";

    private static final String WINDOWS = "windows";

    private static final String EXTENSION_WINDOWS = ".bat";

    private static final String EXTENSION_OTHERS = ".sh";

    private DBFluteUtils() {
    }

    public static String getDBFluteRoot(IProject project) {
        if (project != null) {
            try {
                for (IResource member : project.members()) {
                    if (member.getName().startsWith(PREFIX_DBFLUTE)) {
                        return member.getName();
                    }
                }
            } catch (CoreException ex) {
                Activator.log(ex);
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

    public static void execute(IFile file) throws IOException {
        if (!file.exists()) {
            return;
        }

        ProcessBuilder pb;
        if (file.getName().endsWith(EXTENSION_WINDOWS)) {
            // Windowsの場合。
            //            pb = new ProcessBuilder("cmd", "/c", "start", "cmd", "/c", file
            //                    .getName());
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
        final MessageConsole console = new MessageConsole("Vili Console", null);
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
    }
}
