package ${rootPackageName}.web;

public class _RootPageBase extends ${rootPackageName}.web.PageBase
{
    @org.seasar.ymir.annotation.Meta(name="source",value="return \"redirect:/person/login/login.html\";")
    public String _get()
    {
        return "redirect:/person/login/login.html";
    }
}
