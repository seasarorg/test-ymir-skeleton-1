package org.seasar.ymir.vili.skeleton.generator;

/**
 * 自動生成を行なうオブジェクトを表すインタフェースです。
 * 
 * @author skirnir
 * @param <P> 自動生成のためのパラメータを表すクラス。
 * @see IParameters
 * @see GeneratedResult
 */
public interface IGenerator<P extends IParameters> {
    /**
     * このIGeneratorを使って自動生成する時のパラメータを表すクラスを返します。
     * 
     * @return 自動生成する時のパラメータを表すクラス。
     */
    Class<P> getParametersClass();

    /**
     * このIGeneratorを初期化します。
     * <p>このメソッドはフレームワークから呼び出されます。
     * 引数で渡された値を必要に応じて保持するようにして下さい。
     * </p>
     * 
     * @param targetProjectPath 自動生成のターゲットとするプロジェクトのルートディレクトリのファイルパス。
     * @param targetRootPackageName 自動生成のターゲットとするプロジェクトのルートパッケージ名。
     */
    void initialize(String targetProjectPath, String targetRootPackageName);

    /**
     * 自動生成を行ないます。
     * 
     * @param parameters 自動生成のためのパラメータ。
     * @return 自動生成の結果を表すオブジェクト。nullを返すこともできます。
     */
    GeneratedResult generate(P parameters);
}
