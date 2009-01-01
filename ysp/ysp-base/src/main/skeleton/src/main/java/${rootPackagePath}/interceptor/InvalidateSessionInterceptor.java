package ${rootPackageName}.interceptor;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.ymir.session.SessionManager;

public class InvalidateSessionInterceptor extends AbstractInterceptor {
    private static final long serialVersionUID = 1L;

    private SessionManager sessionManager;

    @Binding(bindingType = BindingType.MUST)
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        sessionManager.invalidateSession();
        return invocation.proceed();
    }
}
