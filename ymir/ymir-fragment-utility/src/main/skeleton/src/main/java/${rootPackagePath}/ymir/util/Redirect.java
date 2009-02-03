package ${rootPackageName}.ymir.util;

import org.seasar.ymir.Response;
import org.seasar.ymir.response.RedirectResponse;

public class Redirect {
    protected Redirect() {
    }

    public static Response to(String path, Object... params) {
        return new RedirectResponse(PageUtils
                .constructPath(path, false, params));
    }

    public static Response to(Class<?> pageClass, Object... params) {
        return new RedirectResponse(PageUtils.constructPath(pageClass, false,
                params));
    }

    public static Response toNonCached(String path, Object... params) {
        return new RedirectResponse(PageUtils.constructPath(path, true, params));
    }

    public static Response toNonCached(Class<?> pageClass, Object... params) {
        return new RedirectResponse(PageUtils.constructPath(pageClass, true,
                params));
    }
}
