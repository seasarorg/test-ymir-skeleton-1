package ${rootPackageName}.handler;

import org.seasar.ymir.Response;
import org.seasar.ymir.handler.annotation.ExceptionHandler;
import org.seasar.ymir.message.Note;

import ${rootPackageName}.exception.IllegalPageStateRuntimeException;

public class IllegalPageStateRuntimeExceptionHandler extends HandlerBase {
    @ExceptionHandler
    public Response handle(IllegalPageStateRuntimeException t) {
        if (t.getNotes().isEmpty()) {
            addNote("error.illegalPageState");
        } else {
            for (Note note : t.getNotes().getNotes()) {
                addNote(note);
            }
        }
        return toErrorPage();
    }
}
