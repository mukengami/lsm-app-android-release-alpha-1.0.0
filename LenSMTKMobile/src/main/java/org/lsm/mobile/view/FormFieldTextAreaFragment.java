package org.lsm.mobile.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.lsm.mobile.R;
import org.lsm.mobile.user.FormField;

import org.lsm.mobile.base.BaseFragment;

public class FormFieldTextAreaFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form_field_textarea, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final FormField formField = (FormField) getActivity().getIntent().getSerializableExtra(FormFieldActivity.EXTRA_FIELD);
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(formField.getLabel());

        final TextView textView = view.findViewById(R.id.label_for_text);
        textView.setText(formField.getLabel());

        final EditText editText = view.findViewById(R.id.text);
        editText.setHint(formField.getPlaceholder());
        editText.setText(getActivity().getIntent().getStringExtra(FormFieldActivity.EXTRA_VALUE));
        view.findViewById(R.id.submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setResult(Activity.RESULT_OK,
                        new Intent()
                                .putExtra(FormFieldActivity.EXTRA_FIELD, formField)
                                .putExtra(FormFieldActivity.EXTRA_VALUE, editText.getText().toString()));
                getActivity().finish();
            }
        });
    }
}
