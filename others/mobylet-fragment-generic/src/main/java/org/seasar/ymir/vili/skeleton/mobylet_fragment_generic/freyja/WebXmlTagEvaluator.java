package org.seasar.ymir.vili.skeleton.mobylet_fragment_generic.freyja;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import net.skirnir.freyja.Attribute;
import net.skirnir.freyja.ConstantElement;
import net.skirnir.freyja.Element;
import net.skirnir.freyja.Macro;
import net.skirnir.freyja.TagElement;
import net.skirnir.freyja.TagEvaluator;
import net.skirnir.freyja.TagEvaluatorUtils;
import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.TemplateEvaluator;
import net.skirnir.freyja.VariableResolver;

public class WebXmlTagEvaluator implements TagEvaluator {
    private static final String[] SPECIAL_TAG_PATTERNS = new String[] {
        "init-param", "param-name", "param-value", "filter", "filter-class",
        "filter-mapping", "listener", "servlet", "servlet-name", "url-pattern",
        "servlet-mapping", "welcome-file-list", };

    private static final String LS = System.getProperty("line.separator");

    private static final String PARAM_NAME_KEEPRESOPNSECONTENTTYPEASIS = "keepResponseContentTypeAsIs";

    private static final String SERVLETNAME_ZPT = "zpt";

    private static final String SERVLETNAME_IMAGESCALESERVLET = "imageScaleServlet";

    private static final String FILTERNAME_MOBYLETSETUPFILTER = "mobyletSetUpFilter";

    private static final String FILTERNAME_MOBYLETFILTER = "mobyletFilter";

    private static final String FILTERNAME_MOBYLETBINARYFILTER = "mobyletBinaryFilter";

    private static final String FILTERNAME_IMAGESCALEFILTER = "imageScaleFilter";

    private static final String FILTERNAME_ENCODINGFILTER = "encodingFilter";

    private static final String PARAM_NAME_REQUESTENCODING = "requestEncoding";

    private static final String CLASSNAME_S2MOBYLETLISTENER = "org.seasar.ymir.zpt.mobylet.http.S2MobyletListener";

    private static final String CLASSNAME_MOBYLETFILTER = "org.mobylet.core.http.MobyletFilter";

    private static final String CLASSNAME_S2MOBYLETFILTER = "org.seasar.mobylet.http.S2MobyletFilter";

    private static final String CLASSNAME_FORCEWRAPMOBYLETFILTER = "org.mobylet.core.http.ForceWrapMobyletFilter";

    private static final String CLASSNAME_MOBYLETSETUPFILTER = "org.seasar.ymir.zpt.mobylet.http.MobyletSetUpFilter";

    private static final String CLASSNAME_MOBYLETPROCESSFILTER = "org.seasar.ymir.zpt.mobylet.http.MobyletProcessFilter";

    private static final String CLASSNAME_MOBYLETBINARYPROCESSFILTER = "org.seasar.ymir.zpt.mobylet.http.MobyletBinaryProcessFilter";

    private static final String CLASSNAME_MOBYLETIMAGESCALEFILTER = "org.seasar.ymir.zpt.mobylet.http.MobyletImageScaleFilter";

    private static final String CLASSNAME_MOBYLETIMAGESCALESERVLET = "org.mobylet.core.http.image.MobyletImageScaleServlet";

    private static final List<String> URL_PATTERNS_DEFAULT = Arrays
            .asList(new String[] { "/*" });

