package org.seasar.ymir.vili.skeleton.ysp.freyja;

import java.util.ArrayList;
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
        "init-param", "param-name", "param-value", "servlet", "servlet-name",
        "url-pattern", "servlet-mapping", "welcome-file-list", };

    private static final String LS = System.getProperty("line.separator");

    private static final String SERVLETNAME_ZPT = "zpt";

    private static final String SERVLETNAME_DEFAULT = "default";

    private static final String CLASSNAME_ZPT = "org.seasar.ymir.scaffold.zpt.ScaffoldingYmirFreyjaServlet";

    private static final String CLASSNAME_DEFAULT = "org.seasar.ymir.servlet.JavaResourceServlet";

    public String evaluate(TemplateContext context, String name,
            Attribute[] attributes, Element[] body) {
        WebXmlContext ctx = (WebXmlContext) context;
        final TagElement element = (TagElement) ctx.getElement();

        switch (ctx.getMode()) {
        case ANALYZE:
            if (name.equals("servlet")) {
                ctx.setServletLastInsertionPoint(element, true);

                if (isServletElement(ctx, element, SERVLETNAME_DEFAULT)) {
                    ctx.setDefaultServletFound(true);
                }
            } else if (name.equals("servlet-mapping")) {
                ctx.setServletLastInsertionPoint(element, false);
                ctx.setServletMappingLastInsertionPoint(element, true);
            } else if (name.equals("welcome-file-list")) {
                ctx.setServletLastInsertionPoint(element, false);
                ctx.setServletMappingLastInsertionPoint(element, false);
            }

            return "";

        case MODIFY:
            StringBuilder sb = new StringBuilder();
            TagElement modified = element;

            if (!ctx.isZptServletAlreadyModified()
                    && isServletElement(ctx, element, SERVLETNAME_ZPT)) {
                modified = modifyZptServletElement(ctx, element);
                ctx.setZptServletAlreadyModified(true);
            }

            sb.append(TagEvaluatorUtils.evaluate(context, modified.getName(),
                    modified.getAttributes(), modified.getBodyElements()));

            if (element == ctx.getServletLastInsertionPoint()) {
                if (!ctx.isDefaultServletFound()) {
                    addDefaultServletElement(ctx, sb,
                            element.getColumnNumber() - 1);
                }
            }

            if (element == ctx.getServletMappingLastInsertionPoint()) {
                if (!ctx.isDefaultServletFound()) {
                    addDefaultServletMappingElement(ctx, sb, element
                            .getColumnNumber() - 1);
                }
            }

            return sb.toString();

        default:
            throw new RuntimeException();
        }
    }

    private TagElement modifyZptServletElement(WebXmlContext ctx,
            TagElement element) {
        List<Element> modifiedBodyElements = new ArrayList<Element>();
        for (Element body : element.getBodyElements()) {
            if (body instanceof TagElement) {
                TagElement b = (TagElement) body;
                if (b.getName().equals("servlet-class")) {
                    body = new TagElement(
                            b.getName(),
                            b.getAttributes(),
                            new Element[] { new ConstantElement(CLASSNAME_ZPT) });
                }
            }
            if (body != null) {
                modifiedBodyElements.add(body);
            }
        }
        return new TagElement(element.getName(), element.getAttributes(),
                modifiedBodyElements.toArray(new Element[0]));
    }

    private void addDefaultServletElement(WebXmlContext ctx, StringBuilder sb,
            int indent) {
        sb.append(LS);
        addSpacesOf(sb, indent).append("<servlet>").append(LS);
        addSpacesOf(sb, indent + 2).append("<servlet-name>").append(
                SERVLETNAME_DEFAULT).append("</servlet-name>").append(LS);
        addSpacesOf(sb, indent + 2).append("<servlet-class>").append(
                CLASSNAME_DEFAULT).append("</servlet-class>").append(LS);
        addSpacesOf(sb, indent).append("</servlet>");
    }

    private void addDefaultServletMappingElement(WebXmlContext ctx,
            StringBuilder sb, int indent) {
        sb.append(LS);
        addSpacesOf(sb, indent).append("<servlet-mapping>").append(LS);
        addSpacesOf(sb, indent + 2).append("<servlet-name>").append(
                SERVLETNAME_DEFAULT).append("</servlet-name>").append(LS);
        addSpacesOf(sb, indent + 2).append("<url-pattern>/</url-pattern>")
                .append(LS);
        addSpacesOf(sb, indent).append("</servlet-mapping>");
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
