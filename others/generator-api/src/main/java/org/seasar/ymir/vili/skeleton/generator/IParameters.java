package org.seasar.ymir.vili.skeleton.generator;

/**
 * 自動生成のパラメータを表すインタフェースです。
 * 
 * @author skirnir
 * @see IGenerator
 */
public interface IParameters {
    /**
     * ルートパッケージ名を設定します。
     * <p>自動生成されるものがクラスやJavaリソースでない場合は呼び出されないか、
     * 指定された値は意味を持ちません。
     * </p>
     * 
     * @param rootPackageName ルートパッケージ名。nullを指定してはいけません。
     */
    void setRootPackageName(String rootPackageName);

    /**
     * 生成するクラスやJavaリソースのパッケージ名を設定します。
     * <p>自動生成されるものがクラスやJavaリソースでない場合は呼び出されないか、
     * 指定された値は意味を持ちません。
     * </p>
     * 
     * @param packageName パッケージ名。nullを指定してはいけません。
     */
    void setPackageName(String packageName);

    /**
     * 生成するクラスの名前を設定します。
     * <p>名前はパッケージ名を含みません。
     * </p>
     * <p>自動生成されるものがクラスでない場合は呼び出されないか、
     * 指定された値は意味を持ちません。
     * </p>
     * 
     * @param className クラス名。nullを指定してはいけません。
     */
    void setClassName(String className);
}
