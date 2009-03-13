package ${rootPackageName}.logic;

import ${rootPackageName}.LoginPerson;
import ${rootPackageName}.enm.PersonRole;
import ${rootPackageName}.impl.LoginPersonImpl;

public class AuthenticationLogic {
    /**
     * ログイン処理を行ないます。
     * <p>指定されたアカウントとパスワードについてログイン処理を行ないます。
     * アカウントとパスワードの組が正しい場合は、アカウントに対応するLoginPersonオブジェクトを返します。
     * 正しくない場合はnullを返します。
     * </p>
     * 
     * @param account アカウント。nullを指定してはいけません。
     * @param password パスワード。nullを指定してはいけません。
     * @return アカウントに対応するLoginPersonオブジェクトまたはnull。
     */
    public LoginPerson login(String account, String password) {
        // TODO ログイン処理を実装して下さい。
        if (password.equals(account)) {
            if ("administrator".equals(account)) {
                return new LoginPersonImpl(1, account, null, PersonRole.USER,
                        PersonRole.ADMINISTRATOR);
            } else {
                return new LoginPersonImpl(2, account, null, PersonRole.USER);
            }
        } else {
            return null;
        }
    }
}
