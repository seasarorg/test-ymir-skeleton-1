package ${rootPackageName}.exception;

import org.seasar.ymir.message.Note;
import org.seasar.ymir.message.Notes;
import org.seasar.ymir.message.NotesHolder;

public class ApplicationException extends Exception implements NotesHolder {
    private static final long serialVersionUID = 1L;

    private Notes ${fieldPrefix}notes${fieldSuffix} = new Notes();

    public ApplicationException() {
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public Notes getNotes() {
        return ${fieldPrefix}notes${fieldSuffix};
    }

    public ApplicationException addNote(Note note) {
        ${fieldPrefix}notes${fieldSuffix}.add(note);
        return this;
    }
}
