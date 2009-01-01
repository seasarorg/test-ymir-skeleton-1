package ${rootPackageName}.web;

import ${rootPackageName}.LoginPerson;

import org.seasar.ymir.scope.annotation.In;
import org.seasar.ymir.scope.impl.SessionScope;

public class PageBase extends ${rootPackageName}.ymir.util.PageBase {
    private LoginPerson loginPerson;

    @In(SessionScope.class)
    final public void setLoginPerson(LoginPerson loginPerson) {
        this.loginPerson = loginPerson;
    }

    public LoginPerson getLoginPerson() {
        return loginPerson;
    }
}
