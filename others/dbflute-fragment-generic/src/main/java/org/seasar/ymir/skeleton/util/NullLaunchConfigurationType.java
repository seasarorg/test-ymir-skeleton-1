package org.seasar.ymir.skeleton.util;

import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;

@SuppressWarnings("unchecked")
class NullLaunchConfigurationType implements ILaunchConfigurationType {
    public String getAttribute(String arg0) {
        return null;
    }

    public String getCategory() {
        return null;
    }

    public String getContributorName() {
        return null;
    }

    public ILaunchConfigurationDelegate getDelegate() throws CoreException {
        return null;
    }

    public ILaunchConfigurationDelegate getDelegate(String arg0)
            throws CoreException {
        return null;
    }

    public ILaunchDelegate[] getDelegates(Set arg0) throws CoreException {
        return null;
    }

    public String getIdentifier() {
        return "";
    }

    public String getName() {
        return null;
    }

    public String getPluginIdentifier() {
        return null;
    }

    public ILaunchDelegate getPreferredDelegate(Set arg0) throws CoreException {
        return null;
    }

    public String getSourceLocatorId() {
        return null;
    }

    public ISourcePathComputer getSourcePathComputer() {
        return null;
    }

    public Set getSupportedModeCombinations() {
        return null;
    }

    public Set getSupportedModes() {
        return null;
    }

    public boolean isPublic() {
        return false;
    }

    public ILaunchConfigurationWorkingCopy newInstance(IContainer arg0,
            String arg1) throws CoreException {
        return null;
    }

    public void setPreferredDelegate(Set arg0, ILaunchDelegate arg1)
            throws CoreException {
    }

    public boolean supportsMode(String arg0) {
        return false;
    }

    public boolean supportsModeCombination(Set arg0) {
        return false;
    }

    public Object getAdapter(Class arg0) {
        return null;
    }
}
