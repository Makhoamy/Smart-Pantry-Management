
package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


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
            getSupportActionBar().setTitle("Settings");
        }

        SharedPreferences prefs = Prefs.get(this);

        CompoundButton swAlerts = findViewById(R.id.swAlerts);
        RadioButton rbMetric = findViewById(R.id.rbMetric);
        RadioButton rbImperial = findViewById(R.id.rbImperial);

        // Load the saved settings.
        swAlerts.setChecked(Prefs.alertsEnabled(this));

        if (Prefs.isMetric(this)) {
            rbMetric.setChecked(true);
        } else {
            rbImperial.setChecked(true);
        }

        // Save the expiry alert preference.
        swAlerts.setOnCheckedChangeListener((button, checked) -> {
            prefs.edit()
                    .putBoolean(Prefs.KEY_ALERTS, checked)
                    .apply();

            String message = checked
                    ? "Expiry alerts enabled"
                    : "Expiry alerts disabled";

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        // Save the selected measurement unit preference.
        rbMetric.setOnClickListener(v -> {
            prefs.edit()
                    .putString(Prefs.KEY_UNITS, Prefs.UNITS_METRIC)
                    .apply();

            Toast.makeText(this, "Metric units selected",
                    Toast.LENGTH_SHORT).show();
        });

        rbImperial.setOnClickListener(v -> {
            prefs.edit()
                    .putString(Prefs.KEY_UNITS, Prefs.UNITS_IMPERIAL)
                    .apply();

            Toast.makeText(this, "Imperial units selected",
                    Toast.LENGTH_SHORT).show();
        });

        NavigationHelper.setupBottomNav(this, R.id.nav_settings);
    }
}
