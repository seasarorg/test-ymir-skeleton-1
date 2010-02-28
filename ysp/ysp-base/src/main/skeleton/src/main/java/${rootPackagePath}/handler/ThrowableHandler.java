package ${rootPackageName}.handler;

import org.seasar.ymir.Response;
import org.seasar.ymir.handler.annotation.ExceptionHandler;

public class ThrowableHandler extends HandlerBase {
    @ExceptionHandler
    public Response handle(Throwable t) {
        addNote("error.generic");
        return toErrorPage();
    }
}
