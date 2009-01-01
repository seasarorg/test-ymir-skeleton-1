package ${rootPackageName}.exception;

import org.seasar.ymir.message.Note;
import org.seasar.ymir.message.Notes;

public class ApplicationRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Notes notes = new Notes();

    public ApplicationRuntimeException() {
    }

    public ApplicationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationRuntimeException(String message) {
        super(message);
    }

    public ApplicationRuntimeException(Throwable cause) {
        super(cause);
    }

    public Notes getNotes() {
        return notes;
    }

    public ApplicationRuntimeException addNote(Note note) {
        notes.add(note);
        return this;
    }
}
