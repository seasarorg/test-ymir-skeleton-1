package ${rootPackageName}.exception;

public class IllegalLogicDetectedRuntimeException extends
    ApplicationRuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalLogicDetectedRuntimeException() {
        super();
    }

    public IllegalLogicDetectedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalLogicDetectedRuntimeException(String message) {
        super(message);
    }

    public IllegalLogicDetectedRuntimeException(Throwable cause) {
        super(cause);
    }
}
