package ${rootPackageName}.handler;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.ymir.Request;
import org.seasar.ymir.RequestProcessor;
import org.seasar.ymir.Response;
import org.seasar.ymir.message.Note;
import org.seasar.ymir.message.Notes;
import org.seasar.ymir.redirection.impl.RedirectionScope;
import org.seasar.ymir.scope.annotation.Out;

import ${rootPackageName}.ymir.util.Forward;

public class HandlerBase {
    private Request ${fieldPrefix}ymirRequest${fieldSuffix};

    private Notes ${fieldPrefix}temporaryNotes${fieldSuffix};

    @Binding(bindingType = BindingType.MUST)
    final public void setYmirRequest(Request ymirRequest) {
        ${fieldSpecialPrefix}${fieldPrefix}ymirRequest${fieldSuffix} = ymirRequest;
    }

    final public Request getYmirRequest() {
        return ${fieldPrefix}ymirRequest${fieldSuffix};
    }

    final protected void addNote(String key) {
        addNote(false, key);
    }

    final protected void addNote(boolean temporary, String key) {
        addNote(new Note(key));
    }

    final protected void addNote(Note note) {
        addNote(false, note);
    }

    final protected void addNote(boolean temporary, Note note) {
        if (note != null) {
            getNotes().add(note);
        }
    }

    final protected void addNotes(Notes notes) {
        addNotes(false, notes);
    }

    final protected void addNotes(boolean temporary, Notes notes) {
        getNotes(temporary).add(notes);
    }

    final protected Notes getNotes() {
        return getNotes(false);
    }

    final protected Notes getNotes(boolean temporary) {
        Notes notes;
        if (temporary) {
            if (${fieldPrefix}temporaryNotes${fieldSuffix} == null) {
                ${fieldPrefix}temporaryNotes${fieldSuffix} = new Notes();
            }
            notes = ${fieldPrefix}temporaryNotes${fieldSuffix};
        } else {
            notes = (Notes) ${fieldPrefix}ymirRequest${fieldSuffix}
                    .getAttribute(RequestProcessor.ATTR_NOTES);
            if (notes == null) {
                notes = new Notes();
                ${fieldPrefix}ymirRequest${fieldSuffix}.setAttribute(RequestProcessor.ATTR_NOTES, notes);
            }
        }
        return notes;
    }

    @Out(RedirectionScope.class)
    final public Notes getTemporaryNotes() {
        return ${fieldPrefix}temporaryNotes${fieldSuffix};
    }

    final protected Response toErrorPage() {
        return Forward.to("/WEB-INF/zpt/error/error.html");
    }
}
