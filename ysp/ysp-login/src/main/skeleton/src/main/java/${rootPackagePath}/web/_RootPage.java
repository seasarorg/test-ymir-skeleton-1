package ${rootPackageName}.web;

import org.seasar.ymir.Response;

import ${rootPackageName}.web.person.login.LoginPage;
import ${rootPackageName}.ymir.util.Redirect;

public class _RootPage extends _RootPageBase {
    @Override
    public Response _get() {
        return Redirect.to(LoginPage.class);
    }
}
