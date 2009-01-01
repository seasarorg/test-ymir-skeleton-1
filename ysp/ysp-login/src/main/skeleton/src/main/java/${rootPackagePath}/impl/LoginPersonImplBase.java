package ${rootPackageName}.impl;

import ${rootPackageName}.LoginPersonBase;
import ${rootPackageName}.enm.PersonType;

// TODO LoginPersonBaseのサンプル実装です。不要であれば削除して下さい。
public class LoginPersonImplBase<P> implements LoginPersonBase<P> {
	protected Integer id;

	protected String name;

	protected P person;

	protected PersonType personType;

	public LoginPersonImplBase() {
	}

	public LoginPersonImplBase(Integer id, String name, P person,
			PersonType personType) {
		this.id = id;
		this.name = name;
		this.person = person;
		this.personType = personType;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public P getPerson() {
		return person;
	}

	public PersonType getPersonType() {
		return personType;
	}
}
