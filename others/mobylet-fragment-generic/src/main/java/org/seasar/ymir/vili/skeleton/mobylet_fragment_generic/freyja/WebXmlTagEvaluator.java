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
        "filter-mapping", "listener", "servlet", "servlet-name", "url-pattern" };

    private static final String LS = System.getProperty("line.separator");

    private static final String PARAM_NAME_KEEPRESOPNSECONTENTTYPEASIS = "keepResponseContentTypeAsIs";

    private static final String SERVLETNAME_ZPT = "zpt";

    private static final String FILTERNAME_MOBYLETSETUPFILTER = "mobyletSetUpFilter";

    private static final String FILTERNAME_MOBYLETFILTER = "mobyletFilter";

    private static final String FILTERNAME_ENCODINGFILTER = "encodingFilter";

    private static final String PARAM_NAME_REQUESTENCODING = "requestEncoding";

    private static final String CLASSNAME_MOBYLETFILTER = "org.mobylet.core.http.MobyletFilter";

    private static final String CLASSNAME_S2MOBYLETFILTER = "org.seasar.mobylet.http.S2MobyletFilter";

    private static final String CLASSNAME_FORCEWRAPMOBYLETFILTER = "org.mobylet.core.http.ForceWrapMobyletFilter";

    private static final String CLASSNAME_MOBYLETSETUPFILTER = "org.seasar.ymir.zpt.mobylet.http.MobyletSetUpFilter";

    private static final String CLASSNAME_MOBYLETPROCESSFILTER = "org.seasar.ymir.zpt.mobylet.http.MobyletProcessFilter";

    private static final List<String> URL_PATTERNS_DEFAULT = Arrays
            .asList(new String[] { "/*" });

    private static final String LISTENERCLASS_S2MOBYLETLISTENER = "org.seasar.ymir.zpt.mobylet.http.S2MobyletListener";

    public String evaluate(TemplateContext context, String name,
            Attribute[] attributes, Element[] body) {
        WebXmlContext ctx = (WebXmlContext) context;
        TagElement element = (TagElement) ctx.getElement();

        switch (ctx.getMode()) {
        case ANALYZE:
            if (isFilterElement(ctx, element, FILTERNAME_MOBYLETFILTER)) {
                ctx.setMobyletFound(true);
            } else if (isServletMappingElement(ctx, element, SERVLETNAME_ZPT)) {
                ctx.addFreyjaURLPattern(getURLPattern(ctx, element));
            }

            if (name.equals("filter-mapping") || name.equals("listener")) {
                ctx.setListenerInsertionPoint(ctx.getElement());
            }
            return "";

        case MODIFY:
            StringBuilder sb = new StringBuilder();
            boolean omitElement = false;

            if (isFilterElement(ctx, element, FILTERNAME_ENCODINGFILTER)
                    || isFilterMappingElement(ctx, element,
                            FILTERNAME_ENCODINGFILTER)) {
                omitElement = true;
            }

            if (!ctx.isMobyletFilterAlreadyAdded() && "filter".equals(name)) {
                addMobyletFilterElement(ctx, sb, element.getColumnNumber() - 1);
                ctx.setMobyletFilterAlreadyAdded(true);
            } else if (!ctx.isMobyletFilterMappingAlreadyAdded()
                    && "filter-mapping".equals(name)) {
                addMobyletFilterMappingElement(ctx, sb, ctx
                        .getFreyjaURLPatterns(), element.getColumnNumber() - 1);
                ctx.setMobyletFilterMappingAlreadyAdded(true);
            } else if (!ctx.isFreyjaServletAlreadyModified()
                    && isServletElement(ctx, element, SERVLETNAME_ZPT)) {
                element = modifyFreyaServletElement(ctx, element);
                ctx.setFreyjaServletAlreadyModified(true);
            }

            if (!omitElement) {
                sb.append(TagEvaluatorUtils.evaluate(context,
                        element.getName(), element.getAttributes(), element
                                .getBodyElements()));
            }

            if (element == ctx.getListenerInsertionPoint()) {
                addMobyletListenerElement(ctx, sb,
                        element.getColumnNumber() - 1);
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
                .equals(keepResponseContentTypeAsIs);
        boolean requestEncodingProcessed = requestEncoding == null;
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
        addSpacesOf(sb, indent).append("</filter>").append(LS);
        addSpacesOf(sb, indent);
    }

    private void addMobyletFilterMappingElement(WebXmlContext ctx,
            StringBuilder sb, List<String> urlPatterns, int indent) {
        if (ctx.containsEnvironment(Environment.YMIR_ZPT_1_0_7)) {
            sb.append("<filter-mapping>").append(LS);
            addSpacesOf(sb, indent + 2).append("<filter-name>").append(
                    FILTERNAME_MOBYLETSETUPFILTER).append("</filter-name>")
                    .append(LS);
            addSpacesOf(sb, indent + 2).append("<url-pattern>/*</url-pattern>")
                    .append(LS);
            addSpacesOf(sb, indent + 2).append(
                    "<dispatcher>REQUEST</dispatcher>").append(LS);
            addSpacesOf(sb, indent + 2).append(
                    "<dispatcher>FORWARD</dispatcher>").append(LS);
            addSpacesOf(sb, indent + 2).append(
                    "<dispatcher>INCLUDE</dispatcher>").append(LS);
            addSpacesOf(sb, indent).append("</filter-mapping>").append(LS);
            addSpacesOf(sb, indent);
        }

        List<String> patterns = urlPatterns.isEmpty() ? URL_PATTERNS_DEFAULT
                : urlPatterns;
        for (String urlPattern : patterns) {
            sb.append("<filter-mapping>").append(LS);
            addSpacesOf(sb, indent + 2).append("<filter-name>").append(
                    FILTERNAME_MOBYLETFILTER).append("</filter-name>").append(
                    LS);
            addSpacesOf(sb, indent + 2).append("<url-pattern>").append(
                    urlPattern).append("</url-pattern>").append(LS);
            addSpacesOf(sb, indent + 2).append(
                    "<dispatcher>REQUEST</dispatcher>").append(LS);
            addSpacesOf(sb, indent + 2).append(
                    "<dispatcher>FORWARD</dispatcher>").append(LS);
            addSpacesOf(sb, indent + 2).append(
                    "<dispatcher>INCLUDE</dispatcher>").append(LS);
            addSpacesOf(sb, indent).append("</filter-mapping>").append(LS);
            addSpacesOf(sb, indent);
        }
    }

    private void addMobyletListenerElement(WebXmlContext ctx, StringBuilder sb,
            int indent) {
        if (ctx.containsEnvironment(Environment.YMIR_ZPT_1_0_7)) {
            sb.append(LS);
            addSpacesOf(sb, indent).append("<listener>").append(LS);
            addSpacesOf(sb, indent + 2).append("<listener-class>").append(
                    LISTENERCLASS_S2MOBYLETLISTENER)
                    .append("</listener-class>").append(LS);
            addSpacesOf(sb, indent).append("</listener>");
        }
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
