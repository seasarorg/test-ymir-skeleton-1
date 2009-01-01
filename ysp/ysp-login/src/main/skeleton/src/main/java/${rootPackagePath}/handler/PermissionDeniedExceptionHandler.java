package ${rootPackageName}.handler;

import javax.servlet.http.HttpServletRequest;

import ${rootPackageName}.ymir.util.Redirect;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.ymir.constraint.PermissionDeniedException;
import org.seasar.ymir.handler.ExceptionHandler;
import org.seasar.ymir.message.Note;
import org.seasar.ymir.message.Notes;
import org.seasar.ymir.redirection.impl.RedirectionScope;
import org.seasar.ymir.scope.annotation.Out;
import org.seasar.ymir.scope.impl.SessionScope;
import org.seasar.ymir.util.ServletUtils;

public class PermissionDeniedExceptionHandler implements
        ExceptionHandler<PermissionDeniedException> {
    private HttpServletRequest httpRequest;

    @Binding(bindingType = BindingType.MUST)
    public void setHttpServletRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public String handle(PermissionDeniedException t) {
        return Redirect.to("/person/login/login.html");
    }

    @Out(RedirectionScope.class)
    public Notes getNotesInRedirectionScope() {
        return new Notes().add(new Note("error.permissionDenied"));
    }

    @Out(SessionScope.class)
    public String getRedirectionURL() {
        return ServletUtils.getRequestURL(httpRequest);
    }
}
