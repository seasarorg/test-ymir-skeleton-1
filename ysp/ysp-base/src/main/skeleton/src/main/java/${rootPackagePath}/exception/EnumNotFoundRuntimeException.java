package ${rootPackageName}.exception;

public class EnumNotFoundRuntimeException extends ApplicationRuntimeException {
    private static final long serialVersionUID = 1L;

    private Class<? extends Enum<?>> enumClass;

    private Object value;

    public EnumNotFoundRuntimeException(Class<? extends Enum<?>> enumClass,
            Object value) {
        this.enumClass = enumClass;
        this.value = value;
    }

    public Class<? extends Enum<?>> getEnumClass() {
        return enumClass;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "enum (type=" + enumClass + ", value=" + value + ") not found";
    }
}
