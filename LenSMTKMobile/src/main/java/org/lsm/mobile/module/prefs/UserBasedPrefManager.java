package org.lsm.mobile.module.prefs;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.lsm.mobile.base.MainApplication;
import org.lsm.mobile.model.api.ProfileModel;
import org.lsm.mobile.module.notification.NotificationPreference;

/**
 * the current implemenation of user profile can only keep one user profile at a time.
 * for example if user A logout and user B login, the profile information about user A is gone.
 *
 * For notification setting, we do want to keep user's individual profile survives login/logout
 */
public class UserBasedPrefManager extends PrefManager {
    public static enum UserPrefType { NOTIFICATION }

    public  static final UserBasedPrefManager getInstance(UserPrefType userPrefType){
        Context context = MainApplication.instance().getApplicationContext();
        ProfileModel profileModel = MainApplication.getEnvironment(MainApplication.instance()).getLoginPrefs().getCurrentUserProfile();
        String prefName = profileModel == null ? userPrefType.name() : profileModel.username + "_" + userPrefType.name();
        return new UserBasedPrefManager(context, prefName);
    }

    private UserBasedPrefManager(Context context, String prefName) {
        super(context, prefName);
    }

    /**
     * get notification profile.
     * @return
     */
    public NotificationPreference getNotificationPreference() {
        String json = getString(Key.NOTIFICATION_PROFILE_JSON);
        if (json == null) {
            return new NotificationPreference();
        }

        Gson gson = new GsonBuilder().create();
        NotificationPreference res = gson.fromJson(json, NotificationPreference.class);
        return res;
    }

    public void saveNotificationPreference(NotificationPreference preference){
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(preference);
        super.put(Key.NOTIFICATION_PROFILE_JSON, json);
    }


}
