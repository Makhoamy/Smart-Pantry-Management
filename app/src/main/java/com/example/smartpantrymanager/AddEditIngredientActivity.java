package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private static final List<String> UNITS = Arrays.asList("g", "kg", "ml", "l", "pcs", "tbsp", "tsp");

    private SmartPantryDatabase db;
    private TextInputLayout tilName, tilQuantity;
    private TextInputEditText etName, etQuantity, etExpiry;
    private Spinner spUnit;
    private int itemId = -1;
    private PantryItem existing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);
        NavigationHelper.applyInsets(this);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = SmartPantryDatabase.getInstance(this);
        tilName = findViewById(R.id.tilName);
        tilQuantity = findViewById(R.id.tilQuantity);
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);
        Button btnSave = findViewById(R.id.btnSave);

        spUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, UNITS));
        etExpiry.setOnClickListener(v -> showDatePicker());

        itemId = getIntent().getIntExtra("item_id", -1);
        if (itemId != -1) {
            getSupportActionBar().setTitle("Edit ingredient");
            existing = db.pantryDao().getById(itemId);
            if (existing != null) {
                etName.setText(existing.name);
                etQuantity.setText(NavigationHelper.formatQuantity(existing.quantity));
                etExpiry.setText(existing.expiryDate);
                spUnit.setSelection(Math.max(0, UNITS.indexOf(existing.unit)));
            }
        } else {
            getSupportActionBar().setTitle("Add ingredient");
        }

        btnSave.setOnClickListener(v -> save());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) ->
                etExpiry.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean validate() {
        boolean ok = true;

        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            tilName.setError("Enter an ingredient name");
            ok = false;
        } else {
            tilName.setError(null);
        }

        double qty = 0;
        try {
            qty = Double.parseDouble(etQuantity.getText().toString().trim());
        } catch (NumberFormatException e) {
            qty = 0;
        }
        if (qty <= 0) {
            tilQuantity.setError("Enter a quantity greater than 0");
            ok = false;
        } else {
            tilQuantity.setError(null);
        }
        return ok;
    }

    private void save() {
        if (!validate()) return;

        PantryItem item = (existing != null) ? existing : new PantryItem();
        item.name = etName.getText().toString().trim();
        item.quantity = Double.parseDouble(etQuantity.getText().toString().trim());
        item.unit = spUnit.getSelectedItem().toString();
        item.expiryDate = etExpiry.getText().toString().trim();

        if (existing != null) {
            db.pantryDao().update(item);
        } else {
            db.pantryDao().insert(item);
        }
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
