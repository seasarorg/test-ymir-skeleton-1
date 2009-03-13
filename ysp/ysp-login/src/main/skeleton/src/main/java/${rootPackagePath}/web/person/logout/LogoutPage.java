package ${rootPackageName}.web.person.logout;

import org.seasar.ymir.Response;
import org.seasar.ymir.scope.annotation.Inject;
import org.seasar.ymir.session.SessionManager;

import ${rootPackageName}.web.person.login.LoginPage;
import ${rootPackageName}.ymir.util.Redirect;

public class LogoutPage extends LogoutPageBase {
    private SessionManager sessionManager;

    @Inject
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Response _get() {
        sessionManager.invalidateSession();
        return Redirect.to(LoginPage.class);
    }
}
