package com.symantec.searchactivity.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.symantec.searchactivity.R;

/**
 * Created by acampos on 9/27/15.
 */
public class EditSettingsDialog extends DialogFragment {
    private EditText mEditText;

    public EditSettingsDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditSettingsDialog newInstance(String title) {
        EditSettingsDialog frag = new EditSettingsDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_settings, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mEditText = (EditText) view.findViewById(R.id.etWebSite);
        Button btnDone =  (Button) view.findViewById(R.id.btnDone);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Host Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        btnDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Log.d(mEditText.getText().toString(), mEditText.getText().toString());

                getDialog().dismiss();
                // Perform action on click
            }
        });

    }
}