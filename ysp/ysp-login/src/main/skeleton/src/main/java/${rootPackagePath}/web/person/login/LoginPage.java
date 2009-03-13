package ${rootPackageName}.web.person.login;

import org.seasar.ymir.Response;
import org.seasar.ymir.constraint.annotation.Required;
import org.seasar.ymir.message.Notes;
import org.seasar.ymir.redirection.impl.RedirectionScope;
import org.seasar.ymir.scope.annotation.In;
import org.seasar.ymir.scope.annotation.Inject;
import org.seasar.ymir.scope.annotation.Out;
import org.seasar.ymir.scope.impl.SessionScope;
import org.seasar.ymir.util.StringUtils;

import ${rootPackageName}.LoginPerson;
import ${rootPackageName}.interceptor.annotation.InvalidateSession;
import ${rootPackageName}.logic.AuthenticationLogic;
import ${rootPackageName}.web.person.mypage.MypagePage;
import ${rootPackageName}.ymir.util.Redirect;

public class LoginPage extends LoginPageBase {
    private AuthenticationLogic authenticationLogic;

    private String redirectionURL;

    @Inject
    public void setAuthenticationLogic(AuthenticationLogic authenticationLogic) {
        this.authenticationLogic = authenticationLogic;
    }

    @In(RedirectionScope.class)
    public void setNotesInRedirectionScope(Notes notes) {
        getNotes().add(notes);
    }

    @In(SessionScope.class)
    public void setRedirectionURL(String redirectionURL) {
        this.redirectionURL = redirectionURL;
    }

    @Override
    @Required( { "account", "password" })
    @InvalidateSession
    public Response _post() {
        LoginPerson loginPerson = authenticationLogic.login(account, password);
        if (loginPerson != null) {
            setLoginPerson(loginPerson);
            if (StringUtils.isEmpty(redirectionURL)) {
                return Redirect.to(MypagePage.class);
            } else {
                return Redirect.to(redirectionURL);
            }
        } else {
            addNote("error.person.login.login.failed");
            return passthrough();
        }
    }

    @Out(SessionScope.class)
    public LoginPerson getLoginPerson() {
        return super.getLoginPerson();
    }
}