    public String evaluate(TemplateContext context, String name,
            Attribute[] attributes, Element[] body) {
        WebXmlContext ctx = (WebXmlContext) context;
        final TagElement element = (TagElement) ctx.getElement();

        switch (ctx.getMode()) {
        case ANALYZE:
            if (isFilterElement(ctx, element, FILTERNAME_MOBYLETFILTER)) {
                ctx.setMobyletFound(true);
            } else if (isServletMappingElement(ctx, element, SERVLETNAME_ZPT)) {
                ctx.addFreyjaURLPattern(getURLPattern(ctx, element));
            }

            if (name.equals("filter")) {
                ctx.setFilterFirstInsertionPoint(element, false);
            } else if (name.equals("filter-mapping")) {
                ctx.setFilterFirstInsertionPoint(element, false);
                ctx.setFilterMappingFirstInsertionPoint(element, false);
                ctx.setServletLastInsertionPoint(element, true);
                ctx.setServletMappingLastInsertionPoint(element, true);
            } else if (name.equals("listener")) {
                ctx.setFilterFirstInsertionPoint(element, false);
                ctx.setFilterMappingFirstInsertionPoint(element, false);
                ctx.setListenerFirstInsertionPoint(element, false);
                ctx.setServletLastInsertionPoint(element, true);
                ctx.setServletMappingLastInsertionPoint(element, true);
            } else if (name.equals("servlet")) {
                ctx.setFilterFirstInsertionPoint(element, false);
                ctx.setFilterMappingFirstInsertionPoint(element, false);
                ctx.setListenerFirstInsertionPoint(element, false);
                ctx.setServletLastInsertionPoint(element, true);
                ctx.setServletMappingLastInsertionPoint(element, true);
            } else if (name.equals("servlet-mapping")) {
                ctx.setFilterFirstInsertionPoint(element, false);
                ctx.setFilterMappingFirstInsertionPoint(element, false);
                ctx.setListenerFirstInsertionPoint(element, false);
                ctx.setServletLastInsertionPoint(element, false);
                ctx.setServletMappingLastInsertionPoint(element, true);
            } else if (name.equals("welcome-file-list")) {
                ctx.setFilterFirstInsertionPoint(element, false);
                ctx.setFilterMappingFirstInsertionPoint(element, false);
                ctx.setListenerFirstInsertionPoint(element, false);
                ctx.setServletLastInsertionPoint(element, false);
                ctx.setServletMappingLastInsertionPoint(element, false);
            }

            return "";

        case MODIFY:
            StringBuilder sb = new StringBuilder();
            TagElement modified = element;

            if (isFilterElement(ctx, element, FILTERNAME_ENCODINGFILTER)) {
                ctx.setEncodingFilterRemoved(true);
                modified = null;
            } else if (isFilterMappingElement(ctx, element,
                    FILTERNAME_ENCODINGFILTER)) {
                ctx.setEncodingFilterMappingRemoved(true);
                modified = null;
            }

            if (element == ctx.getFilterFirstInsertionPoint()) {
                addMobyletFilterElement(ctx, sb, element.getColumnNumber() - 1);
            }

            if (element == ctx.getFilterMappingFirstInsertionPoint()) {
                addMobyletFilterMappingElement(ctx, sb, ctx
                        .getFreyjaURLPatterns(), element.getColumnNumber() - 1);
            }

            if (element == ctx.getListenerFirstInsertionPoint()) {
                addMobyletListenerElement(ctx, sb,
                        element.getColumnNumber() - 1);
            }

            if (!ctx.isFreyjaServletAlreadyModified()
                    && isServletElement(ctx, element, SERVLETNAME_ZPT)) {
                modified = modifyFreyaServletElement(ctx, element);
                ctx.setFreyjaServletAlreadyModified(true);
            }

            if (modified != null) {
                sb.append(TagEvaluatorUtils.evaluate(context, modified
                        .getName(), modified.getAttributes(), modified
                        .getBodyElements()));
            }

            if (element == ctx.getServletLastInsertionPoint()) {
                if (!ctx.containsEnvironment(Environment.YMIR_ZPT_1_0_7)
                        && ctx.isNetworkImageResizingFeatureEnabled()) {
                    addMobyletImageScaleServletElement(ctx, sb, element
                            .getColumnNumber() - 1);
                }
            }

            if (element == ctx.getServletMappingLastInsertionPoint()) {
                if (!ctx.containsEnvironment(Environment.YMIR_ZPT_1_0_7)
                        && ctx.isNetworkImageResizingFeatureEnabled()) {
                    addMobyletImageScaleServletMappingElement(ctx, sb, element
                            .getColumnNumber() - 1);
                }
            }

            return sb.toString();

        default:
            throw new RuntimeException();
        }
    }

