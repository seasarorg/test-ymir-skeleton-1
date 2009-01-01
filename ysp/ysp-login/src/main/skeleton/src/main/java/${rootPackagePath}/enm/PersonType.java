package ${rootPackageName}.enm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ${rootPackageName}.exception.EnumNotFoundRuntimeException;

public enum PersonType {
    // TODO アプリケーションに合わせて定義しなおして下さい。
    USER(0), ADMINISTRATOR(1), ;

    private static final Map<Integer, PersonType> enumMap;

    private Integer value;

    private PersonType(Integer value) {
        this.value = value;
    }

    public static PersonType enumOf(Integer value) {
        if (value == null) {
            return null;
        }
        if (!enumMap.containsKey(value)) {
            throw new EnumNotFoundRuntimeException(PersonType.class, value);
        }
        return enumMap.get(value);
    }

    static {
        Map<Integer, PersonType> map = new HashMap<Integer, PersonType>();
        for (PersonType enm : values()) {
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
