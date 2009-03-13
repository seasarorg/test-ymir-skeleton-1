package ${rootPackageName}.web;

import org.seasar.ymir.scope.annotation.In;
import org.seasar.ymir.scope.impl.SessionScope;

import ${rootPackageName}.LoginPerson;
import ${rootPackageName}.enm.PersonRole;

public class PageBase extends ${rootPackageName}.ymir.util.PageBase {
    private LoginPerson loginPerson;

    @In(SessionScope.class)
    final public void setLoginPerson(LoginPerson loginPerson) {
        this.loginPerson = loginPerson;
    }

    public LoginPerson getLoginPerson() {
        return loginPerson;
    }

    public boolean isAdministrator() {
        if (loginPerson != null) {
            return loginPerson.isInRole(PersonRole.ADMINISTRATOR);
        } else {
            return false;
        }
    }
}
