package org.seasar.ymir.vili.skeleton.mobylet_fragment_generic.freyja;

import java.util.ArrayList;
import java.util.List;

import org.t2framework.vili.maven.util.ArtifactUtils;

import net.skirnir.freyja.impl.TemplateContextImpl;

public class WebXmlContext extends TemplateContextImpl {
    public enum Mode {
        ANALYZE, MODIFY;
    }

    private Mode mode = Mode.ANALYZE;

    private boolean mobyletFound;

    private boolean mobyletFilterAlreadyAdded;

    private boolean mobyletFilterMappingAlreadyAdded;

    private boolean freyjaServletAlreadyModified;

    private List<String> freyjaURLPatterns = new ArrayList<String>();

    private String ymirZptVersion;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public boolean isMobyletFound() {
        return mobyletFound;
    }

    public void setMobyletFound(boolean mobyletFound) {
        this.mobyletFound = mobyletFound;
    }

    public boolean isMobyletFilterAlreadyAdded() {
        return mobyletFilterAlreadyAdded;
    }

    public void setMobyletFilterAlreadyAdded(boolean mobyletFilterAlreadyAdded) {
        this.mobyletFilterAlreadyAdded = mobyletFilterAlreadyAdded;
    }

    public boolean isMobyletFilterMappingAlreadyAdded() {
        return mobyletFilterMappingAlreadyAdded;
    }

    public void setMobyletFilterMappingAlreadyAdded(
            boolean mobyletFilterMappingAlreadyAdded) {
        this.mobyletFilterMappingAlreadyAdded = mobyletFilterMappingAlreadyAdded;
    }

    public boolean isFreyjaServletAlreadyModified() {
        return freyjaServletAlreadyModified;
    }

    public void setFreyjaServletAlreadyModified(
            boolean freyjaServletAlreadyModified) {
        this.freyjaServletAlreadyModified = freyjaServletAlreadyModified;
    }

    public void addFreyjaURLPattern(String urlPattern) {
        freyjaURLPatterns.add(urlPattern);
    }

    public List<String> getFreyjaURLPatterns() {
        return freyjaURLPatterns;
    }

    public boolean isFreyjaFound() {
        return !freyjaURLPatterns.isEmpty();
    }

    public void setYmirZptVersion(String ymirZptVersion) {
        this.ymirZptVersion = ymirZptVersion;
    }

    public boolean isCustomizedFiltersAvailable() {
        return ymirZptVersion != null
                && ArtifactUtils.compareVersions(ymirZptVersion,
                        "1.0.7-SNAPSHOT") >= 0;
    }
}
