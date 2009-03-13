package ${rootPackageName}.handler;

import javax.servlet.http.HttpServletRequest;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.ymir.Response;
import org.seasar.ymir.constraint.PermissionDeniedException;
import org.seasar.ymir.handler.annotation.ExceptionHandler;
import org.seasar.ymir.message.Note;
import org.seasar.ymir.message.Notes;
import org.seasar.ymir.redirection.impl.RedirectionScope;
import org.seasar.ymir.scope.annotation.Out;
import org.seasar.ymir.scope.impl.SessionScope;
import org.seasar.ymir.util.ServletUtils;

import ${rootPackageName}.web.person.login.LoginPage;
import ${rootPackageName}.ymir.util.Redirect;

public class PermissionDeniedExceptionHandler {
    private HttpServletRequest httpRequest;

    @Binding(bindingType = BindingType.MUST)
    public void setHttpServletRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @ExceptionHandler
    public Response handle(PermissionDeniedException t) {
        return Redirect.to(LoginPage.class);
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
