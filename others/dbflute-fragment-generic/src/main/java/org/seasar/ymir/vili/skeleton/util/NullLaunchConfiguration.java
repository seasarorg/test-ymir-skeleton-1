package org.seasar.ymir.vili.skeleton.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;

@SuppressWarnings("unchecked")
class NullLaunchConfiguration implements ILaunchConfiguration {
    private ILaunchConfigurationType type = new NullLaunchConfigurationType();

    public boolean contentsEqual(ILaunchConfiguration arg0) {
        return false;
    }

    public ILaunchConfigurationWorkingCopy copy(String arg0)
            throws CoreException {
        return null;
    }

    public void delete() throws CoreException {
    }

    public boolean exists() {
        return false;
    }

    public boolean getAttribute(String arg0, boolean arg1) throws CoreException {
        return false;
    }

    public int getAttribute(String arg0, int arg1) throws CoreException {
        return 0;
    }

    public List getAttribute(String arg0, List arg1) throws CoreException {
        return null;
    }

    public Set getAttribute(String arg0, Set arg1) throws CoreException {
        return null;
    }

    public Map getAttribute(String arg0, Map arg1) throws CoreException {
        return null;
    }

    public String getAttribute(String arg0, String arg1) throws CoreException {
        return null;
    }

    public Map getAttributes() throws CoreException {
        return null;
    }

    public String getCategory() throws CoreException {
        return null;
    }

    public IFile getFile() {
        return null;
    }

    public IPath getLocation() {
        return null;
    }

    public IResource[] getMappedResources() throws CoreException {
        return null;
    }

    public String getMemento() throws CoreException {
        return null;
    }

    public Set getModes() throws CoreException {
        return null;
    }

    public String getName() {
        return null;
    }

    public ILaunchDelegate getPreferredDelegate(Set arg0) throws CoreException {
        return null;
    }

    public ILaunchConfigurationType getType() throws CoreException {
        return type;
    }

    public ILaunchConfigurationWorkingCopy getWorkingCopy()
            throws CoreException {
        return null;
    }

    public boolean isLocal() {
        return false;
    }

    public boolean isMigrationCandidate() throws CoreException {
        return false;
    }

    public boolean isReadOnly() {
        return false;
    }

    public boolean isWorkingCopy() {
        return false;
    }

    public ILaunch launch(String arg0, IProgressMonitor arg1)
            throws CoreException {
        return null;
    }

    public ILaunch launch(String arg0, IProgressMonitor arg1, boolean arg2)
            throws CoreException {
        return null;
    }

    public ILaunch launch(String arg0, IProgressMonitor arg1, boolean arg2,
            boolean arg3) throws CoreException {
        return null;
    }

    public void migrate() throws CoreException {
    }

    public boolean supportsMode(String arg0) throws CoreException {
        return false;
    }

    public Object getAdapter(Class arg0) {
        return null;
    }
}
