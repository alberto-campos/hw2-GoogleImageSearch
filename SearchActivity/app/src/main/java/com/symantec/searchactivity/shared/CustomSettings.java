package com.symantec.searchactivity.shared;


import android.app.Application;

public class CustomSettings extends Application {

    private String filters = "";

    public String getFilters() {

        return filters;
    }

    public void setFilters(String aFilter) {
        filters = filters + aFilter;
    }

    public void clearFilter() {
        filters = "";
    }
}
