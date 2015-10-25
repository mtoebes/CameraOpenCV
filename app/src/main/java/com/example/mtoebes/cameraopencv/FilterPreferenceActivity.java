package com.example.mtoebes.cameraopencv;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class FilterPreferenceActivity extends PreferenceActivity {

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.filter_preferences);
        }
    }
