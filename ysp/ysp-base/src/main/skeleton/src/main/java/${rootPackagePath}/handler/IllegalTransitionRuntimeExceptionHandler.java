package ${rootPackageName}.handler;

import org.seasar.ymir.Response;
import org.seasar.ymir.conversation.IllegalTransitionRuntimeException;
import org.seasar.ymir.handler.annotation.ExceptionHandler;

public class IllegalTransitionRuntimeExceptionHandler extends HandlerBase {
    @ExceptionHandler
    public Response handle(IllegalTransitionRuntimeException t) {
        addNote("error.illegalTransition");
        return toErrorPage();
    }
}
