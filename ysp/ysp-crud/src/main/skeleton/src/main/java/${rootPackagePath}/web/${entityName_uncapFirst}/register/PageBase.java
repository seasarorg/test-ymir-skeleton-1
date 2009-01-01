package ${rootPackageName}.web.${entityName_uncapFirst}.register;

import ${rootPackageName}.dbflute.exbhv.${entityName}Bhv;

import org.seasar.ymir.conversation.impl.ConversationScope;
import org.seasar.ymir.scope.annotation.In;
import org.seasar.ymir.scope.annotation.Inject;
import org.seasar.ymir.scope.annotation.Out;

import ${rootPackageName}.dbflute.exentity.${entityName};

public class PageBase extends ${rootPackageName}.web.PageBase {
    protected ${entityName}Bhv ${entityName_uncapFirst}Bhv;

    protected ${entityName} ${entityName_uncapFirst} = new ${entityName}();

    @Inject
    final public void set${entityName}Bhv(${entityName}Bhv ${entityName_uncapFirst}Bhv) {
        this.${entityName_uncapFirst}Bhv = ${entityName_uncapFirst}Bhv;
    }

    @In(ConversationScope.class)
    final public void set${entityName}(${entityName} ${entityName_uncapFirst}) {
        this.${entityName_uncapFirst} = ${entityName_uncapFirst};
    }

    @Out(ConversationScope.class)
    public ${entityName} get${entityName}() {
        return ${entityName_uncapFirst};
    }
}
