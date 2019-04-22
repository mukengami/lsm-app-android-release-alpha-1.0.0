package org.lsm.mobile.view;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.google.inject.Inject;

import org.lsm.mobile.R;
import org.lsm.mobile.base.BaseFragment;
import org.lsm.mobile.core.IEdxEnvironment;
import org.lsm.mobile.event.MediaStatusChangeEvent;
import org.lsm.mobile.logger.Logger;
import org.lsm.mobile.module.analytics.Analytics;
import org.lsm.mobile.module.prefs.PrefManager;
import org.lsm.mobile.util.FileUtil;
import org.lsm.mobile.view.dialog.IDialogCallback;
import org.lsm.mobile.view.dialog.NetworkCheckDialogFragment;

import de.greenrobot.event.EventBus;


public class SettingsFragment extends BaseFragment {

    public static final String TAG = SettingsFragment.class.getCanonicalName();

    private final Logger logger = new Logger(SettingsFragment.class);

    @Inject
    protected IEdxEnvironment environment;

    @Inject
    ExtensionRegistry extensionRegistry;

    private Switch wifiSwitch;
    private Switch sdCardSwitch;
    private LinearLayout sdCardSettingsLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        environment.getAnalyticsRegistry().trackScreenView(Analytics.Screens.SETTINGS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View layout = inflater.inflate(R.layout.fragment_settings, container, false);
        wifiSwitch = (Switch) layout.findViewById(R.id.wifi_setting);
        sdCardSwitch = (Switch) layout.findViewById(R.id.download_location_switch);
        sdCardSettingsLayout = (LinearLayout) layout.findViewById(R.id.sd_card_setting_layout);
        updateWifiSwitch();
        updateSDCardSwitch();
        final LinearLayout settingsLayout = (LinearLayout) layout.findViewById(R.id.settings_layout);
        for (SettingsExtension extension : extensionRegistry.forType(SettingsExtension.class)) {
            extension.onCreateSettingsView(settingsLayout);
        }
        return layout;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void updateWifiSwitch() {
        final PrefManager wifiPrefManager = new PrefManager(
                getActivity().getBaseContext(), PrefManager.Pref.WIFI);

        wifiSwitch.setOnCheckedChangeListener(null);
        wifiSwitch.setChecked(wifiPrefManager.getBoolean(PrefManager.Key.DOWNLOAD_ONLY_ON_WIFI, true));
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wifiPrefManager.put(PrefManager.Key.DOWNLOAD_ONLY_ON_WIFI, true);
                    wifiPrefManager.put(PrefManager.Key.DOWNLOAD_OFF_WIFI_SHOW_DIALOG_FLAG, true);
                } else {
                    showWifiDialog();
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(MediaStatusChangeEvent event) {
        sdCardSwitch.setEnabled(event.isSdCardAvailable());
    }

    private void updateSDCardSwitch() {
        final PrefManager prefManager =
                new PrefManager(getActivity().getBaseContext(), PrefManager.Pref.USER_PREF);
        if (!environment.getConfig().isDownloadToSDCardEnabled() || Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            sdCardSettingsLayout.setVisibility(View.GONE);
        } else {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().registerSticky(this);
            }
            sdCardSwitch.setOnCheckedChangeListener(null);
            sdCardSwitch.setChecked(prefManager.getBoolean(PrefManager.Key.DOWNLOAD_TO_SDCARD, true));
            sdCardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    prefManager.put(PrefManager.Key.DOWNLOAD_TO_SDCARD, isChecked);
                }
            });
            sdCardSwitch.setEnabled(FileUtil.isRemovableStorageAvailable(getActivity()));
        }
    }

    protected void showWifiDialog() {
        final NetworkCheckDialogFragment newFragment = NetworkCheckDialogFragment.newInstance(getString(R.string.wifi_dialog_title_help),
                getString(R.string.wifi_dialog_message_help),
                new IDialogCallback() {
                    @Override
                    public void onPositiveClicked() {
                        try {
                            PrefManager wifiPrefManager = new PrefManager
                                    (getActivity().getBaseContext(), PrefManager.Pref.WIFI);
                            wifiPrefManager.put(PrefManager.Key.DOWNLOAD_ONLY_ON_WIFI, false);
                            updateWifiSwitch();
                        } catch (Exception ex) {
                            logger.error(ex);
                        }
                    }

                    @Override
                    public void onNegativeClicked() {
                        try {
                            PrefManager wifiPrefManager = new PrefManager(
                                    getActivity().getBaseContext(), PrefManager.Pref.WIFI);
                            wifiPrefManager.put(PrefManager.Key.DOWNLOAD_ONLY_ON_WIFI, true);
                            wifiPrefManager.put(PrefManager.Key.DOWNLOAD_OFF_WIFI_SHOW_DIALOG_FLAG, true);

                            updateWifiSwitch();
                        } catch (Exception ex) {
                            logger.error(ex);
                        }
                    }
                });

        newFragment.setCancelable(false);
        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }
}
