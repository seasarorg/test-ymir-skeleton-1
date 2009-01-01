package ${rootPackageName}.web.person.logout;

public class LogoutPageBase extends ${rootPackageName}.web.person.PageBase {
    @org.seasar.ymir.annotation.Meta(name="source",value="return \"redirect:/person/login/login.html\";")
    public String _get()
    {
        return "redirect:/person/login/login.html";
    }
}
