package ${rootPackageName}.handler;

import org.seasar.dbflute.exception.EntityAlreadyUpdatedException;
import org.seasar.ymir.Response;
import org.seasar.ymir.handler.annotation.ExceptionHandler;

public class EntityAlreadyUpdatedExceptionHandler extends HandlerBase {
    @ExceptionHandler
    public Response handle(EntityAlreadyUpdatedException t) {
        addNote("error.entityAlreadyUpdated");
        return toErrorPage();
    }
}
