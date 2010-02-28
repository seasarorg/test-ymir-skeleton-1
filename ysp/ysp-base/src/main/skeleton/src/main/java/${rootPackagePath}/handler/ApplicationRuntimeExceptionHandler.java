package ${rootPackageName}.handler;

import org.seasar.ymir.Response;
import org.seasar.ymir.handler.annotation.ExceptionHandler;
import org.seasar.ymir.message.Note;

import ${rootPackageName}.exception.ApplicationRuntimeException;

public class ApplicationRuntimeExceptionHandler extends HandlerBase {
    @ExceptionHandler
    public Response handle(ApplicationRuntimeException t) {
        if (t.getNotes().isEmpty()) {
            addNote("error.generic");
        } else {
            for (Note note : t.getNotes().getNotes()) {
                addNote(note);
            }
        }
        return toErrorPage();
    }
}
