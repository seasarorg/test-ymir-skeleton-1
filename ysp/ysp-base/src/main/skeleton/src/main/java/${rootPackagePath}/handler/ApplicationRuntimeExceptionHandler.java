package ${rootPackageName}.handler;

import java.util.Iterator;

import ${rootPackageName}.exception.ApplicationRuntimeException;

import org.seasar.ymir.handler.ExceptionHandler;
import org.seasar.ymir.message.Note;

public class ApplicationRuntimeExceptionHandler extends HandlerBase implements
        ExceptionHandler<ApplicationRuntimeException> {
    public String handle(ApplicationRuntimeException t) {
        for (Iterator<Note> itr = t.getNotes().get(); itr.hasNext();) {
            addNote(itr.next());
        }
        return toErrorPage();
    }
}
