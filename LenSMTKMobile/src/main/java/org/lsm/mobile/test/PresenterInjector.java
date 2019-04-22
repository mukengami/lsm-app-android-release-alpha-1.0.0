package org.lsm.mobile.test;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import org.lsm.mobile.view.Presenter;

@VisibleForTesting
public interface PresenterInjector {
    @Nullable
    Presenter<?> getPresenter();
}
