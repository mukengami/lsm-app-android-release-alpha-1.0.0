package org.lsm.mobile.module.registration.view;

import android.text.InputType;
import android.view.Gravity;
import android.view.View;

import org.lsm.mobile.module.registration.model.RegistrationFormField;

class RegistrationTextAreaView extends RegistrationEditTextView {

    // Number of lines for TextArea
    private static final int INIT_LINES = 1;
    private static final int MAX_LINES = 7;

    public RegistrationTextAreaView(RegistrationFormField field, View view) {
        super(field, view);

        mTextInputEditText.setLines(INIT_LINES);
        mTextInputEditText.setMaxLines(MAX_LINES);

        // allow multiline text
        mTextInputEditText.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        // text should start from the left-top
        mTextInputEditText.setGravity(Gravity.START | Gravity.TOP);
    }
}
