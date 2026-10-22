package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etQuantity;
    private EditText etExpiry;
    private Spinner spUnit;
    private Button btnSave;

    private SmartPantryDatabase db;
    private PantryItem pantryItem;
    private int itemId = -1;

    private String[] units = {
            "pcs", "g", "kg", "ml", "L", "cups", "tbsp", "tsp"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_ingredient);
        NavigationHelper.applyInsets(this);

        // Attach the toolbar so it shows a title and a back arrow.
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = SmartPantryDatabase.getInstance(this);

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);
        btnSave = findViewById(R.id.btnSave);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spUnit.setAdapter(unitAdapter);

        etExpiry.setOnClickListener(v -> showDatePicker());

        itemId = getIntent().getIntExtra("item_id", -1);

        if (itemId != -1) {
            getSupportActionBar().setTitle("Edit ingredient");
            loadIngredient();
        } else {
            getSupportActionBar().setTitle("Add ingredient");
        }

        btnSave.setOnClickListener(v -> saveIngredient());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void showDatePicker() {
        // Start on the date already chosen, otherwise on today.
        LocalDate start = LocalDate.now();

        try {
            String current = etExpiry.getText().toString().trim();
            if (!current.isEmpty()) {
                start = LocalDate.parse(current);
            }
        } catch (DateTimeParseException e) {
            // Keep today's date.
        }

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    // Locale.ROOT keeps ASCII digits so the date can always
                    // be parsed again (yyyy-MM-dd).
                    String date = String.format(
                            Locale.ROOT,
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            dayOfMonth
                    );

                    etExpiry.setText(date);
                },
                start.getYear(),
                start.getMonthValue() - 1,
                start.getDayOfMonth()
        );

        // The expiry date is optional, so allow it to be removed again.
        datePicker.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear",
                (dialog, which) -> etExpiry.setText(""));

        datePicker.show();
    }

    private void loadIngredient() {
        pantryItem = db.pantryDao().getById(itemId);

        if (pantryItem == null) {
            Toast.makeText(this, "Ingredient not found",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etName.setText(pantryItem.name);
        etQuantity.setText(plainNumber(pantryItem.quantity));

        etExpiry.setText(
                pantryItem.expiryDate == null ? "" : pantryItem.expiryDate
        );

        for (int i = 0; i < units.length; i++) {
            if (units[i].equalsIgnoreCase(pantryItem.unit)) {
                spUnit.setSelection(i);
                break;
            }
        }
    }

    // 2.0 -> "2", 0.5 -> "0.5" (always uses a dot so it can be parsed back).
    private static String plainNumber(double value) {
        if (value == Math.rint(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }

    private void saveIngredient() {
        String name = etName.getText().toString().trim();
        // Accept a decimal comma as well as a decimal point.
        String quantityText = etQuantity.getText().toString().trim()
                .replace(',', '.');
        String unit = spUnit.getSelectedItem().toString();
        String expiryDate = etExpiry.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Enter an ingredient name");
            etName.requestFocus();
            return;
        }

        // A name made only of digits or symbols can never match a recipe.
        if (!name.matches(".*\\p{L}.*")) {
            etName.setError("Enter a valid ingredient name");
            etName.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            etQuantity.setError("Enter a quantity");
            etQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid quantity");
            etQuantity.requestFocus();
            return;
        }

        if (Double.isNaN(quantity) || Double.isInfinite(quantity)) {
            etQuantity.setError("Enter a valid quantity");
            etQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            etQuantity.setError("Quantity must be greater than zero");
            etQuantity.requestFocus();
            return;
        }

        if (pantryItem == null) {
            pantryItem = new PantryItem();
        }

        pantryItem.name = name;
        pantryItem.quantity = quantity;
        pantryItem.unit = unit;
        pantryItem.expiryDate = expiryDate.isEmpty() ? null : expiryDate;

        if (itemId == -1) {
            db.pantryDao().insert(pantryItem);

            Toast.makeText(this, "Ingredient added",
                    Toast.LENGTH_SHORT).show();
        } else {
            db.pantryDao().update(pantryItem);

            Toast.makeText(this, "Ingredient updated",
                    Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
