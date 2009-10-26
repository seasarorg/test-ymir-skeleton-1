package org.seasar.ymir.vili.skeleton.mobylet_fragment_generic.freyja;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import net.skirnir.freyja.Element;
import net.skirnir.freyja.impl.TemplateContextImpl;

public class WebXmlContext extends TemplateContextImpl {
    public enum Mode {
        ANALYZE, MODIFY;
    }

    private Mode mode = Mode.ANALYZE;

    private boolean mobyletFound;

    private boolean localImageResizingFeatureEnabled;

    private boolean mobyletFilterAlreadyAdded;

    private boolean mobyletFilterMappingAlreadyAdded;

    private boolean freyjaServletAlreadyModified;

    private List<String> freyjaURLPatterns = new ArrayList<String>();

    private Set<Environment> environments = EnumSet.noneOf(Environment.class);

    private Element listenerInsertionPoint;

    private Element servletInsertionPoint;

    private Element servletMappingInsertionPoint;

    private String[] localImageUrlPatterns = new String[0];

    private String imageScaleServletPath;

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

    public void addEnvironment(Environment environment) {
        environments.add(environment);
    }

    public void removeEnvironment(Environment environment) {
        environments.remove(environment);
    }

    public boolean containsEnvironment(Environment environment) {
        return environments.contains(environment);
    }

    public void setListenerInsertionPoint(Element element, boolean overwrite) {
        if (listenerInsertionPoint == null || overwrite) {
            listenerInsertionPoint = element;
        }
    }

    public Element getListenerInsertionPoint() {
        return listenerInsertionPoint;
    }

    public void setServletInsertionPoint(Element element, boolean overwrite) {
        if (servletInsertionPoint == null || overwrite) {
            servletInsertionPoint = element;
        }
    }

    public Element getServletInsertionPoint() {
        return servletInsertionPoint;
    }

    public void setServletMappingInsertionPoint(Element element,
            boolean overwrite) {
        if (servletMappingInsertionPoint == null || overwrite) {
            servletMappingInsertionPoint = element;
        }
    }

    public Element getServletMappingInsertionPoint() {
        return servletMappingInsertionPoint;
    }

    public void setLocalImageResizingFeatureEnabled(
            boolean enableLocalImageResizingFeature) {
        this.localImageResizingFeatureEnabled = enableLocalImageResizingFeature;
    }

    public boolean isLocalImageResizingFeatureEnabled() {
        return localImageResizingFeatureEnabled;
    }

    public void setLocalImageUrlPatterns(String[] urlPatterns) {
        localImageUrlPatterns = urlPatterns;
    }

    public String[] getLocalImageUrlPatterns() {
        return localImageUrlPatterns;
    }

    public void setImageScaleServletPath(String imageScaleServletPath) {
        this.imageScaleServletPath = imageScaleServletPath;
    }

    public String getImageScaleServletPath() {
        return imageScaleServletPath;
    }
}