    private String getURLPattern(WebXmlContext ctx, TagElement element) {
        for (Element body : element.getBodyElements()) {
            if (!(body instanceof TagElement)) {
                continue;
            }
            TagElement b = (TagElement) body;

            if (b.getName().equals("url-pattern")) {
                return TagEvaluatorUtils.evaluateElements(ctx,
                        b.getBodyElements()).trim();
            }
        }
        return null;
    }

    private String getParameter(WebXmlContext ctx, TagElement element,
            String name) {
        for (Element body : element.getBodyElements()) {
            if (!(body instanceof TagElement)) {
                continue;
            }
            TagElement b = (TagElement) body;

            if (!b.getName().equals("init-param")) {
                continue;
            }

            String paramName = null;
            String paramValue = null;
            for (Element bodyBody : b.getBodyElements()) {
                if (!(bodyBody instanceof TagElement)) {
                    continue;
                }
                TagElement bb = (TagElement) bodyBody;

                if (bb.getName().equals("param-name")) {
                    paramName = TagEvaluatorUtils.evaluateElements(ctx,
                            bb.getBodyElements()).trim();
                } else if (bb.getName().equals("param-value")) {
                    paramValue = TagEvaluatorUtils.evaluateElements(ctx,
                            bb.getBodyElements()).trim();
                }
            }
            if (name.equals(paramName)) {
                return paramValue;
            }
        }
        return null;
    }

    private TagElement modifyFreyaServletElement(WebXmlContext ctx,
            TagElement element) {
        String keepResponseContentTypeAsIs = getParameter(ctx, element,
                PARAM_NAME_KEEPRESOPNSECONTENTTYPEASIS);
        String requestEncoding = getParameter(ctx, element,
                PARAM_NAME_REQUESTENCODING);

        boolean keepResponseContentTypeAsIsProcessed = String.valueOf(true)
                .equals(keepResponseContentTypeAsIs)
                || ctx.containsEnvironment(Environment.YMIR_ZPT_1_0_7);
        boolean requestEncodingProcessed = (requestEncoding == null);
        if (keepResponseContentTypeAsIsProcessed && requestEncodingProcessed) {
            return element;
        }

        List<Element> modifiedBodyElements = new ArrayList<Element>();
        for (Element body : element.getBodyElements()) {
            if (body instanceof TagElement) {
                TagElement b = (TagElement) body;
                if (b.getName().equals("init-param")) {
                    if (!keepResponseContentTypeAsIsProcessed
                            && keepResponseContentTypeAsIs == null) {
                        modifiedBodyElements.add(createFreyjaInitParamElement(b
                                .getColumnNumber() - 1, false));
                        keepResponseContentTypeAsIsProcessed = true;
                    }

                    String paramName = null;
                    int paramValueIndex = -1;
                    Element[] bodyBodies = b.getBodyElements();
                    for (int i = 0; i < bodyBodies.length; i++) {
                        Element bodyBody = bodyBodies[i];
                        if (!(bodyBody instanceof TagElement)) {
                            continue;
                        }
                        TagElement bb = (TagElement) bodyBody;

                        if (bb.getName().equals("param-name")) {
                            paramName = TagEvaluatorUtils.evaluateElements(ctx,
                                    bb.getBodyElements()).trim();
                        } else if (bb.getName().equals("param-value")) {
                            paramValueIndex = i;
                        }
                    }
                    if (!keepResponseContentTypeAsIsProcessed
                            && PARAM_NAME_KEEPRESOPNSECONTENTTYPEASIS
                                    .equals(paramName) && paramValueIndex >= 0) {
                        TagElement paramValueElement = (TagElement) bodyBodies[paramValueIndex];
                        bodyBodies[paramValueIndex] = new TagElement(
                                paramValueElement.getName(), paramValueElement
                                        .getAttributes(),
                                new Element[] { new ConstantElement(String
                                        .valueOf(true)) });
                        keepResponseContentTypeAsIsProcessed = true;
                    } else if (!requestEncodingProcessed
                            && PARAM_NAME_REQUESTENCODING.equals(paramName)) {
                        body = null;
                        requestEncodingProcessed = true;
                    }
                }
            }
            if (body != null) {
                modifiedBodyElements.add(body);
            }
        }
        if (!keepResponseContentTypeAsIsProcessed) {
            modifiedBodyElements.add(createFreyjaInitParamElement(element
                    .getColumnNumber() + 1, true));
        }
        return new TagElement(element.getName(), element.getAttributes(),
                modifiedBodyElements.toArray(new Element[0]));
    }

