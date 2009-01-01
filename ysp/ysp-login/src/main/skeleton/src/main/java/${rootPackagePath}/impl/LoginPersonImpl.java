package ${rootPackageName}.impl;

import ${rootPackageName}.LoginPerson;
import ${rootPackageName}.enm.PersonType;

// TODO LoginPersonのサンプル実装です。不要であれば削除して下さい。
public class LoginPersonImpl extends LoginPersonImplBase<Object> implements
		LoginPerson {
	public LoginPersonImpl() {
	}

	public LoginPersonImpl(Integer id, String name, Object person,
			PersonType personType) {
		super(id, name, person, personType);
	}
}
