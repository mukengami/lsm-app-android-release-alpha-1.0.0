package org.lsm.mobile.view;

import android.support.annotation.NonNull;

import org.lsm.mobile.base.BaseAppActivity;
import org.lsm.mobile.test.BaseTestCase;
import org.lsm.mobile.test.GenericSuperclassUtils;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.mockito.Mockito.mock;

public abstract class PresenterFragmentTest<FragmentT extends PresenterFragment<PresenterT, ViewT>, PresenterT extends Presenter<ViewT>, ViewT> extends BaseTestCase {

    protected FragmentT fragment;
    protected ViewT view;
    protected PresenterT presenter;

    protected void startFragment(@NonNull final FragmentT fragment) {
        this.presenter = mock(getPresenterType());
        fragment.presenter = presenter;
        SupportFragmentTestUtil.startVisibleFragment(fragment, HostActivity.class, android.R.id.content);
        this.fragment = fragment;
        this.view = fragment.view;
    }

    @SuppressWarnings("unchecked")
    private Class<PresenterT> getPresenterType() {
        return (Class<PresenterT>) GenericSuperclassUtils.getTypeArguments(getClass(), PresenterFragmentTest.class)[1];
    }

    private static class HostActivity extends BaseAppActivity {
    }
}
