package ${rootPackageName}.web.${entityName_uncapFirst}.register;

import org.seasar.ymir.Response;

public class ConfirmPageBase extends PageBase {
    protected ${rootPackageName}.dto.${entityName_uncapFirst}.register.ConfirmViewDto confirmView;

    protected ${rootPackageName}.converter.${entityName_uncapFirst}.register.ConfirmViewConverter confirmViewConverter;

    public ${rootPackageName}.dto.${entityName_uncapFirst}.register.ConfirmViewDto getConfirmView() {
        return this.confirmView;
    }

    @org.seasar.ymir.scope.annotation.Inject
    public void setConfirmViewConverter(${rootPackageName}.converter.${entityName_uncapFirst}.register.ConfirmViewConverter confirmViewConverter) {
        this.confirmViewConverter = confirmViewConverter;
    }

    public void _get() {
    }

    public Response _post_back() {
        return null;
    }

    public Response _post_next() {
        return null;
    }

    public void _prerender() {
    }
}
