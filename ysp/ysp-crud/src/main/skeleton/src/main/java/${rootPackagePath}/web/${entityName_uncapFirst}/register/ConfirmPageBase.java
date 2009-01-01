package ${rootPackageName}.web.${entityName_uncapFirst}.register;

public class ConfirmPageBase extends PageBase {
    protected ${rootPackageName}.dto.${entityName_uncapFirst}.register.ConfirmViewDto confirmView;

    protected ${rootPackageName}.converter.${entityName_uncapFirst}.register.ConfirmViewConverter confirmViewConverter;


    public ${rootPackageName}.dto.${entityName_uncapFirst}.register.ConfirmViewDto getConfirmView()
    {
        return this.confirmView;
    }

    @org.seasar.ymir.scope.annotation.Inject
    public void setConfirmViewConverter(${rootPackageName}.converter.${entityName_uncapFirst}.register.ConfirmViewConverter confirmViewConverter)
    {
        this.confirmViewConverter = confirmViewConverter;
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

    public String _post_back()
    {
        return null;

    }

    public String _post_next()
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
