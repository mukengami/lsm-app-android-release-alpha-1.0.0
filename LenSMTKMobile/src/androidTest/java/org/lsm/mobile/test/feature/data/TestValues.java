package org.lsm.mobile.test.feature.data;

import com.google.gson.Gson;

import org.lsm.mobile.authentication.AuthResponse;
import org.lsm.mobile.model.api.ProfileModel;

public enum TestValues {
    ;

    public static final Credentials ACTIVE_USER_CREDENTIALS = new Credentials("honor@example.com", "edx");
    public static final ProfileModel DUMMY_PROFILE = new Gson().fromJson("{\"id\": 1, \"username\": \"user\", \"email\": \"email@example.com\", \"name\": \"name\", \"course_enrollments\": \"https://mobile-devi.sandbox.edx.org/api/mobile/v0.5/users/staff/course_enrollments/\"}", ProfileModel.class);
    public static final AuthResponse INVALID_AUTH_TOKEN_RESPONSE = new Gson().fromJson("{\"access_token\": \"I am an invalid token\", \"token_type\": \"Bearer\", \"expires_in\": 2591999, \"scope\": \"\"}", AuthResponse.class);
}
