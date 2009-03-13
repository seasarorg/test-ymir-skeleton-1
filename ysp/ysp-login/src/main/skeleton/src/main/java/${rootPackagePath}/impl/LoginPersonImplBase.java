package ${rootPackageName}.impl;

import java.util.EnumSet;

import ${rootPackageName}.LoginPersonBase;
import ${rootPackageName}.enm.PersonRole;

// TODO LoginPersonBaseのサンプル実装です。不要であれば削除して下さい。
public class LoginPersonImplBase<P> implements LoginPersonBase<P> {
    protected Integer id;

    protected String name;

    protected P person;

    private EnumSet<PersonRole> personRoleSet;

    public LoginPersonImplBase() {
    }

    public LoginPersonImplBase(Integer id, String name, P person,
            PersonRole firstRole, PersonRole... restRoles) {
        this.id = id;
        this.name = name;
        this.person = person;
        personRoleSet = EnumSet.of(firstRole, restRoles);
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

    public PersonRole[] getPersonRoles() {
        return personRoleSet.toArray(new PersonRole[0]);
    }

    public boolean isInRole(PersonRole personRole) {
        return personRoleSet.contains(personRole);
    }
}
