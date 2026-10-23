package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {
    private TextInputEditText nameInput, quantityInput, unitInput, expiryInput;
    private PantryDatabase database;
    private int itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        database = PantryDatabase.getInstance(this);
        nameInput = findViewById(R.id.ingredientNameInput);
        quantityInput = findViewById(R.id.ingredientQuantityInput);
        unitInput = findViewById(R.id.ingredientUnitInput);
        expiryInput = findViewById(R.id.ingredientExpiryInput);
        MaterialButton saveButton = findViewById(R.id.saveIngredientButton);
        MaterialButton cancelButton = findViewById(R.id.cancelIngredientButton);

        itemId = getIntent().getIntExtra("item_id", -1);
        if (itemId != -1) loadItem();

        saveButton.setOnClickListener(v -> saveIngredient());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void loadItem() {
        AppExecutors.DATABASE_EXECUTOR.execute(() -> {
            PantryItem item = database.pantryItemDao().getItemById(itemId);
            runOnUiThread(() -> {
                if (item != null) {
                    ((android.widget.TextView) findViewById(R.id.ingredientFormTitle)).setText("Edit ingredient");
                    nameInput.setText(item.getName());
                    quantityInput.setText(String.valueOf(item.getQuantity()));
                    unitInput.setText(item.getUnit());
                    expiryInput.setText(item.getExpiryDate());
                }
            });
        });
    }

    private void saveIngredient() {
        String name = text(nameInput);
        String quantityText = text(quantityInput);
        String unit = text(unitInput).toLowerCase(Locale.ROOT);
        String expiry = text(expiryInput);

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityText) || TextUtils.isEmpty(unit)) {
            Toast.makeText(this, "Enter an ingredient name, quantity and unit.", Toast.LENGTH_LONG).show();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
            if (quantity <= 0 || Double.isNaN(quantity) || Double.isInfinite(quantity)) {
                Toast.makeText(this, "Quantity must be greater than zero.", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid quantity.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!expiry.isEmpty() && !isValidDate(expiry)) {
            Toast.makeText(this, "Use the date format YYYY-MM-DD.", Toast.LENGTH_LONG).show();
            return;
        }

        PantryItem item = new PantryItem(name, quantity, unit, expiry);
        if (itemId != -1) item.setId(itemId);

        AppExecutors.DATABASE_EXECUTOR.execute(() -> {
            if (itemId == -1) database.pantryItemDao().insert(item);
            else database.pantryItemDao().update(item);

            runOnUiThread(() -> {
                Toast.makeText(this, "Ingredient saved", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private String text(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }

    private boolean isValidDate(String value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        format.setLenient(false);
        try {
            Date date = format.parse(value);
            return date != null && format.format(date).equals(value);
        } catch (ParseException e) {
            return false;
        }
    }
}
