package ${rootPackageName}.handler;

import ${rootPackageName}.dbflute.allcommon.exception.EntityAlreadyExistsException;

import org.seasar.ymir.handler.ExceptionHandler;

public class EntityAlreadyExistsExceptionHandler extends HandlerBase implements
        ExceptionHandler<EntityAlreadyExistsException> {
    public String handle(EntityAlreadyExistsException t) {
        addNote("error.entityAlreadyExists");
        return toErrorPage();
    }
}
