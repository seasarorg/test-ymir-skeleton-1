package ${rootPackageName}.ymir.util;

import org.seasar.ymir.Response;
import org.seasar.ymir.id.action.Action;
import org.seasar.ymir.id.action.GetAction;
import org.seasar.ymir.response.RedirectResponse;

import com.example.ymir.util.PageUtils;

public class Redirect {
    protected Redirect() {
    }

    public static Response to(String path, Object... params) {
        return new RedirectResponse(PageUtils
                .constructPath(path, false, params));
    }

    public static Response to(Class<?> pageClass) {
        return to(pageClass, (String) null);
    }

    public static Response to(Class<?> pageClass,
            Class<? extends GetAction> actionInterface, Object... params) {
        Object[] pms = new Object[1 + params.length];
        pms[0] = "";
        System.arraycopy(params, 0, pms, 1, params.length);

        try {
            return to(pageClass, (String) actionInterface.getField(
                    Action.FIELD_KEY).get(null), pms);
        } catch (Throwable t) {
            throw new RuntimeException(
                    "Cannot get action key from action method '"
                            + actionInterface
                            + "'. Try to re-generate Page class.", t);
        }
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

        return new RedirectResponse(PageUtils.constructPath(pageClass, false,
                pms));
    }

    public static Response toNonCached(String path, Object... params) {
        return new RedirectResponse(PageUtils.constructPath(path, true, params));
    }

    public static Response toNonCached(Class<?> pageClass) {
        return toNonCached(pageClass, (String) null);
    }

    public static Response toNonCached(Class<?> pageClass,
            Class<? extends GetAction> actionInterface, Object... params) {
        Object[] pms = new Object[1 + params.length];
        pms[0] = "";
        System.arraycopy(params, 0, pms, 1, params.length);

        try {
            return toNonCached(pageClass, (String) actionInterface.getField(
                    Action.FIELD_KEY).get(null), pms);
        } catch (Throwable t) {
            throw new RuntimeException(
                    "Cannot get action key from action method '"
                            + actionInterface
                            + "'. Try to re-generate Page class.", t);
        }
    }

    public static Response toNonCached(Class<?> pageClass, String param,
            Object... params) {
        Object[] pms;
        if (param != null) {
            pms = new Object[1 + params.length];
            pms[0] = param;
            System.arraycopy(params, 0, pms, 1, params.length);
        } else {
            pms = params;
        }

        return new RedirectResponse(PageUtils.constructPath(pageClass, true,
                pms));
    }
}
