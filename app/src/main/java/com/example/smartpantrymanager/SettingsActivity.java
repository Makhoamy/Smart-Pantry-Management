package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());

        SharedPreferences preferences =
                getSharedPreferences(UnitFormatter.PREFS_NAME, MODE_PRIVATE);

        SwitchCompat alertsSwitch = findViewById(R.id.swAlerts);
        RadioGroup unitsGroup = findViewById(R.id.rgUnits);
        MaterialButton applyButton = findViewById(R.id.btnApplySettings);

        // Show the saved choices.
        alertsSwitch.setChecked(preferences.getBoolean(UnitFormatter.KEY_EXPIRY_ALERTS, true));
        boolean imperial = UnitFormatter.IMPERIAL.equals(
                preferences.getString(UnitFormatter.KEY_UNITS, UnitFormatter.METRIC));
        unitsGroup.check(imperial ? R.id.rbImperial : R.id.rbMetric);

        // Nothing is saved until the button is pressed.
        applyButton.setOnClickListener(v -> {
            boolean useImperial = unitsGroup.getCheckedRadioButtonId() == R.id.rbImperial;
            preferences.edit()
                    .putString(UnitFormatter.KEY_UNITS,
                            useImperial ? UnitFormatter.IMPERIAL : UnitFormatter.METRIC)
                    .putBoolean(UnitFormatter.KEY_EXPIRY_ALERTS, alertsSwitch.isChecked())
                    .apply();
            Toast.makeText(this,
                    (useImperial ? "Imperial" : "Metric") + " units applied",
                    Toast.LENGTH_SHORT).show();
        });

        BottomNav.setup(this, R.id.nav_settings);
    }
}
