package ${rootPackageName}.exception;

public class LoginFailedRuntimeException extends ApplicationRuntimeException {
    private static final long serialVersionUID = 1L;

    public LoginFailedRuntimeException() {
    }

    public LoginFailedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginFailedRuntimeException(String message) {
        super(message);
    }

    public LoginFailedRuntimeException(Throwable cause) {
        super(cause);
    }
}
