package org.lsm.mobile.user;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.inject.Inject;

import org.lsm.mobile.event.ProfilePhotoUpdatedEvent;
import org.lsm.mobile.module.prefs.LoginPrefs;
import org.lsm.mobile.task.Task;

import de.greenrobot.event.EventBus;

public class DeleteAccountImageTask extends
        Task<Void> {

    @Inject
    private UserService userService;

    @Inject
    private LoginPrefs loginPrefs;

    @NonNull
    private final String username;

    public DeleteAccountImageTask(@NonNull Context context, @NonNull String username) {
        super(context);
        this.username = username;
    }


    public Void call() throws Exception {
        userService.deleteProfileImage(username).execute();
        return null;
    }

    @Override
    protected void onSuccess(Void response) throws Exception {
        EventBus.getDefault().post(new ProfilePhotoUpdatedEvent(username, null));
        // Delete the logged in user's ProfileImage
        loginPrefs.setProfileImage(username, null);
    }
}
