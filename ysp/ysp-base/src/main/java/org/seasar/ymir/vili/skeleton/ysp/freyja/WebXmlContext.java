package org.seasar.ymir.vili.skeleton.ysp.freyja;

import net.skirnir.freyja.Element;
import net.skirnir.freyja.impl.TemplateContextImpl;

public class WebXmlContext extends TemplateContextImpl {
    public enum Mode {
        ANALYZE, MODIFY;
    }

    private Mode mode = Mode.ANALYZE;

    private boolean zptServletAlreadyModified;

    private boolean defaultServletFound;

    private Element servletLastInsertionPoint;

    private Element servletMappingLastInsertionPoint;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public boolean isZptServletAlreadyModified() {
        return zptServletAlreadyModified;
    }

    public void setZptServletAlreadyModified(
            boolean freyjaServletAlreadyModified) {
        this.zptServletAlreadyModified = freyjaServletAlreadyModified;
    }

    public boolean isDefaultServletFound() {
        return defaultServletFound;
    }

    public void setDefaultServletFound(boolean defaultServletFound) {
        this.defaultServletFound = defaultServletFound;
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
}
