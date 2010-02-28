package ${rootPackageName}.handler;

import org.seasar.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.ymir.Response;
import org.seasar.ymir.handler.annotation.ExceptionHandler;

public class IllegalPageStateRuntimeExceptionHandler extends HandlerBase {
    @ExceptionHandler
    public Response handle(EntityAlreadyDeletedException t) {
        addNote("error.illegalPageState");
        return toErrorPage();
    }
}
