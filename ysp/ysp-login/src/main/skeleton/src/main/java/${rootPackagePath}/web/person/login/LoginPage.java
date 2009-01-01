package ${rootPackageName}.web.person.login;

import ${rootPackageName}.LoginPerson;
import ${rootPackageName}.interceptor.annotation.InvalidateSession;
import ${rootPackageName}.ymir.util.Redirect;

import org.seasar.ymir.constraint.annotation.Required;
import org.seasar.ymir.message.Notes;
import org.seasar.ymir.redirection.impl.RedirectionScope;
import org.seasar.ymir.scope.annotation.In;
import org.seasar.ymir.scope.annotation.Out;
import org.seasar.ymir.scope.impl.SessionScope;
import org.seasar.ymir.util.StringUtils;

public class LoginPage extends LoginPageBase {
    private String redirectionURL;

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
    public String _post() {
        LoginPerson loginPerson = authenticationLogic.login(account, password);
        if (loginPerson != null) {
            setLoginPerson(loginPerson);
            if (StringUtils.isEmpty(redirectionURL)) {
                return Redirect.to("/person/mypage/mypage.html");
            } else {
                return Redirect.to(redirectionURL);
            }
        } else {
            addNote("error.person.login.login.failed");
            return PASSTHROUGH;
        }
    }

    @Out(SessionScope.class)
    public LoginPerson getLoginPerson() {
        return super.getLoginPerson();
    }
}
