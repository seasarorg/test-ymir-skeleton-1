package ${rootPackageName}.exception;

import org.seasar.ymir.message.Note;
import org.seasar.ymir.message.Notes;
import org.seasar.ymir.message.NotesHolder;

public class ApplicationRuntimeException extends RuntimeException implements
        NotesHolder {
    private static final long serialVersionUID = 1L;

    private Notes ${fieldPrefix}notes${fieldSuffix} = new Notes();

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
        return ${fieldPrefix}notes${fieldSuffix};
    }

    public ApplicationRuntimeException addNote(Note note) {
        ${fieldPrefix}notes${fieldSuffix}.add(note);
        return this;
    }
}