    private Element createFreyjaInitParamElement(int indent, boolean shift) {
        StringBuilder sb = new StringBuilder();
        if (shift) {
            addSpacesOf(sb, 2);
        }
        sb.append("<init-param>").append(LS);
        addSpacesOf(sb, indent + 2).append("<param-name>").append(
                PARAM_NAME_KEEPRESOPNSECONTENTTYPEASIS).append("</param-name>")
                .append(LS);
        addSpacesOf(sb, indent + 2).append("<param-value>true</param-value>")
                .append(LS);
        addSpacesOf(sb, indent).append("</init-param>").append(LS);
        if (shift) {
            addSpacesOf(sb, indent - 2);
        } else {
            addSpacesOf(sb, indent);
        }
        return new ConstantElement(sb.toString());
    }

    private void addMobyletFilterElement(WebXmlContext ctx, StringBuilder sb,
            int indent) {
        if (ctx.containsEnvironment(Environment.YMIR_ZPT_1_0_7)) {
            sb.append("<filter>").append(LS);
            addSpacesOf(sb, indent + 2).append("<filter-name>").append(
                    FILTERNAME_MOBYLETSETUPFILTER).append("</filter-name>")
                    .append(LS);
            addSpacesOf(sb, indent + 2).append("<filter-class>").append(
                    CLASSNAME_MOBYLETSETUPFILTER).append("</filter-class>")
                    .append(LS);
            addSpacesOf(sb, indent).append("</filter>").append(LS);
            addSpacesOf(sb, indent);

            if (ctx.isNetworkImageResizingFeatureEnabled()) {
                sb.append("<filter>").append(LS);
                addSpacesOf(sb, indent + 2).append("<filter-name>").append(
                        FILTERNAME_IMAGESCALEFILTER).append("</filter-name>")
                        .append(LS);
                addSpacesOf(sb, indent + 2).append("<filter-class>").append(
                        CLASSNAME_MOBYLETIMAGESCALEFILTER).append(
                        "</filter-class>").append(LS);
                addSpacesOf(sb, indent + 2).append("<init-param>").append(LS);
                addSpacesOf(sb, indent + 4).append(
                        "<param-name>path</param-name>").append(LS);
                addSpacesOf(sb, indent + 4).append("<param-value>").append(
                        ctx.getImageScaleServletPath())
                        .append("</param-value>").append(LS);
                addSpacesOf(sb, indent + 2).append("</init-param>").append(LS);
                addSpacesOf(sb, indent).append("</filter>").append(LS);
                addSpacesOf(sb, indent);
            }
        }

        if (ctx.containsEnvironment(Environment.FREYJA)
                && ctx.isLocalImageResizingFeatureEnabled()) {
            sb.append("<filter>").append(LS);
            addSpacesOf(sb, indent + 2).append("<filter-name>").append(
                    FILTERNAME_MOBYLETBINARYFILTER).append("</filter-name>")
                    .append(LS);
            addSpacesOf(sb, indent + 2)
                    .append("<filter-class>")
                    .append(
                            ctx.containsEnvironment(Environment.YMIR_ZPT_1_0_7) ? CLASSNAME_MOBYLETBINARYPROCESSFILTER
                                    : CLASSNAME_MOBYLETFILTER).append(
                            "</filter-class>").append(LS);
            addSpacesOf(sb, indent).append("</filter>").append(LS);
            addSpacesOf(sb, indent);
        }

        sb.append("<filter>").append(LS);
        addSpacesOf(sb, indent + 2).append("<filter-name>").append(
                FILTERNAME_MOBYLETFILTER).append("</filter-name>").append(LS);
        if (ctx.containsEnvironment(Environment.FREYJA)) {
            addSpacesOf(sb, indent + 2)
                    .append("<filter-class>")
                    .append(
                            ctx.containsEnvironment(Environment.YMIR_ZPT_1_0_7) ? CLASSNAME_MOBYLETPROCESSFILTER
                                    : CLASSNAME_FORCEWRAPMOBYLETFILTER).append(
                            "</filter-class>").append(LS);
            addSpacesOf(sb, indent + 2).append("<init-param>").append(LS);
            addSpacesOf(sb, indent + 4).append(
                    "<param-name>isAllForceWrap</param-name>").append(LS);
            addSpacesOf(sb, indent + 4).append(
                    "<param-value>true</param-value>").append(LS);
            addSpacesOf(sb, indent + 2).append("</init-param>").append(LS);
        } else {
            addSpacesOf(sb, indent + 2)
                    .append("<filter-class>")
                    .append(
                            ctx.containsEnvironment(Environment.SEASAR2) ? CLASSNAME_S2MOBYLETFILTER
                                    : CLASSNAME_MOBYLETFILTER).append(
                            "</filter-class>").append(LS);
        }
        addSpacesOf(sb, indent).append("</filter>");
        if (!ctx.isEncodingFilterRemoved()) {
            sb.append(LS);
            addSpacesOf(sb, indent);
        }
    }

