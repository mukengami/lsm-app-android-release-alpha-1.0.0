package org.lsm.mobile.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.lsm.mobile.R;
import org.lsm.mobile.base.BaseSingleFragmentActivity;
import org.lsm.mobile.base.WebViewCourseInfoFragment;
import org.lsm.mobile.module.analytics.Analytics;

public class CourseInfoActivity extends BaseSingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        environment.getAnalyticsRegistry().trackScreenView(Analytics.Screens.COURSE_INFO_SCREEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        AuthPanelUtils.configureAuthPanel(findViewById(R.id.auth_panel), environment);
    }

    @Override
    public Fragment getFirstFragment() {
        final WebViewCourseInfoFragment fragment = new WebViewCourseInfoFragment();
        fragment.setArguments(getIntent().getExtras());
        return fragment;
    }
}
