package org.seasar.ymir.vili.skeleton.generator;

import java.util.ArrayList;
import java.util.List;

/**
 * 自動生成の結果を表すクラスです。
 * 
 * @author skirnir
 * @see IGenerator
 */
public class GeneratedResult {
    private List<String> activeResourcePaths = new ArrayList<String>();

    /**
     * 自動生成後にエディタで開きたいリソースのパスのリストを返します。
     * 
     * @return エディタで開きたいリソースのパスのリスト。nullを返すことはありません。
     */
    public List<String> getActiveResourcePaths() {
        return new ArrayList<String>(activeResourcePaths);
    }

    /**
     * 自動生成後にエディタで開きたいリソースのパスを追加します。
     * 
     * @param activeResourcePath エディタで開きたいリソースのパス。nullを指定してはいけません。
     * @return このオブジェクト。
     */
    public GeneratedResult addActiveResourcePath(String activeResourcePath) {
        activeResourcePaths.add(activeResourcePath);
        return this;
    }
}
