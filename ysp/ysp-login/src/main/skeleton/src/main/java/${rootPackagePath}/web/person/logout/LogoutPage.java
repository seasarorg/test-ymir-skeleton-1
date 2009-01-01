package ${rootPackageName}.web.person.logout;

import org.seasar.ymir.scope.annotation.Inject;
import org.seasar.ymir.session.SessionManager;

public class LogoutPage extends LogoutPageBase {
    private SessionManager sessionManager;

    @Inject
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public String _get() {
        sessionManager.invalidateSession();
        return super._get();
    }
}
