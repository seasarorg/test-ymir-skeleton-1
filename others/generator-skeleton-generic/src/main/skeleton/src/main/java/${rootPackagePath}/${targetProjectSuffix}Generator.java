package ${rootPackageName};

import org.seasar.ymir.vili.skeleton.generator.IGenerator;
import org.seasar.ymir.vili.skeleton.generator.IParameters;

abstract public class ${targetProjectSuffix}Generator<P extends IParameters>
        extends GenericGenerator implements IGenerator<P> {
    public ${targetProjectSuffix}Generator() {
        super(${landmarkClassName}.class, "${targetRootPackageName}");
    }
}
