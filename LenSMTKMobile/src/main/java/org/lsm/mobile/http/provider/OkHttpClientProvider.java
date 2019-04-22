package org.lsm.mobile.http.provider;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.lsm.mobile.BuildConfig;
import org.lsm.mobile.R;
import org.lsm.mobile.http.interceptor.CustomCacheQueryInterceptor;
import org.lsm.mobile.http.interceptor.JsonMergePatchInterceptor;
import org.lsm.mobile.http.interceptor.NewVersionBroadcastInterceptor;
import org.lsm.mobile.http.interceptor.NoCacheHeaderStrippingInterceptor;
import org.lsm.mobile.http.interceptor.OauthHeaderRequestInterceptor;
import org.lsm.mobile.http.authenticator.OauthRefreshTokenAuthenticator;
import org.lsm.mobile.http.interceptor.StaleIfErrorHandlingInterceptor;
import org.lsm.mobile.http.interceptor.StaleIfErrorInterceptor;
import org.lsm.mobile.http.interceptor.UserAgentInterceptor;
import org.lsm.mobile.http.util.Tls12SocketFactory;

import java.io.File;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public interface OkHttpClientProvider extends Provider<OkHttpClient> {
    @NonNull OkHttpClient get();
    @NonNull OkHttpClient getWithOfflineCache();
    @NonNull OkHttpClient getNonOAuthBased();

    @Singleton
    class Impl implements OkHttpClientProvider {
        private static final int cacheSize = 10 * 1024 * 1024; // 10 MiB

        private static final int FLAG_IS_OAUTH_BASED = 1;
        private static final int USES_OFFLINE_CACHE = 1 << 1;

        @Inject
        private Context context;

        private final OkHttpClient[] clients = new OkHttpClient[1 << 2];

        @NonNull
        @Override
        public OkHttpClient get() {
            return get(true, false);
        }

        @NonNull
        @Override
        public OkHttpClient getWithOfflineCache() {
            return get(true, true);
        }

        @NonNull
        @Override
        public OkHttpClient getNonOAuthBased() {
            return get(false, false);
        }

        @NonNull
        private synchronized OkHttpClient get(boolean isOAuthBased, boolean usesOfflineCache) {
            final int index = (isOAuthBased ? FLAG_IS_OAUTH_BASED : 0) |
                    (usesOfflineCache ? USES_OFFLINE_CACHE : 0);
            OkHttpClient client = clients[index];
            if (client == null) {
                final OkHttpClient.Builder builder = new OkHttpClient.Builder();
                List<Interceptor> interceptors = builder.interceptors();
                if (usesOfflineCache) {
                    final File cacheDirectory = new File(context.getFilesDir(), "http-cache");
                    if (!cacheDirectory.exists()) {
                        cacheDirectory.mkdirs();
                    }
                    final Cache cache = new Cache(cacheDirectory, cacheSize);
                    builder.cache(cache);
                    interceptors.add(new StaleIfErrorInterceptor());
                    interceptors.add(new StaleIfErrorHandlingInterceptor());
                    interceptors.add(new CustomCacheQueryInterceptor(context));
                    builder.networkInterceptors().add(new NoCacheHeaderStrippingInterceptor());
                }
                interceptors.add(new JsonMergePatchInterceptor());
                interceptors.add(new UserAgentInterceptor(
                        System.getProperty("http.agent") + " " +
                                context.getString(R.string.app_name) + "/" +
                                BuildConfig.APPLICATION_ID + "/" +
                                BuildConfig.VERSION_NAME));
                if (isOAuthBased) {
                    interceptors.add(new OauthHeaderRequestInterceptor(context));
                }
                interceptors.add(new NewVersionBroadcastInterceptor());
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    interceptors.add(loggingInterceptor);
                }
                builder.authenticator(new OauthRefreshTokenAuthenticator(context));
                // Enable TLS 1.2 support
                client = Tls12SocketFactory.enableTls12OnPreLollipop(builder).build();
                clients[index] = client;
            }
            return client;
        }
    }
}
