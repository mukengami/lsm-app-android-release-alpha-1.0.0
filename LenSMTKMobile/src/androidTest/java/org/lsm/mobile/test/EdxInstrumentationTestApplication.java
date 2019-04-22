package org.lsm.mobile.test;

import android.support.annotation.Nullable;

import org.lsm.mobile.base.MainApplication;
import org.lsm.mobile.view.Presenter;

public class EdxInstrumentationTestApplication extends MainApplication implements PresenterInjector {

    @Nullable
    private Presenter<?> nextPresenter = null;

    @Nullable
    @Override
    public Presenter<?> getPresenter() {
        try {
            return nextPresenter;
        } finally {
            nextPresenter = null;
        }
    }

    public void setNextPresenter(@Nullable Presenter<?> nextPresenter) {
        this.nextPresenter = nextPresenter;
    }
}
