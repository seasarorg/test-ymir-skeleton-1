package ${rootPackageName}.web.person.mypage;

public class MypagePageBase extends ${rootPackageName}.web.person.PageBase {
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

    public void _prerender()
    {

    }

    public void _validationFailed(org.seasar.ymir.message.Notes notes)
    {

    }
}