    private void addMobyletFilterMappingElement(WebXmlContext ctx,
            StringBuilder sb, List<String> urlPatterns, int indent) {
        if (ctx.containsEnvironment(Environment.YMIR_ZPT_1_0_7)) {
            addFilterMapping(sb, FILTERNAME_MOBYLETSETUPFILTER, "/*", true,
                    true, true, indent);

            if (ctx.isNetworkImageResizingFeatureEnabled()) {
                addFilterMapping(sb, FILTERNAME_IMAGESCALEFILTER, "/*", true,
                        true, false, indent);
            }
        }

        if (ctx.containsEnvironment(Environment.FREYJA)
                && ctx.isLocalImageResizingFeatureEnabled()) {
            for (String urlPattern : ctx.getLocalImageUrlPatterns()) {
                addFilterMapping(sb, FILTERNAME_MOBYLETBINARYFILTER,
                        urlPattern, true, true, true, indent);
            }
        }

        for (String urlPattern : urlPatterns.isEmpty() ? URL_PATTERNS_DEFAULT
                : urlPatterns) {
            addFilterMapping(sb, FILTERNAME_MOBYLETFILTER, urlPattern, true,
                    true, true, indent);
        }

        if (ctx.isEncodingFilterMappingRemoved()) {
            sb.delete(sb.length() - (LS.length() + indent), sb.length());
        }
    }

    private void addFilterMapping(StringBuilder sb, String fiterName,
            String urlPattern, boolean addRequestDispatcher,
            boolean addForwardDispatcher, boolean addIncludeDispatcher,
            int indent) {
        sb.append("<filter-mapping>").append(LS);
        addSpacesOf(sb, indent + 2).append("<filter-name>").append(fiterName)
                .append("</filter-name>").append(LS);
        addSpacesOf(sb, indent + 2).append("<url-pattern>").append(
                urlPattern.trim()).append("</url-pattern>").append(LS);
        if (addRequestDispatcher) {
            addSpacesOf(sb, indent + 2).append(
                    "<dispatcher>REQUEST</dispatcher>").append(LS);
        }
        if (addForwardDispatcher) {
            addSpacesOf(sb, indent + 2).append(
                    "<dispatcher>FORWARD</dispatcher>").append(LS);
        }
        if (addIncludeDispatcher) {
            addSpacesOf(sb, indent + 2).append(
                    "<dispatcher>INCLUDE</dispatcher>").append(LS);
        }
        addSpacesOf(sb, indent).append("</filter-mapping>").append(LS);
        addSpacesOf(sb, indent);
    }

