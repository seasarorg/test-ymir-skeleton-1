package ${rootPackageName}.handler;

import ${rootPackageName}.dbflute.allcommon.exception.EntityAlreadyDeletedException;

import org.seasar.ymir.handler.ExceptionHandler;

public class EntityAlreadyDeletedExceptionHandler extends HandlerBase implements
        ExceptionHandler<EntityAlreadyDeletedException> {
    public String handle(EntityAlreadyDeletedException t) {
        addNote("error.entityAlreadyDeleted");
        return toErrorPage();
    }
}
