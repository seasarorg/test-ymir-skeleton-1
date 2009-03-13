package ${rootPackageName}.web.${entityName_uncapFirst}.register;

import org.seasar.ymir.Response;
import org.seasar.ymir.conversation.annotation.Begin;
import org.seasar.ymir.conversation.annotation.Conversation;

import ${rootPackageName}.ymir.util.Redirect;

@Conversation(name = "${entityName_uncapFirst}.register", phase = "input", followAfter = "confirm")
public class InputPage extends InputPageBase {
    @Override
    @Begin
    public void _get() {
    }

    public void _get_continue() {
    }

    @Override
    public Response _post_next() {
        inputFormConverter.copyTo(${entityName_uncapFirst}, inputForm);
        return Redirect.to(ConfirmPage.class);
    }

    @Override
    public void _prerender() {
        inputFormConverter.copyTo(inputForm, ${entityName_uncapFirst});
    }
}
