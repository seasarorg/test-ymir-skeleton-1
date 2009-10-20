package org.seasar.ymir.vili.skeleton.freyja;

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
        "filter-mapping", "servlet", "servlet-name", "url-pattern" };

    private static final String LS = System.getProperty("line.separator");

    private static final String PARAM_NAME_KEEPRESOPNSECONTENTTYPEASIS = "keepResponseContentTypeAsIs";

    private static final String SERVLETNAME_ZPT = "zpt";

    private static final String FILTERNAME_MOBYLETFILTER = "mobyletFilter";

    private static final String FILTERNAME_ENCODINGFILTER = "encodingFilter";

    private static final String PARAM_NAME_REQUESTENCODING = "requestEncoding";

    private static final String CLASSNAME_MOBYLETFILTER = "org.mobylet.core.http.MobyletFilter";

    private static final String CLASSNAME_FORCEWRAPMOBYLETFILTER = "org.mobylet.core.http.ForceWrapMobyletFilter";

    private static final List<String> URL_PATTERNS_DEFAULT = Arrays
            .asList(new String[] { "/*" });

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
                addMobyletFilterElement(sb, element.getColumnNumber() - 1, ctx
                        .isFreyjaFound());
                ctx.setMobyletFilterAlreadyAdded(true);
            } else if (!ctx.isMobyletFilterMappingAlreadyAdded()
                    && "filter-mapping".equals(name)) {
                addMobyletFilterMappingElement(sb, ctx.getFreyjaURLPatterns(),
                        element.getColumnNumber() - 1);
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

    private void addMobyletFilterElement(StringBuilder sb, int indent,
            boolean useForceWrapMobyletFilterClass) {
        sb.append("<filter>").append(LS);
        addSpacesOf(sb, indent + 2).append("<filter-name>").append(
                FILTERNAME_MOBYLETFILTER).append("</filter-name>").append(LS);
        if (useForceWrapMobyletFilterClass) {
            addSpacesOf(sb, indent + 2).append("<filter-class>").append(
                    CLASSNAME_FORCEWRAPMOBYLETFILTER).append("</filter-class>")
                    .append(LS);
            addSpacesOf(sb, indent + 2).append("<init-param>").append(LS);
            addSpacesOf(sb, indent + 4).append(
                    "<param-name>isAllForceWrap</param-name>").append(LS);
            addSpacesOf(sb, indent + 4).append(
                    "<param-value>true</param-value>").append(LS);
            addSpacesOf(sb, indent + 2).append("</init-param>").append(LS);
        } else {
            addSpacesOf(sb, indent + 2).append("<filter-class>").append(
                    CLASSNAME_MOBYLETFILTER).append("</filter-class>").append(
                    LS);
        }
        addSpacesOf(sb, indent).append("</filter>").append(LS);
        addSpacesOf(sb, indent);
    }

    private void addMobyletFilterMappingElement(StringBuilder sb,
            List<String> urlPatterns, int indent) {
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
