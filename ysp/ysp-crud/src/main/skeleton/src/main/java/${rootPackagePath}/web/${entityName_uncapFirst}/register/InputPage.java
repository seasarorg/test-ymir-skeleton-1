package ${rootPackageName}.web.${entityName_uncapFirst}.register;

import ${rootPackageName}.ymir.util.Redirect;

import org.seasar.ymir.conversation.annotation.Begin;
import org.seasar.ymir.conversation.annotation.Conversation;

@Conversation(name = "${entityName_uncapFirst}.register", phase = "input", followAfter = "confirm")
public class InputPage extends InputPageBase {
    @Override
    @Begin
    public void _get() {
    }

    public void _get_continue() {
    }

    @Override
    public String _post_next() {
        inputFormConverter.copyTo(${entityName_uncapFirst}, inputForm);
        return Redirect.to("/${entityName_uncapFirst}/register/confirm.html");
    }

    @Override
    public void _prerender() {
        inputFormConverter.copyTo(inputForm, ${entityName_uncapFirst});
    }
}
