package org.lsm.mobile.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.google.inject.Inject;

import org.lsm.mobile.R;
import org.lsm.mobile.core.IEdxEnvironment;
import org.lsm.mobile.http.HttpStatus;
import org.lsm.mobile.http.HttpStatusException;
import org.lsm.mobile.http.notifications.FullScreenErrorNotification;
import org.lsm.mobile.http.provider.OkHttpClientProvider;
import org.lsm.mobile.interfaces.RefreshListener;
import org.lsm.mobile.interfaces.WebViewStatusListener;
import org.lsm.mobile.logger.Logger;
import org.lsm.mobile.util.WebViewUtil;
import org.lsm.mobile.view.custom.EdxWebView;
import org.lsm.mobile.view.custom.URLInterceptorWebViewClient;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * An abstract fragment providing basic functionality for URL interception, its follow up action,
 * error handling and show page progress based on page status.
 */
public abstract class BaseWebViewFragment extends OfflineSupportBaseFragment
        implements WebViewStatusListener, RefreshListener {
    protected final Logger logger = new Logger(getClass().getName());

    private EdxWebView webView;
    protected ProgressBar progressWheel;

    protected FullScreenErrorNotification errorNotification;

    @Inject
    protected IEdxEnvironment environment;

    @Inject
    private OkHttpClientProvider okHttpClientProvider;

    protected URLInterceptorWebViewClient client;

    public abstract FullScreenErrorNotification initFullScreenErrorNotification();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        errorNotification = initFullScreenErrorNotification();
        webView = (EdxWebView) view.findViewById(R.id.webview);
        progressWheel = (ProgressBar) view.findViewById(R.id.loading_indicator);

        initWebView();
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }

    private void initWebView() {
        client = new URLInterceptorWebViewClient(getActivity(), webView);

        // if all the links are to be treated as external
        client.setAllLinksAsExternal(isAllLinksExternal());

        client.setPageStatusListener(pageStatusListener);
    }

    /**
     * Loads the given URL into {@link #webView}.
     *
     * @param url The URL to load.
     */
    protected void loadUrl(@NonNull String url) {
        WebViewUtil.loadUrlBasedOnOsVersion(getContext(), webView, url, this, errorNotification,
                okHttpClientProvider, R.string.lbl_reload, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefresh();
                    }
                });
    }

    @Override
    public void showLoadingProgress() {
        if (progressWheel != null) {
            progressWheel.setVisibility(View.VISIBLE);
        }
        if (webView != null) {
            webView.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideLoadingProgress() {
        if (progressWheel != null) {
            progressWheel.setVisibility(View.GONE);
        }
        if (webView != null) {
            webView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void clearWebView() {
        WebViewUtil.clearWebviewHtml(webView);
    }

    /**
     * By default, all links will not be treated as external.
     * Depends on host, as long as the links have same host, they are treated as non-external links.
     *
     * @return
     */
    protected boolean isAllLinksExternal() {
        return false;
    }

    /**
     * See description of: {@link org.lsm.mobile.view.custom.URLInterceptorWebViewClient.IPageStatusListener#onPageLoadProgressChanged(WebView, int)
     * IPageStatusListener#onPageLoadProgressChanged}.
     */
    protected void onWebViewLoadProgressChanged(int progress) {
    }

    /*
     * In order to avoid reflection issues of public functions in event bus especially those that
     * aren't available on a certain api level, this listener has been refactored to a class
     * variable which is better explained in following references:
     * https://github.com/greenrobot/EventBus/issues/149
     * http://greenrobot.org/eventbus/documentation/faq/
     */
    private URLInterceptorWebViewClient.IPageStatusListener pageStatusListener = new URLInterceptorWebViewClient.IPageStatusListener() {
        @Override
        public void onPageStarted() {
            showLoadingProgress();
        }

        @Override
        public void onPageFinished() {
            hideLoadingProgress();
        }

        @Override
        public void onPageLoadError(WebView view, int errorCode, String description,
                                    String failingUrl) {
            errorNotification.showError(getContext(),
                    new HttpStatusException(Response.error(HttpStatus.SERVICE_UNAVAILABLE,
                            ResponseBody.create(MediaType.parse("text/plain"), description))),
                    R.string.lbl_reload, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onRefresh();
                        }
                    });
            clearWebView();
        }

        @Override
        @TargetApi(Build.VERSION_CODES.M)
        public void onPageLoadError(WebView view, WebResourceRequest request,
                                    WebResourceResponse errorResponse,
                                    boolean isMainRequestFailure) {
            if (isMainRequestFailure) {
                errorNotification.showError(getContext(),
                        new HttpStatusException(Response.error(errorResponse.getStatusCode(),
                                ResponseBody.create(MediaType.parse(errorResponse.getMimeType()),
                                        errorResponse.getReasonPhrase()))),
                        R.string.lbl_reload, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onRefresh();
                            }
                        });
                clearWebView();
            }
        }

        @Override
        public void onPageLoadProgressChanged(WebView view, int progress) {
            onWebViewLoadProgressChanged(progress);
        }
    };
}
