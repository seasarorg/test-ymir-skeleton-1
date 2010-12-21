package org.seasar.ymir.vili.skeleton.generator;

public interface IGenerator<P extends IParameters> {
    Class<P> getParametersClass();

    void generate(P parameters);
}
