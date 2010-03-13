package ${rootPackageName}.exception;

public class EnumNotFoundRuntimeException extends ApplicationRuntimeException {
    private static final long serialVersionUID = 1L;

    private Class<? extends Enum<?>> ${fieldPrefix}enumClass${fieldSuffix};

    private Object ${fieldPrefix}value${fieldSuffix};

    public EnumNotFoundRuntimeException(Class<? extends Enum<?>> enumClass,
            Object value) {
        ${fieldSpecialPrefix}${fieldPrefix}enumClass${fieldSuffix} = enumClass;
        ${fieldSpecialPrefix}${fieldPrefix}value${fieldSuffix} = value;
    }

    public Class<? extends Enum<?>> getEnumClass() {
        return ${fieldPrefix}enumClass${fieldSuffix};
    }

    public Object getValue() {
        return ${fieldPrefix}value${fieldSuffix};
    }

    @Override
    public String toString() {
        return "enum (type=" + ${fieldPrefix}enumClass${fieldSuffix} + ", value=" + ${fieldPrefix}value${fieldSuffix} + ") not found";
    }
}
