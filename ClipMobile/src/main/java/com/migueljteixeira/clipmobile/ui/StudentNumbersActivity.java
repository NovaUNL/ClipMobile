package com.migueljteixeira.clipmobile.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class StudentNumbersActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        StudentNumbersFragment fragment = (StudentNumbersFragment) fm.findFragmentById(android.R.id.content);

        if (fragment == null) {
            fragment = new StudentNumbersFragment();
            fm.beginTransaction().add(android.R.id.content, fragment).commit();
        }
    }

}
