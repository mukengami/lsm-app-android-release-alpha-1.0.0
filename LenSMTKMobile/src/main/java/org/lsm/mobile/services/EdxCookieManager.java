package org.lsm.mobile.services;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.inject.Inject;

import org.lsm.mobile.authentication.LoginService;
import org.lsm.mobile.event.SessionIdRefreshEvent;
import org.lsm.mobile.logger.Logger;
import org.lsm.mobile.util.Config;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import okhttp3.Cookie;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import roboguice.RoboGuice;

/**
 *  A central place for course data model transformation
 */
public class EdxCookieManager {

    // We'll assume that cookies are valid for at least one hour; after that
    // they'll be requeried on API levels lesser than Marshmallow (which
    // provides an error callback with the HTTP error code) prior to usage.
    private static final long FRESHNESS_INTERVAL = TimeUnit.HOURS.toMillis(1);

    private long authSessionCookieExpiration = -1;

    protected final Logger logger = new Logger(getClass().getName());

    private static EdxCookieManager instance;

    @Inject
    private Config config;

    @Inject
    private LoginService loginService;

    private Call<RequestBody> loginCall;

    public static synchronized EdxCookieManager getSharedInstance(@NonNull final Context context) {
        if ( instance == null ) {
            instance = new EdxCookieManager();
            RoboGuice.getInjector(context).injectMembers(instance);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.createInstance(context);
            }
        }
        return instance;
    }

    public void clearWebWiewCookie() {
        CookieManager.getInstance().removeAllCookie();
        authSessionCookieExpiration = -1;
    }

    public synchronized  void tryToRefreshSessionCookie( ){
        if (loginCall == null || loginCall.isCanceled()) {
            loginCall = loginService.login();
            loginCall.enqueue(new Callback<RequestBody>() {
                @Override
                public void onResponse(@NonNull final Call<RequestBody> call,
                                       @NonNull final Response<RequestBody> response) {
                    clearWebWiewCookie();
                    final CookieManager cookieManager = CookieManager.getInstance();
                    for (Cookie cookie : Cookie.parseAll(
                            call.request().url(), response.headers())) {
                        cookieManager.setCookie(config.getApiHostURL(), cookie.toString());
                    }
                    authSessionCookieExpiration = System.currentTimeMillis() + FRESHNESS_INTERVAL;
                    EventBus.getDefault().post(new SessionIdRefreshEvent(true));
                    loginCall = null;
                }

                @Override
                public void onFailure(@NonNull final Call<RequestBody> call,
                                      @NonNull final Throwable error) {
                    EventBus.getDefault().post(new SessionIdRefreshEvent(false));
                    loginCall = null;
                }
            });
        }
    }

    public boolean isSessionCookieMissingOrExpired() {
        return authSessionCookieExpiration < System.currentTimeMillis();
    }
}
