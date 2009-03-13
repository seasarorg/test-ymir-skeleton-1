package ${rootPackageName}.web.person.login;

import org.seasar.ymir.Response;

public class LoginPageBase extends ${rootPackageName}.web.person.PageBase {
    protected String account;

    protected String password;

    public String getAccount() {
        return this.account;
    }

    @org.seasar.ymir.scope.annotation.RequestParameter
    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return this.password;
    }

    @org.seasar.ymir.scope.annotation.RequestParameter
    public void setPassword(String password) {
        this.password = password;
    }

    public void _get() {

    }

    public Response _post() {
        return null;

    }

    public void _prerender() {

    }
}
