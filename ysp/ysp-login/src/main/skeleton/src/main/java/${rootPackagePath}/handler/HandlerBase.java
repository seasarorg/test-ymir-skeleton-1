package ${rootPackageName}.handler;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.ymir.Request;
import org.seasar.ymir.RequestProcessor;
import org.seasar.ymir.Response;
import org.seasar.ymir.message.Note;
import org.seasar.ymir.message.Notes;
import org.seasar.ymir.scope.annotation.In;
import org.seasar.ymir.scope.impl.SessionScope;

import ${rootPackageName}.LoginPerson;
import ${rootPackageName}.ymir.util.Forward;

public class HandlerBase {
    private Request ymirRequest;

    private LoginPerson loginPerson;

    @Binding(bindingType = BindingType.MUST)
    final public void setYmirRequest(Request ymirRequest) {
        this.ymirRequest = ymirRequest;
    }

    @In(SessionScope.class)
    final public void setLoginPerson(LoginPerson loginPerson) {
        this.loginPerson = loginPerson;
    }

    final public Request getYmirRequest() {
        return ymirRequest;
    }

    public LoginPerson getLoginPerson() {
        return loginPerson;
    }

    final protected void addNote(String key) {
        addNote(new Note(key));
    }

    final protected void addNote(Note note) {
        if (note != null) {
            getNotes().add(note);
        }
    }

    final protected Notes getNotes() {
        Notes notes = (Notes) ymirRequest
                .getAttribute(RequestProcessor.ATTR_NOTES);
        if (notes == null) {
            notes = new Notes();
            ymirRequest.setAttribute(RequestProcessor.ATTR_NOTES, notes);
        }
        return notes;
    }

    final protected Response toErrorPage() {
        return Forward.to("/WEB-INF/zpt/error/error.html");
    }
}
