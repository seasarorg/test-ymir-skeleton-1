package ${rootPackageName};

import ${rootPackageName}.enm.PersonType;

public interface LoginPersonBase<P> {
    P getPerson();

    Integer getId();

    String getName();

    PersonType getPersonType();
}
