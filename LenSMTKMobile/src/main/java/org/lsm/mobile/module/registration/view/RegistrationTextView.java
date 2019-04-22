package org.lsm.mobile.module.registration.view;

import android.text.InputType;
import android.view.View;

import org.lsm.mobile.module.registration.model.RegistrationFormField;

/**
 * Created by rohan on 2/11/15.
 */
class RegistrationTextView extends RegistrationEditTextView {

    public RegistrationTextView(RegistrationFormField field, View view) {
        super(field, view);
        mTextInputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
    }
}
