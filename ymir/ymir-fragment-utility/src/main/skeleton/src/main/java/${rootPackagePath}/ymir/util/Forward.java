package ${rootPackageName}.ymir.util;

import org.seasar.ymir.Response;
import org.seasar.ymir.response.ForwardResponse;

public class Forward {
    protected Forward() {
    }

    public static Response to(String path, Object... params) {
        return new ForwardResponse(PageUtils.constructPath(path, false, params));
    }

    public static Response to(Class<?> pageClass, Object... params) {
        return new ForwardResponse(PageUtils.constructPath(pageClass, false,
                params));
    }
}
