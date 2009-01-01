package ${rootPackageName}.ymir.interceptor;

import java.util.List;

import org.seasar.ymir.Dispatch;
import org.seasar.ymir.Dispatcher;
import org.seasar.ymir.Request;
import org.seasar.ymir.Response;
import org.seasar.ymir.interceptor.impl.AbstractYmirProcessInterceptor;
import org.seasar.ymir.util.ResponseUtils;
import org.seasar.ymir.util.ServletUtils;

/**
 * キャッシュコントロールに関するヘッダ指定を一括して行なうためのインターセプタです。
 */
public class CacheControlInterceptor extends AbstractYmirProcessInterceptor {
    private String[] ignorePaths = null;

    /**
     * キャッシュコントロールに関するヘッダ指定を行わないページのコンテキスト相対パスを設定します。
     * 
     * @param ignroePathList
     */
    public void setIgnorePaths(List<String> ignroePathList) {
        ignorePaths = ignroePathList.toArray(new String[0]);
    }

    @Override
    public Response responseCreated(Request request, Response response) {
        Dispatch dispatch = request.getCurrentDispatch();
        if (dispatch.getDispatcher() != Dispatcher.REQUEST) {
            // requestの時以外にも処理をしてしまうと、forwardやincludeの場合に
            // 予期せぬno-cacheヘッダがついてしまうことがあるのでこうしている。
            return response;
        }

        if (ignorePaths != null) {
            // 無視すべきパスである場合は何もしない。
            String path = ServletUtils.normalizePath(request
                    .getCurrentDispatch().getPath());
            for (int i = 0; i < ignorePaths.length; i++) {
                if (path.equals(ignorePaths[i])) {
                    return response;
                }
            }
        }

        ResponseUtils.setPrivateCache(response);
        return response;
    }
}
