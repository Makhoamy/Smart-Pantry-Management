package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        NavigationHelper.applyInsets(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        SharedPreferences prefs = Prefs.get(this);
        SwitchMaterial swAlerts = findViewById(R.id.swAlerts);
        RadioButton rbMetric = findViewById(R.id.rbMetric);
        RadioButton rbImperial = findViewById(R.id.rbImperial);

        // Load saved values first, then attach listeners
        swAlerts.setChecked(Prefs.alertsEnabled(this));
        boolean metric = Prefs.isMetric(this);
        rbMetric.setChecked(metric);
        rbImperial.setChecked(!metric);

        swAlerts.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean(Prefs.KEY_ALERTS, checked).apply());
        rbMetric.setOnClickListener(v ->
                prefs.edit().putString(Prefs.KEY_UNITS, Prefs.UNITS_METRIC).apply());
        rbImperial.setOnClickListener(v ->
                prefs.edit().putString(Prefs.KEY_UNITS, Prefs.UNITS_IMPERIAL).apply());

        NavigationHelper.setupBottomNav(this, R.id.nav_settings);
    }
}