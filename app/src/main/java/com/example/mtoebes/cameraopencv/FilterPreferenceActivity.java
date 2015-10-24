package com.example.mtoebes.cameraopencv;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class FilterPreferenceActivity extends PreferenceActivity {

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.filter_preferences);
        }
    }
