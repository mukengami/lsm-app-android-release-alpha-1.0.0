package org.lsm.mobile.profiles;

import android.databinding.DataBindingUtil;

import org.lsm.mobile.databinding.FragmentUserProfileBinding;
import org.lsm.mobile.databinding.FragmentUserProfileBioBinding;
import org.lsm.mobile.view.PresenterFragmentTest;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class UserProfileBioFragmentTest extends PresenterFragmentTest<UserProfileBioFragment, UserProfileBioPresenter, UserProfileBioPresenter.ViewInterface> {

    FragmentUserProfileBioBinding binding;

    @Before
    public void before() {
        startFragment(UserProfileBioFragment.newInstance());
        binding = DataBindingUtil.getBinding(fragment.getView());
        assertThat(binding).isNotNull();
    }
    @Test
    public void click_onParentalConsentEditProfileButton_callsEditProfile() {
        binding.parentalConsentEditProfileButton.performClick();
        verify(presenter).onEditProfile();
    }

    @Test
    public void click_onIncompleteEditProfileButton_callsEditProfile() {
        binding.incompleteEditProfileButton.performClick();
        verify(presenter).onEditProfile();
    }

}
