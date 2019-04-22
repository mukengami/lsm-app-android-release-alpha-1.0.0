package org.lsm.mobile.module.registration.view;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import org.lsm.mobile.R;
import org.lsm.mobile.logger.Logger;
import org.lsm.mobile.module.registration.model.RegistrationFormField;
import org.lsm.mobile.module.registration.model.RegistrationOption;

public class RegistrationSelectView implements IRegistrationFieldView {

    protected static final Logger logger = new Logger(RegistrationEditTextView.class);
    private RegistrationFormField mField;
    private View mView;
    private RegistrationOptionSpinner mInputView;
    private TextView mInstructionsView;
    private TextView mErrorView;
    @Nullable
    private OnSpinnerItemSelectedListener onSpinnerItemSelectedListener;

    public RegistrationSelectView(RegistrationFormField field, View view) {
        // create and configure view and save it to an instance variable
        this.mField = field;
        this.mView = view;

        this.mInputView = (RegistrationOptionSpinner) view.findViewById(R.id.input_spinner);
        this.mInstructionsView = (TextView) view.findViewById(R.id.input_spinner_instructions);
        this.mErrorView = (TextView) view.findViewById(R.id.input_spinner_error);

        // set prompt
        mInputView.setPrompt(mField.getLabel());

        // Remove JSON defined default value, which is appropriate for web but not for mobile.
        // e.g. server sends "--" as the default value for a select box, but on mobile we want
        // the default value to be the label of select box like Gender, Country etc.
        for (RegistrationOption option : mField.getOptions()) {
            if (option.isDefaultValue()) {
                mField.getOptions().remove(option);
                break;
            }
        }
        // Create default option using label text
        RegistrationOption defaultOption = new RegistrationOption();
        defaultOption.setName(mField.getLabel());
        defaultOption.setDefaultValue(true);
        mField.getOptions().add(0, defaultOption);

        mInputView.setItems(mField.getOptions(), defaultOption);

        setInstructions(field.getInstructions());

        // hide error text view
        mErrorView.setVisibility(View.GONE);

        // This tag is necessary for End-to-End tests to work properly
        mInputView.setTag(mField.getName());

        // Do a11y adjustment
        mInputView.setContentDescription(String.format("%s. %s.", mInputView.getSelectedItemName(), mField.getInstructions()));
        ViewCompat.setImportantForAccessibility(mInstructionsView, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);

        mInputView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isChangedByUser = false;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isChangedByUser) {
                    isChangedByUser = true;
                    return;
                }
                isValidInput();
                if (onSpinnerItemSelectedListener != null) {
                    onSpinnerItemSelectedListener.onSpinnerItemSelected();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setOnSpinnerItemSelectedListener(@Nullable OnSpinnerItemSelectedListener onSpinnerItemSelectedListener) {
        this.onSpinnerItemSelectedListener = onSpinnerItemSelectedListener;
    }

    public void setOnSpinnerFocusedListener(@Nullable OnSpinnerFocusedListener onSpinnerFocusedListener) {
        this.mInputView.setOnSpinnerFocusedListener(onSpinnerFocusedListener);
    }

    @Override
    public JsonElement getCurrentValue() {
        // turn text view content into a JsonElement and return it
        return new JsonPrimitive(mInputView.getSelectedItemValue());
    }

    public boolean setRawValue(String value){
        if ( mInputView.hasValue( value ) ){
            mInputView.select( value );
            return true;
        }
        return false;
    }

    @Override
    public boolean hasValue() {
        return (mInputView.getSelectedItem() != null
                && !TextUtils.isEmpty(mInputView.getSelectedItemValue()));
    }

    @Override
    public RegistrationFormField getField() {
        return mField;
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public void setInstructions(@Nullable String instructions) {
        if (instructions != null && !instructions.isEmpty()) {
            mInstructionsView.setVisibility(View.VISIBLE);
            mInstructionsView.setText(instructions);
        }
        else {
            mInstructionsView.setVisibility(View.GONE);
        }
    }

    @Override
    public void handleError(String error) {
        if (error != null && !error.isEmpty()) {
            mErrorView.setVisibility(View.VISIBLE);
            mErrorView.setText(error);

            final String errorTag = mInputView.getResources().getString(R.string.label_error);
            mInputView.setContentDescription(String.format("%s. %s. %s, %s.",
                    mInputView.getSelectedItemName(), mField.getInstructions(), errorTag, error));
        }
        else {
            logger.warn("error message not provided, so not informing the user about this error");
        }
    }

    @Override
    public boolean isValidInput() {
        // hide error as we are re-validating the input
        mErrorView.setVisibility(View.GONE);

        mInputView.setContentDescription(String.format("%s. %s.", mInputView.getSelectedItemName(), mField.getInstructions()));

        // check if this is required field and has an input value
        if (mField.isRequired() && !hasValue()) {
            String errorMessage = mField.getErrorMessage().getRequired();
            if(errorMessage==null || errorMessage.isEmpty()){
                errorMessage = getView().getResources().getString(R.string.error_select_field,
                        mField.getLabel());
            }
            handleError(errorMessage);
            return false;
        }

        //For select we should not have length checks as there is no input

        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        mInputView.setEnabled(enabled);
    }

    @Override
    public void setActionListener(IActionListener actionListener) {
        // no actions for this field
    }

    @Override
    public View getOnErrorFocusView() {
        return mInputView;
    }

    public interface OnSpinnerItemSelectedListener {
        /**
         * Callback method to be invoked when an item in the spinner has been selected.
         */
        void onSpinnerItemSelected();
    }

    public interface OnSpinnerFocusedListener {
        /**
         * Callback method to be invoked when the spinner has been focused.
         */
        void onSpinnerFocused();
    }
}
