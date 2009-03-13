package ${rootPackageName}.web.${entityName_uncapFirst}.register;

import org.seasar.ymir.Response;
import org.seasar.ymir.conversation.annotation.Conversation;
import org.seasar.ymir.conversation.annotation.End;

import ${rootPackageName}.dto.${entityName_uncapFirst}.register.ConfirmViewDto;
import ${rootPackageName}.ymir.util.Redirect;

@Conversation(name = "${entityName_uncapFirst}.register", phase = "confirm", followAfter = "input")
public class ConfirmPage extends ConfirmPageBase {
    @Override
    public Response _post_back() {
        return Redirect.to(InputPage.class, "continue");
    }

    @Override
    @End
    public Response _post_next() {
        ${entityName_uncapFirst}Bhv.insert(${entityName_uncapFirst});
        return Redirect.to(CompletePage.class);
    }

    @Override
    public void _prerender() {
        confirmView = confirmViewConverter.copyTo(new ConfirmViewDto(), ${entityName_uncapFirst});
    }
}
