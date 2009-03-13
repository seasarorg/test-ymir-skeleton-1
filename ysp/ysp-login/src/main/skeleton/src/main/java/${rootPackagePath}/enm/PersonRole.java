package ${rootPackageName}.enm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ${rootPackageName}.exception.EnumNotFoundRuntimeException;

public enum PersonRole {
    // TODO アプリケーションに合わせて定義しなおして下さい。
    USER(0), ADMINISTRATOR(1), ;

    private static final Map<Integer, PersonRole> enumMap;

    private Integer value;

    private PersonRole(Integer value) {
        this.value = value;
    }

    public static PersonRole enumOf(Integer value) {
        if (value == null) {
            return null;
        }
        if (!enumMap.containsKey(value)) {
            throw new EnumNotFoundRuntimeException(PersonRole.class, value);
        }
        return enumMap.get(value);
    }

    static {
        Map<Integer, PersonRole> map = new HashMap<Integer, PersonRole>();
        for (PersonRole enm : values()) {
            map.put(enm.getValue(), enm);
        }
        enumMap = Collections.unmodifiableMap(map);
    }

    public Integer getValue() {
        return value;
    }

    public boolean sameAs(Integer value) {
        return value.equals(value);
    }
}
