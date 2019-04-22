package org.lsm.mobile.core;


import org.lsm.mobile.module.analytics.AnalyticsRegistry;
import org.lsm.mobile.module.db.IDatabase;
import org.lsm.mobile.module.download.IDownloadManager;
import org.lsm.mobile.module.notification.NotificationDelegate;
import org.lsm.mobile.module.prefs.LoginPrefs;
import org.lsm.mobile.module.prefs.UserPrefs;
import org.lsm.mobile.module.storage.IStorage;
import org.lsm.mobile.util.Config;
import org.lsm.mobile.view.Router;

/**
 * TODO - we should decompose this class into environment setting and service provider settings.
 */
public interface IEdxEnvironment {

    IDatabase getDatabase();

    IStorage getStorage();

    IDownloadManager getDownloadManager();

    UserPrefs getUserPrefs();

    LoginPrefs getLoginPrefs();

    AnalyticsRegistry getAnalyticsRegistry();

    NotificationDelegate getNotificationDelegate();

    Router getRouter();

    Config getConfig();
}
