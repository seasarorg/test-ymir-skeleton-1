package org.seasar.ymir.vili.skeleton.generator.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardDialog;
import org.seasar.cms.pluggable.ClassTraverser;
import org.seasar.framework.util.ClassTraversal.ClassHandler;
import org.seasar.ymir.vili.skeleton.generator.Globals;
import org.seasar.ymir.vili.skeleton.generator.IGenerator;
import org.seasar.ymir.vili.skeleton.generator.annotation.GUI;
import org.seasar.ymir.vili.skeleton.generator.ui.GenerateWizard;
import org.t2framework.vili.IAction;
import org.t2framework.vili.ViliContext;
import org.t2framework.vili.ViliProjectPreferences;
import org.t2framework.vili.util.ProjectClassLoader;
import org.t2framework.vili.util.WorkbenchUtils;

public class GenerateAction implements IAction, Globals {
    public void run(IProject project, ViliProjectPreferences preferences) {
        List<Class<IGenerator<?>>> generatorClasses = getGeneratorClassesBoundToGUI(
                project, preferences.getRootPackageName());
        if (generatorClasses.isEmpty()) {
            return;
        }

        IPreferenceStore store = ViliContext.getVili().getMoldPreferenceStore(
                project, GROUPID, ARTIFACTID);
        String targetProjectName = store.getString(PARAM_TARGETPROJECTNAME);
        String targetRootPackageName = store
                .getString(PARAM_TARGETROOTPACKAGENAME);
        new WizardDialog(WorkbenchUtils.getShell(), new GenerateWizard(
                generatorClasses, targetProjectName, targetRootPackageName))
                .open();
    }

    private List<Class<IGenerator<?>>> getGeneratorClassesBoundToGUI(
            IProject project, String rootPackageName) {
        final ClassLoader projectClassLoader = new ProjectClassLoader(JavaCore
                .create(project), getClass().getClassLoader());
        String landmarkClassName = rootPackageName + ".Landmark";
        Class<?> landmarkClass;
        try {
            landmarkClass = projectClassLoader.loadClass(landmarkClassName);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Landmark class (" + landmarkClassName
                    + ") not found", ex);
        }

        final List<Class<IGenerator<?>>> generatorClasses = new ArrayList<Class<IGenerator<?>>>();
        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(projectClassLoader);

            ClassTraverser traverser = new ClassTraverser();
            traverser.addReferenceClass(landmarkClass);
            traverser.addClassPattern(rootPackageName, ".*");
            traverser.setClassHandler(new ClassHandler() {
                public void processClass(String packageName,
                        String shortClassName) {
                    String className = packageName + "." + shortClassName;
                    try {
                        @SuppressWarnings("unchecked")
                        Class<IGenerator<?>> generatorClass = (Class<IGenerator<?>>) projectClassLoader
                                .loadClass(className);
                        if (generatorClass.isAnnotationPresent(GUI.class)) {
                            generatorClasses.add(generatorClass);
                        }
                    } catch (ClassNotFoundException ex) {
                        ViliContext.getVili().log(
                                "Cannot load class: " + className, ex);
                    }
                }
            });
            traverser.traverse();
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
        }

        return generatorClasses;
    }
}
