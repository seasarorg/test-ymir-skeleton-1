package org.seasar.ymir.vili.skeleton.generator;

public interface IGenerator<P extends IParameters> {
    Class<P> getParametersClass();

    void initialize(String targetProjectPath, String targetRootPackageName);

    void generate(P parameters);
}
