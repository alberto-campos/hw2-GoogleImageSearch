package com.symantec.searchactivity.fragments;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.symantec.searchactivity.R;

public class FragmentDialogSettings extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        showAlertDialog();
    }

    private void showAlertDialog() {
       // FragmentManager fm = getSupportFragmentManager();
        EditSettingsDialog alertDialog = EditSettingsDialog.newInstance("Search filter");
       // alertDialog.show(fm, "fragment_edit_settings");
        alertDialog.show(getFragmentManager(), "fragment_edit_settings");
    }

}
