package ${rootPackageName}.web.${entityName_uncapFirst}.register;

public class InputPageBase extends PageBase {
    @org.seasar.ymir.annotation.Meta(name="property",value="inputForm")
    protected ${rootPackageName}.dto.${entityName_uncapFirst}.register.InputFormDto inputForm = new ${rootPackageName}.dto.${entityName_uncapFirst}.register.InputFormDto();

    protected ${rootPackageName}.converter.${entityName_uncapFirst}.register.InputFormConverter inputFormConverter;


<#list table.columns as column>
    @org.seasar.ymir.annotation.Meta(name="formProperty",value="inputForm")
    public String get${column.name?cap_first}()
    {
        return this.inputForm.get${column.name?cap_first}();
    }

    @org.seasar.ymir.annotation.Meta(name="formProperty",value="inputForm")
    @org.seasar.ymir.scope.annotation.RequestParameter
    public void set${column.name?cap_first}(String ${column.name})
    {
        this.inputForm.set${column.name?cap_first}(${column.name});
    }
</#list>

    public ${rootPackageName}.dto.${entityName_uncapFirst}.register.InputFormDto getInputForm()
    {
        return this.inputForm;
    }

    @org.seasar.ymir.scope.annotation.Inject
    public void setInputFormConverter(${rootPackageName}.converter.${entityName_uncapFirst}.register.InputFormConverter inputFormConverter)
    {
        this.inputFormConverter = inputFormConverter;
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
