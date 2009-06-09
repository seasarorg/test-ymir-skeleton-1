package ${rootPackageName}.ymir.util;

import org.seasar.ymir.Response;
import org.seasar.ymir.response.ForwardResponse;

public class Forward {
    protected Forward() {
    }

    public static Response to(String path) {
        return to(path, null);
    }

    public static Response to(String path, String param, Object... params) {
        Object[] pms;
        if (param != null) {
            pms = new Object[1 + params.length];
            pms[0] = param;
            System.arraycopy(params, 0, pms, 1, params.length);
        } else {
            pms = params;
        }

        return new ForwardResponse(PageUtils.constructPath(path, false, pms));
    }

    public static Response to(Class<?> pageClass) {
        return to(pageClass, (String) null);
    }

    public static Response to(Class<?> pageClass, String param,
            Object... params) {
        Object[] pms;
        if (param != null) {
            pms = new Object[1 + params.length];
            pms[0] = param;
            System.arraycopy(params, 0, pms, 1, params.length);
        } else {
            pms = params;
        }

        return new ForwardResponse(PageUtils.constructPath(pageClass, false,
                pms));
    }
}
