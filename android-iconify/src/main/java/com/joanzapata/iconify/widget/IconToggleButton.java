package com.joanzapata.iconify.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;
import com.joanzapata.iconify.Iconify;

public class IconToggleButton extends ToggleButton {

    public IconToggleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public IconToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconToggleButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        setTransformationMethod(null);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(Iconify.compute(this, text), BufferType.NORMAL);
    }
}
