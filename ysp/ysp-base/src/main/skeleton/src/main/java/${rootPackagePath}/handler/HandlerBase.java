package ${rootPackageName}.handler;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.ymir.Request;
import org.seasar.ymir.RequestProcessor;
import org.seasar.ymir.Response;
import org.seasar.ymir.message.Note;
import org.seasar.ymir.message.Notes;

import ${rootPackageName}.ymir.util.Forward;

public class HandlerBase {
    private Request ${fieldPrefix}ymirRequest${fieldSuffix};

    @Binding(bindingType = BindingType.MUST)
    final public void setYmirRequest(Request ymirRequest) {
        ${fieldSpecialPrefix}${fieldPrefix}ymirRequest${fieldSuffix} = ymirRequest;
    }

    final public Request getYmirRequest() {
        return ${fieldPrefix}ymirRequest${fieldSuffix};
    }

    final protected void addNote(String key) {
        addNote(new Note(key));
    }

    final protected void addNote(Note note) {
        if (note != null) {
            getNotes().add(note);
        }
    }

    final protected void addNotes(Notes notes) {
        getNotes().add(notes);
    }

    final protected Notes getNotes() {
        Notes notes = (Notes) ${fieldPrefix}ymirRequest${fieldSuffix}
                .getAttribute(RequestProcessor.ATTR_NOTES);
        if (notes == null) {
            notes = new Notes();
            ${fieldPrefix}ymirRequest${fieldSuffix}.setAttribute(RequestProcessor.ATTR_NOTES, notes);
        }
        return notes;
    }

    final protected Response toErrorPage() {
        return Forward.to("/WEB-INF/zpt/error/error.html");
    }
}
