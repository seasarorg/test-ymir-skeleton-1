package ${rootPackageName};

import ${rootPackageName}.enm.PersonRole;

public interface LoginPersonBase<P> {
    /**
     * ログインユーザに対応するエンティティを返します。
     * <p>通常はデータベースエンティティを返します。
     * </p>
     * 
     * @return ログインユーザに対応するエンティティ。
     */
    P getPerson();

    /**
     * ログインユーザを一意に表す識別子を返します。
     * 
     * @return ログインユーザを一意に表す識別子。
     */
    Integer getId();

    /**
     * ログインユーザの表示名を返します。
     * 
     * @return ログインユーザの表示名。
     */
    String getName();

    /**
     * ログインユーザのロールを返します。
     * 
     * @return ログインユーザのロールの配列。
     * nullが返されることはありません。
     */
    PersonRole[] getPersonRoles();

    /**
     * ログインユーザが指定されたロールを持つかどうかを返します。
     * 
     * @param personRole ロール。
     * @return 指定されたロールを持つかどうか。
     */
    boolean isInRole(PersonRole personRole);
}
