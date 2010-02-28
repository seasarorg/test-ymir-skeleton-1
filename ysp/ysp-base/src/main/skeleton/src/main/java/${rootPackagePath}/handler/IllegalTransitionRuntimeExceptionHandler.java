package ${rootPackageName}.handler;

import org.seasar.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.ymir.Response;
import org.seasar.ymir.handler.annotation.ExceptionHandler;

public class IllegalTransitionRuntimeExceptionHandler extends HandlerBase {
    @ExceptionHandler
    public Response handle(EntityAlreadyDeletedException t) {
        addNote("error.illegalTransition");
        return toErrorPage();
    }
}
