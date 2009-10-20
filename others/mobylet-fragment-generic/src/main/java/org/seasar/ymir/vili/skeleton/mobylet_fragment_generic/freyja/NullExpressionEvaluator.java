package org.seasar.ymir.vili.skeleton.mobylet_fragment_generic.freyja;

import net.skirnir.freyja.ExpressionEvaluator;
import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.VariableResolver;

public class NullExpressionEvaluator implements ExpressionEvaluator {
    public Object evaluate(TemplateContext context,
            VariableResolver varResolver, String expression) {
        return expression;
    }

    public boolean isTrue(Object obj) {
        return false;
    }
}
