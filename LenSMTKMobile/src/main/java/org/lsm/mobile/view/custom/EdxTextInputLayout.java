package org.lsm.mobile.view.custom;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;

import org.lsm.mobile.R;

public class EdxTextInputLayout extends TextInputLayout {
    private final int HINT_TEXT_APPEARANCE_STYLE = R.style.edX_Widget_TextInputLayout_HintTextAppearance;

    public EdxTextInputLayout(Context context) {
        super(context);
        setCustomHintAppearance();
    }

    public EdxTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomHintAppearance();
    }

    public EdxTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setCustomHintAppearance();
    }

    protected void setCustomHintAppearance() {
        setHintTextAppearance(HINT_TEXT_APPEARANCE_STYLE);
    }
}
