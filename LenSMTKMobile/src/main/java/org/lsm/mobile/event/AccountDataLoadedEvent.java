package org.lsm.mobile.event;

import android.support.annotation.NonNull;

import org.lsm.mobile.user.Account;

/**
 * Fired whenever we get/update a user's account details.
 */
public class AccountDataLoadedEvent {
    @NonNull
    private final Account account;

    public AccountDataLoadedEvent(@NonNull Account account) {
        this.account = account;
    }

    @NonNull
    public Account getAccount() {
        return account;
    }
}
