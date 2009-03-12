package ${rootPackageName}.handler;

import org.seasar.dbflute.exception.EntityAlreadyExistsException;
import org.seasar.ymir.Response;
import org.seasar.ymir.handler.annotation.ExceptionHandler;

public class EntityAlreadyExistsExceptionHandler extends HandlerBase {
    @ExceptionHandler
    public Response handle(EntityAlreadyExistsException t) {
        addNote("error.entityAlreadyExists");
        return toErrorPage();
    }
}
