package org.lsm.mobile.test.feature.interactor;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;

import org.lsm.mobile.R;
import org.lsm.mobile.base.MainApplication;
import org.lsm.mobile.test.feature.data.Credentials;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.lsm.mobile.test.feature.matcher.ActionBarMatcher.isInActionBar;
import static org.hamcrest.CoreMatchers.allOf;

public class LogInScreenInteractor {

    public LogInScreenInteractor observeLogInScreen() {
        final MainApplication app = MainApplication.instance();
        final CharSequence title = app.getResources().getString(R.string.login_title);
        onView(allOf(isInActionBar(), withText(title.toString()))).check(matches(isCompletelyDisplayed()));
        onUsernameView().check(matches(isCompletelyDisplayed()));
        onPasswordView().check(matches(isCompletelyDisplayed()));
        onLogInButton().check(matches(isCompletelyDisplayed()));
        return this;
    }

    public MyCoursesScreenInteractor logIn(Credentials credentials) {
        onUsernameView().perform(replaceText(credentials.email));
        onPasswordView().perform(replaceText(credentials.password));
        onLogInButton().perform(click());
        return new MyCoursesScreenInteractor();
    }

    public LandingScreenInteractor navigateBack() {
        Espresso.pressBack();
        return new LandingScreenInteractor();
    }

    private ViewInteraction onUsernameView() {
        return onView(withHint(R.string.email_username));
    }

    private ViewInteraction onPasswordView() {
        return onView(withHint(R.string.password));
    }

    private ViewInteraction onLogInButton() {
        return onView(withContentDescription(R.string.login_btn));
    }
}