    private void addMobyletListenerElement(WebXmlContext ctx, StringBuilder sb,
            int indent) {
        if (ctx.containsEnvironment(Environment.YMIR_ZPT_1_0_7)) {
            sb.append("<listener>").append(LS);
            addSpacesOf(sb, indent + 2).append("<listener-class>").append(
                    CLASSNAME_S2MOBYLETLISTENER).append("</listener-class>")
                    .append(LS);
            addSpacesOf(sb, indent).append("</listener>").append(LS);
            addSpacesOf(sb, indent);
        }
    }

    private void addMobyletImageScaleServletElement(WebXmlContext ctx,
            StringBuilder sb, int indent) {
        sb.append(LS);
        addSpacesOf(sb, indent).append("<servlet>").append(LS);
        addSpacesOf(sb, indent + 2).append("<servlet-name>").append(
                SERVLETNAME_IMAGESCALESERVLET).append("</servlet-name>")
                .append(LS);
        addSpacesOf(sb, indent + 2).append("<servlet-class>").append(
                CLASSNAME_MOBYLETIMAGESCALESERVLET).append("</servlet-class>")
                .append(LS);
        addSpacesOf(sb, indent).append("</servlet>");
    }

    private void addMobyletImageScaleServletMappingElement(WebXmlContext ctx,
            StringBuilder sb, int indent) {
        sb.append(LS);
        addSpacesOf(sb, indent).append("<servlet-mapping>").append(LS);
        addSpacesOf(sb, indent + 2).append("<servlet-name>").append(
                SERVLETNAME_IMAGESCALESERVLET).append("</servlet-name>")
                .append(LS);
        addSpacesOf(sb, indent + 2).append("<url-pattern>").append(
                stripContextPath(ctx.getImageScaleServletPath())).append(
                "</url-pattern>").append(LS);
        addSpacesOf(sb, indent).append("</servlet-mapping>");
    }

    private String stripContextPath(String path) {
        if (path == null || !path.startsWith("/")) {
            return path;
        }
        int slash = path.indexOf('/', 1);
        if (slash < 0) {
            return path;
        }
        return path.substring(slash);
    }

    private StringBuilder addSpacesOf(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append(' ');
        }
        return sb;
    }

    public Element expandMacroVariables(TemplateContext context,
            VariableResolver macroVarResolver, String name,
            Attribute[] attributes, Element[] body) {
        return null;
    }

    public void gatherMacroVariables(TemplateContext context,
            VariableResolver macroVarResolver, String name,
            Attribute[] attributes, Element[] body) {
    }

    public Macro getMacro(TemplateEvaluator evaluator, String name,
            Attribute[] attributes, Element[] body, String macroName,
            Element previousElement) {
        return null;
    }

    public String getProperty(String key) {
        return null;
    }

    public String[] getSpecialAttributePatternStrings() {
        return null;
    }

    public String[] getSpecialTagPatternStrings() {
        return SPECIAL_TAG_PATTERNS;
    }

    public TemplateContext newContext() {
        return new WebXmlContext();
    }

    public void setProperties(Properties properties) {
    }

    private boolean isServletElement(WebXmlContext ctx, TagElement element,
            String servletName) {
        return isElement(ctx, element, "servlet", "servlet-name", servletName);
    }

    private boolean isServletMappingElement(WebXmlContext ctx,
            TagElement element, String servletName) {
        return isElement(ctx, element, "servlet-mapping", "servlet-name",
                servletName);
    }

    private boolean isFilterElement(WebXmlContext ctx, TagElement element,
            String filterName) {
        return isElement(ctx, element, "filter", "filter-name", filterName);
    }

    private boolean isFilterMappingElement(WebXmlContext ctx,
            TagElement element, String filterName) {
        return isElement(ctx, element, "filter-mapping", "filter-name",
                filterName);
    }

    private boolean isElement(WebXmlContext ctx, TagElement element,
            String elementName, String subElementName, String subElementValue) {
        if (!element.getName().equals(elementName)) {
            return false;
        }
        for (Element body : element.getBodyElements()) {
            if (!(body instanceof TagElement)) {
                continue;
            }
            TagElement b = (TagElement) body;

            if (b.getName().equals(subElementName)) {
                return TagEvaluatorUtils.evaluateElements(ctx,
                        b.getBodyElements()).trim().equals(subElementValue);
            }
        }
        return false;
    }
}
