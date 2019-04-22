package org.lsm.mobile.view;

import org.lsm.mobile.R;

public class WebViewDiscoverDegreesFragment extends WebViewDiscoverFragment {
    @Override
    protected String getSearchUrl() {
        return environment.getConfig().getDiscoveryConfig().getDegreeDiscoveryConfig().getBaseUrl();
    }

    @Override
    protected int getQueryHint() {
        return R.string.search_for_degrees;
    }

    @Override
    protected boolean isSearchEnabled() {
        return environment.getConfig().getDiscoveryConfig().getDegreeDiscoveryConfig().isSearchEnabled();
    }
}
