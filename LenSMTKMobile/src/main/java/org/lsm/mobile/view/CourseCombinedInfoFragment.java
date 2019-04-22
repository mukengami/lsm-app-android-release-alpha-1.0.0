package org.lsm.mobile.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.lsm.mobile.R;
import org.lsm.mobile.base.BaseFragment;
import org.lsm.mobile.core.IEdxEnvironment;
import org.lsm.mobile.event.NetworkConnectivityChangeEvent;
import org.lsm.mobile.http.callback.ErrorHandlingOkCallback;
import org.lsm.mobile.http.notifications.FullScreenErrorNotification;
import org.lsm.mobile.http.notifications.SnackbarErrorNotification;
import org.lsm.mobile.http.provider.OkHttpClientProvider;
import org.lsm.mobile.interfaces.RefreshListener;
import org.lsm.mobile.logger.Logger;
import org.lsm.mobile.model.api.AnnouncementsModel;
import org.lsm.mobile.model.api.EnrolledCoursesResponse;
import org.lsm.mobile.social.facebook.FacebookProvider;
import org.lsm.mobile.util.NetworkUtil;
import org.lsm.mobile.util.StandardCharsets;
import org.lsm.mobile.util.WebViewUtil;
import org.lsm.mobile.view.custom.EdxWebView;
import org.lsm.mobile.view.custom.URLInterceptorWebViewClient;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.Request;

public class CourseCombinedInfoFragment extends BaseFragment implements RefreshListener {

    static final String TAG = CourseCombinedInfoFragment.class.getCanonicalName();

    private final Logger logger = new Logger(getClass().getName());

    private EdxWebView announcementWebView;

    private EnrolledCoursesResponse courseData;
    private List<AnnouncementsModel> savedAnnouncements;

    @Inject
    protected IEdxEnvironment environment;

    @Inject
    private OkHttpClientProvider okHttpClientProvider;

    private FullScreenErrorNotification errorNotification;

    private SnackbarErrorNotification snackbarErrorNotification;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logger.debug("created: " + getClass().getName());

        final Context context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_course_combined_info, container, false);

        announcementWebView = (EdxWebView) view.findViewById(R.id.announcement_webview);
        URLInterceptorWebViewClient client = new URLInterceptorWebViewClient(
                getActivity(), announcementWebView);
        // treat every link as external link in this view, so that all links will open in external browser
        client.setAllLinksAsExternal(true);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        errorNotification = new FullScreenErrorNotification(announcementWebView);
        snackbarErrorNotification = new SnackbarErrorNotification(announcementWebView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            try {
                savedAnnouncements = savedInstanceState.getParcelableArrayList(Router.EXTRA_ANNOUNCEMENTS);
            } catch (Exception ex) {
                logger.error(ex);
            }

        }

        try {
            final Bundle bundle = getArguments();
            courseData = (EnrolledCoursesResponse) bundle.getSerializable(Router.EXTRA_COURSE_DATA);
            FacebookProvider fbProvider = new FacebookProvider();

            if (courseData != null) {
                //Create the inflater used to create the announcement list
                if (savedAnnouncements == null) {
                    loadAnnouncementData(courseData);
                } else {
                    populateAnnouncements(savedAnnouncements);
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (savedAnnouncements != null) {
            outState.putParcelableArrayList(Router.EXTRA_ANNOUNCEMENTS, new ArrayList<Parcelable>(savedAnnouncements));
        }
    }

    private void loadAnnouncementData(EnrolledCoursesResponse enrollment) {
        okHttpClientProvider.getWithOfflineCache().newCall(new Request.Builder()
                .url(enrollment.getCourse().getCourse_updates())
                .get()
                .build())
                .enqueue(new ErrorHandlingOkCallback<List<AnnouncementsModel>>(getActivity(),
                        new TypeToken<List<AnnouncementsModel>>() {
                        }, errorNotification, snackbarErrorNotification,
                        this) {
                    @Override
                    protected void onResponse(final List<AnnouncementsModel> announcementsList) {
                        if (getActivity() == null) {
                            return;
                        }
                        savedAnnouncements = announcementsList;
                        if (announcementsList != null && announcementsList.size() > 0) {
                            populateAnnouncements(announcementsList);
                        } else {
                            errorNotification.showError(R.string.no_announcements_to_display,
                                    FontAwesomeIcons.fa_exclamation_circle, 0, null);
                        }
                    }

                    @Override
                    protected void onFinish() {
                        if (getActivity() == null) {
                            return;
                        }
                        if (!EventBus.getDefault().isRegistered(CourseCombinedInfoFragment.this)) {
                            EventBus.getDefault().registerSticky(CourseCombinedInfoFragment.this);
                        }
                    }
                });

    }

    private void populateAnnouncements(@NonNull List<AnnouncementsModel> announcementsList) {
        errorNotification.hideError();

        StringBuilder buff = WebViewUtil.getIntialWebviewBuffer(getActivity(), logger);

        buff.append("<body>");
        for (AnnouncementsModel model : announcementsList) {
            buff.append("<div class=\"header\">");
            buff.append(model.getDate());
            buff.append("</div>");
            buff.append("<div class=\"separator\"></div>");
            buff.append("<div>");
            buff.append(model.getContent());
            buff.append("</div>");
        }
        buff.append("</body>");

        announcementWebView.clearCache(true);
        announcementWebView.loadDataWithBaseURL(environment.getConfig().getApiHostURL(), buff.toString(), "text/html", StandardCharsets.UTF_8.name(), null);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(NetworkConnectivityChangeEvent event) {
        if (!NetworkUtil.isConnected(getContext())) {
            if (!errorNotification.isShowing()) {
                snackbarErrorNotification.showOfflineError(this);
            }
        }
    }

    @Override
    public void onRefresh() {
        errorNotification.hideError();
        loadAnnouncementData(courseData);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onRevisit() {
        if (NetworkUtil.isConnected(getActivity())) {
            snackbarErrorNotification.hideError();
        }
    }
}
