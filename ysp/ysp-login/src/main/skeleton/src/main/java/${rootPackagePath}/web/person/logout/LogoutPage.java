package ${rootPackageName}.web.person.logout;

import org.seasar.ymir.Response;
import org.seasar.ymir.session.annotation.InvalidateSession;

import ${rootPackageName}.web.person.login.LoginPage;
import ${rootPackageName}.ymir.util.Redirect;

public class LogoutPage extends LogoutPageBase {
    @Override
    @InvalidateSession
    public Response _get() {
        return Redirect.to(LoginPage.class);
    }
}
