package ${rootPackageName}.web.${entityName_uncapFirst}.register;

import ${rootPackageName}.dto.${entityName_uncapFirst}.register.ConfirmViewDto;
import ${rootPackageName}.ymir.util.Redirect;

import org.seasar.ymir.conversation.annotation.Conversation;
import org.seasar.ymir.conversation.annotation.End;

@Conversation(name = "${entityName_uncapFirst}.register", phase = "confirm", followAfter = "input")
public class ConfirmPage extends ConfirmPageBase {
    @Override
    public String _post_back() {
        return Redirect.to("/${entityName_uncapFirst}/register/input.html", "continue", "");
    }

    @Override
    @End
    public String _post_next() {
        ${entityName_uncapFirst}Bhv.insert(${entityName_uncapFirst});
        return Redirect.to("/${entityName_uncapFirst}/register/complete.html");
    }

    @Override
    public void _prerender() {
        confirmView = confirmViewConverter.copyTo(new ConfirmViewDto(), ${entityName_uncapFirst});
    }
}
