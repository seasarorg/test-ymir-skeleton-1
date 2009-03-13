package ${rootPackageName}.web.person.mypage;

import ${rootPackageName}.constraint.annotation.Logined;
import ${rootPackageName}.enm.PersonRole;

@Logined(PersonRole.USER)
public class MypagePage extends MypagePageBase {
}
