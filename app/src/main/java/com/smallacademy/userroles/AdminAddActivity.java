package com.smallacademy.userroles;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class AdminAddActivity extends AppCompatActivity {
    private EditText addBarcode;
    private EditText addName;
    private EditText addPrice;
    private EditText addImg;
    private EditText addDesc;
    private Button btnSave;
    private Button btnBack;

    private ImageView iconScan;

    private ScanOptions options;

    private Spinner categorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add);

        // Initialize views
        addBarcode = findViewById(R.id.addBarcode);
        addName = findViewById(R.id.addName);
        addPrice = findViewById(R.id.addPrice);
        addImg = findViewById(R.id.addImg);
        addDesc = findViewById(R.id.addDesc);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        iconScan = findViewById(R.id.ScanBarcodeIcon);
        categorySpinner = findViewById(R.id.addCat);

        // Initialize scanning options
        options = new ScanOptions();

        // Set click listener on the "SCAN" icon
        iconScan.setOnClickListener(view -> scanCode());

        // Create an array with the predefined categories and add "Select Category" as the first item
        String[] predefinedCategories = getResources().getStringArray(R.array.predefined_categories);
        String[] categoriesWithDefault = new String[predefinedCategories.length + 1];
        categoriesWithDefault[0] = "Select Category";
        System.arraycopy(predefinedCategories, 0, categoriesWithDefault, 1, predefinedCategories.length);

        // Set up the ArrayAdapter for the Spinner with the categories
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoriesWithDefault);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        // Set click listener on the "SAVE" button
        btnSave.setOnClickListener(view -> saveProductData());

        // Set click listener on the "BACK" button
        btnBack.setOnClickListener(view -> finish());
    }

    // Method to initiate barcode scanning
    private void scanCode() {
        // Customize the scanning options if needed
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureActAdmin.class);

        // Launch the barcode scanning activity
        barLauncher.launch(options);
    }

    // ActivityResultLauncher to handle the barcode scanning result
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    // Set the scanned barcode to the EditText addBarcode
                    addBarcode.setText(result.getContents());
                }
            });

    // Method to save data to the Realtime Database
    private void saveProductData() {
        // Get input values from EditText fields
        String barcode = addBarcode.getText().toString().trim();
        String name = addName.getText().toString().trim();

        // Get the selected category from the Spinner
        String category = categorySpinner.getSelectedItem().toString();
        if (category.equals("Select a Category")) {
            // Show an error if "Select Category" is selected
            Toast.makeText(this, "Please select a category.", Toast.LENGTH_SHORT).show();
            return;
        }

        String priceText = addPrice.getText().toString().trim();
        String imageUrl = addImg.getText().toString().trim();
        String description = addDesc.getText().toString().trim();

        // Check if all required fields are filled before saving
        if (barcode.isEmpty() || name.isEmpty() || priceText.isEmpty() || imageUrl.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceText);

        // Get a reference to the Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("products");

        // Create a new ProductModel object with the entered data
        ProductModel product = new ProductModel(name, barcode, category, price, description, imageUrl);

        // Generate a unique key for the new product
        String productId = databaseReference.push().getKey();

        // Set the value in the database and add a completion listener to check if the data was saved successfully
        databaseReference.child(productId).setValue(product, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    // Data saved successfully
                    Toast.makeText(AdminAddActivity.this, "Product data saved successfully!", Toast.LENGTH_SHORT).show();
                    // Clear input fields to allow entering a new item
                    addBarcode.setText("");
                    addName.setText("");
                    addPrice.setText("");
                    addImg.setText("");
                    addDesc.setText("");
                    // Optionally go back to the previous activity after saving
                    finish();
                } else {
                    // Error occurred while saving data
                    Toast.makeText(AdminAddActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

