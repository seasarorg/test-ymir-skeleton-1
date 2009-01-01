package ${rootPackageName}.web.person.login;

import org.seasar.ymir.scope.annotation.Inject;

import ${rootPackageName}.logic.AuthenticationLogic;

public class LoginPageBase extends ${rootPackageName}.web.person.PageBase {
    protected AuthenticationLogic authenticationLogic;

    protected String account;

    protected String password;


    @Inject
    public void setAuthenticationLogic(AuthenticationLogic authenticationLogic) {
        this.authenticationLogic = authenticationLogic;
    }

    public String getAccount()
    {
        return this.account;
    }

    @org.seasar.ymir.scope.annotation.RequestParameter
    public void setAccount(String account)
    {
        this.account = account;
    }

    public String getPassword()
    {
        return this.password;
    }

    @org.seasar.ymir.scope.annotation.RequestParameter
    public void setPassword(String password)
    {
        this.password = password;
    }

    public void _get()
    {

    }

    @org.seasar.ymir.annotation.Meta(name="source",value={"throw ex;","ex"})
    @org.seasar.ymir.conversation.annotation.Begin(alwaysBegin=true)
    public void _permissionDenied(org.seasar.ymir.constraint.PermissionDeniedException ex)
        throws org.seasar.ymir.constraint.PermissionDeniedException
    {
        throw ex;
    }

    public String _post()
    {
        return null;

    }

    public void _prerender()
    {

    }

    public void _validationFailed(org.seasar.ymir.message.Notes notes)
    {

    }
}
