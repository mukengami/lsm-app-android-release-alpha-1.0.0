package org.lsm.mobile.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.google.inject.Inject;

import org.lsm.mobile.core.IEdxEnvironment;
import org.lsm.mobile.course.CourseAPI;
import org.lsm.mobile.model.api.EnrolledCoursesResponse;
import org.lsm.mobile.module.prefs.LoginPrefs;
import org.lsm.mobile.util.Config;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import roboguice.RoboGuice;

import static org.lsm.mobile.http.util.CallUtil.executeStrict;

public class CoursesAsyncLoader extends AsyncTaskLoader<AsyncTaskResult<List<EnrolledCoursesResponse>>> {
    private AsyncTaskResult<List<EnrolledCoursesResponse>> mData;
    private final Context context;
    private Observer mObserver;

    @Inject
    IEdxEnvironment environment;

    @Inject
    private Config config;

    @Inject
    CourseAPI api;

    @Inject
    LoginPrefs loginPrefs;

    public CoursesAsyncLoader(Context context) {
        super(context);
        this.context = context;
        RoboGuice.injectMembers(context, this);
    }

    @Override
    public AsyncTaskResult<List<EnrolledCoursesResponse>> loadInBackground() {
        List<EnrolledCoursesResponse> enrolledCoursesResponse = null;

        AsyncTaskResult<List<EnrolledCoursesResponse>> result = new AsyncTaskResult<>();

        try {
            enrolledCoursesResponse = executeStrict(api.getEnrolledCourses());
            environment.getNotificationDelegate().syncWithServerForFailure();
            environment.getNotificationDelegate().checkCourseEnrollment(enrolledCoursesResponse);

        } catch (Exception exception) {
            result.setEx(exception);
        }

        result.setResult(enrolledCoursesResponse);

        return result;
    }

    @Override
    protected void onReset() {
        onStopLoading();

        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }

    }

    @Override
    protected void onStartLoading() {

        if (mData != null) {
            deliverResult(mData);
        }

        if (mObserver == null) {
            mObserver = new Observer() {
                @Override
                public void update(Observable observable, Object o) {

                }
            };
        }

        if (takeContentChanged() || mData == null) {
            forceLoad();
        }

    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(AsyncTaskResult<List<EnrolledCoursesResponse>> data) {
        super.onCanceled(data);
        releaseResources(data);
    }

    @Override
    public void deliverResult(AsyncTaskResult<List<EnrolledCoursesResponse>> data) {

        if (isReset()) {
            releaseResources(data);
            return;
        }

        AsyncTaskResult<List<EnrolledCoursesResponse>> oldData = mData;
        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    private void releaseResources(AsyncTaskResult<List<EnrolledCoursesResponse>> data) {

    }

}
