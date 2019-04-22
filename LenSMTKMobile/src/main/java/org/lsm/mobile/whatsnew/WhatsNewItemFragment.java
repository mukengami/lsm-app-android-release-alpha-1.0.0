package org.lsm.mobile.whatsnew;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.lsm.mobile.R;
import org.lsm.mobile.base.BaseFragment;
import org.lsm.mobile.databinding.FragmentWhatsNewItemBinding;
import org.lsm.mobile.util.UiUtil;

public class WhatsNewItemFragment extends BaseFragment {
    public static final String ARG_MODEL = "ARG_MODEL";

    private FragmentWhatsNewItemBinding binding;

    public static WhatsNewItemFragment newInstance(@NonNull WhatsNewItemModel model) {
        final WhatsNewItemFragment fragment = new WhatsNewItemFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_MODEL, model);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_whats_new_item, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Bundle args = getArguments();
        final WhatsNewItemModel model = args.getParcelable(ARG_MODEL);

        binding.title.setText(escapePlatformName(model.getTitle()));
        binding.message.setText(escapePlatformName(model.getMessage()));

        @DrawableRes
        final int imageRes = UiUtil.getDrawable(getContext(), model.getImage());
        binding.image.setImageResource(imageRes);
        // We need different scale types for portrait and landscape images
        final Drawable drawable = UiUtil.getDrawable(getContext(), imageRes);
        if (drawable != null) {
            if (drawable.getIntrinsicHeight() > drawable.getIntrinsicWidth()) {
                binding.image.setScaleType(ImageView.ScaleType.FIT_END);
            } else {
                binding.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }
    }

    private String escapePlatformName(@NonNull String input) {
        return input.replaceAll("platform_name", getString(R.string.platform_name));
    }
}
