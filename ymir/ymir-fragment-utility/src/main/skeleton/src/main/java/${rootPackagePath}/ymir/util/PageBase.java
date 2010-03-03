package ${rootPackageName}.ymir.util;

import java.util.Iterator;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.ymir.Request;
import org.seasar.ymir.RequestProcessor;
import org.seasar.ymir.Response;
import org.seasar.ymir.message.Note;
import org.seasar.ymir.message.Notes;
import org.seasar.ymir.redirection.impl.RedirectionScope;
import org.seasar.ymir.response.PassthroughResponse;
import org.seasar.ymir.scope.annotation.In;
import org.seasar.ymir.scope.annotation.Out;

abstract public class PageBase {
    public static final String SCHEME_FORWARD = PageUtils.SCHEME_FORWARD;

    public static final String SCHEME_REDIRECT = PageUtils.SCHEME_REDIRECT;

    public static final String SCHEME_PROCEED = PageUtils.SCHEME_PROCEED;

    public static final String PASSTHROUGH = PageUtils.SCHEME_PASSTHROUGH;

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
        addNote(temporary, new Note(key));
    }

    final protected void addNote(String category, String key) {
        addNote(false, category, key);
    }

    final protected void addNote(boolean temporary, String category, String key) {
        addNote(temporary, category, new Note(key));
    }

    final protected void addNote(Note note) {
        addNote(false, note);
    }

    final protected void addNote(boolean temporary, Note note) {
        if (note != null) {
            getNotes(temporary).add(note);
        }
    }

    final protected void addNote(String category, Note note) {
        addNote(false, category, note);
    }

    final protected void addNote(boolean temporary, String category, Note note) {
        if (note != null) {
            getNotes(temporary).add(category, note);
        }
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

    @In(RedirectionScope.class)
    final public void setTemporaryNotes(Notes temporaryNotes) {
        Notes notes = getNotes();

        for (Iterator<String> itr = temporaryNotes.categories(); itr.hasNext();) {
            String category = itr.next();
            for (Note note : temporaryNotes.getNotes(category)) {
                notes.add(category, note);
            }
        }
    }

    final protected Response passthrough() {
        return new PassthroughResponse();
    }
}
