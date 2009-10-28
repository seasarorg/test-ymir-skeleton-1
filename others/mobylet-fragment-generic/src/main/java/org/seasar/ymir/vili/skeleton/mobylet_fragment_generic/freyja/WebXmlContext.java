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

    private boolean networkImageResizingFeatureEnabled;

    private boolean freyjaServletAlreadyModified;

    private List<String> freyjaURLPatterns = new ArrayList<String>();

    private Set<Environment> environments = EnumSet.noneOf(Environment.class);

    private Element filterFirstInsertionPoint;

    private Element filterMappingFirstInsertionPoint;

    private Element listenerFirstInsertionPoint;

    private Element servletLastInsertionPoint;

    private Element servletMappingLastInsertionPoint;

    private String[] localImageUrlPatterns = new String[0];

    private String imageScaleServletPath;

    private boolean encodingFilterRemoved;

    private boolean encodingFilterMappingRemoved;

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

    public void setFilterFirstInsertionPoint(Element element, boolean overwrite) {
        if (filterFirstInsertionPoint == null || overwrite) {
            filterFirstInsertionPoint = element;
        }
    }

    public Element getFilterFirstInsertionPoint() {
        return filterFirstInsertionPoint;
    }

    public void setFilterMappingFirstInsertionPoint(Element element,
            boolean overwrite) {
        if (filterMappingFirstInsertionPoint == null || overwrite) {
            filterMappingFirstInsertionPoint = element;
        }
    }

    public Element getFilterMappingFirstInsertionPoint() {
        return filterMappingFirstInsertionPoint;
    }

    public void setListenerFirstInsertionPoint(Element element,
            boolean overwrite) {
        if (listenerFirstInsertionPoint == null || overwrite) {
            listenerFirstInsertionPoint = element;
        }
    }

    public Element getListenerFirstInsertionPoint() {
        return listenerFirstInsertionPoint;
    }

    public void setServletLastInsertionPoint(Element element, boolean overwrite) {
        if (servletLastInsertionPoint == null || overwrite) {
            servletLastInsertionPoint = element;
        }
    }

    public Element getServletLastInsertionPoint() {
        return servletLastInsertionPoint;
    }

    public void setServletMappingLastInsertionPoint(Element element,
            boolean overwrite) {
        if (servletMappingLastInsertionPoint == null || overwrite) {
            servletMappingLastInsertionPoint = element;
        }
    }

    public Element getServletMappingLastInsertionPoint() {
        return servletMappingLastInsertionPoint;
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

    public void setNetworkImageResizingFeatureEnabled(
            boolean networkImageResizingFeatureEnabled) {
        this.networkImageResizingFeatureEnabled = networkImageResizingFeatureEnabled;
    }

    public boolean isNetworkImageResizingFeatureEnabled() {
        return networkImageResizingFeatureEnabled;
    }

    public void setEncodingFilterRemoved(boolean encodingFilterRemoved) {
        this.encodingFilterRemoved = encodingFilterRemoved;
    }

    public boolean isEncodingFilterRemoved() {
        return encodingFilterRemoved;
    }

    public void setEncodingFilterMappingRemoved(
            boolean encodingFilterMappingRemoved) {
        this.encodingFilterMappingRemoved = encodingFilterMappingRemoved;
    }

    public boolean isEncodingFilterMappingRemoved() {
        return encodingFilterMappingRemoved;
    }
}
