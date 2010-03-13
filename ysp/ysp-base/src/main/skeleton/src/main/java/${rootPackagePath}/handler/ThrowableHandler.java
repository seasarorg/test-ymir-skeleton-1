package ${rootPackageName}.handler;

import org.seasar.ymir.Response;
import org.seasar.ymir.handler.annotation.ExceptionHandler;
import org.seasar.ymir.message.Notes;
import org.seasar.ymir.message.NotesHolder;

public class ThrowableHandler extends HandlerBase {
    @ExceptionHandler
    public Response handle(Throwable t) {
        Notes notes = null;
        if (t instanceof NotesHolder) {
            notes = ((NotesHolder) t).getNotes();
        }
        if (notes == null || notes.isEmpty()) {
            addNote("error.generic");
        } else {
            addNotes(notes);
        }
        return toErrorPage();
    }
}
