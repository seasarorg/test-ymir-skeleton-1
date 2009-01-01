package ${rootPackageName}.constraint;

import java.lang.reflect.AnnotatedElement;

import javax.servlet.http.HttpSession;

import ${rootPackageName}.LoginPerson;
import ${rootPackageName}.constraint.annotation.Logined;
import ${rootPackageName}.enm.PersonType;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.ymir.Request;
import org.seasar.ymir.constraint.ConstraintViolatedException;
import org.seasar.ymir.constraint.PermissionDeniedException;
import org.seasar.ymir.constraint.impl.AbstractConstraint;
import org.seasar.ymir.session.SessionManager;

public class LoginedConstraint extends AbstractConstraint<Logined> {
    public static final String KEY_LOGINPERSON = "loginPerson";

    private SessionManager sessionManager;

    @Binding(bindingType = BindingType.MUST)
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void confirm(Object component, Request request, Logined annotation,
            AnnotatedElement element) throws ConstraintViolatedException {
        HttpSession session = sessionManager.getSession(false);
        if (session != null) {
            LoginPerson loginPerson = (LoginPerson) session
                    .getAttribute(KEY_LOGINPERSON);
            if (loginPerson != null) {
                if (annotation.value().length == 0) {
                    return;
                } else {
                    PersonType personType = loginPerson.getPersonType();
                    boolean matched = false;
                    for (PersonType pt : annotation.value()) {
                        if (pt == personType) {
                            matched = true;
                            break;
                        }
                    }
                    if (matched) {
                        return;
                    }
                }
            }
        }

        throw new PermissionDeniedException();
    }
}
