package ${rootPackageName}.web.sample.login;

import ${rootPackageName}.constraint.annotation.Logined;
import ${rootPackageName}.enm.PersonRole;

// TODO サンプルです。不要であれば削除して下さい。
@Logined(PersonRole.USER)
public class UserPage extends UserPageBase {

}
