package org.lsm.mobile.event;

import android.support.annotation.NonNull;

public class NewRelicEvent {
    @NonNull
    private String screenName;

    public NewRelicEvent(@NonNull String screenName) {
        this.screenName = screenName;
    }

    @NonNull
    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(@NonNull String screenName) {
        this.screenName = screenName;
    }
}
