package org.lsm.mobile.core;


import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.lsm.mobile.module.analytics.AnalyticsRegistry;
import org.lsm.mobile.module.db.IDatabase;
import org.lsm.mobile.module.download.IDownloadManager;
import org.lsm.mobile.module.notification.NotificationDelegate;
import org.lsm.mobile.module.prefs.LoginPrefs;
import org.lsm.mobile.module.prefs.UserPrefs;
import org.lsm.mobile.module.storage.IStorage;
import org.lsm.mobile.util.Config;
import org.lsm.mobile.view.Router;

import de.greenrobot.event.EventBus;

@Singleton
public class EdxEnvironment implements IEdxEnvironment {

    @Inject
    IDatabase database;

    @Inject
    IStorage storage;

    @Inject
    IDownloadManager downloadManager;

    @Inject
    UserPrefs userPrefs;

    @Inject
    LoginPrefs loginPrefs;

    @Inject
    AnalyticsRegistry analyticsRegistry;

    @Inject
    NotificationDelegate notificationDelegate;

    @Inject
    Router router;

    @Inject
    Config config;

    @Inject
    EventBus eventBus;

    @Override
    public IDatabase getDatabase() {
        return database;
    }

    @Override
    public IDownloadManager getDownloadManager() {
        return downloadManager;
    }

    @Override
    public UserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public LoginPrefs getLoginPrefs() {
        return loginPrefs;
    }

    public AnalyticsRegistry getAnalyticsRegistry() {
        return analyticsRegistry;
    }

    @Override
    public NotificationDelegate getNotificationDelegate() {
        return notificationDelegate;
    }

    @Override
    public Router getRouter() {
        return router;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public IStorage getStorage() {
        return storage;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
