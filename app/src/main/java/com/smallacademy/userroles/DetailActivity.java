package com.smallacademy.userroles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productBarcodeTextView;
    private TextView productCategoryTextView;
    private TextView productPriceTextView;
    private TextView productDescriptionTextView;
    private ImageButton editButton;
    private ImageButton deleteButton;

    private ImageView backButton;

    private DatabaseReference productRef;
    private ValueEventListener productValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Initialize views
        productImageView = findViewById(R.id.prd_img_dt);
        productNameTextView = findViewById(R.id.prd_name_dt);
        productBarcodeTextView = findViewById(R.id.prd_barcode_dt);
        productCategoryTextView = findViewById(R.id.prd_cat_dt);
        productPriceTextView = findViewById(R.id.prd_price_dt);
        productDescriptionTextView = findViewById(R.id.prd_desc_dt);
        editButton = findViewById(R.id.btn_edit);
        deleteButton = findViewById(R.id.btn_delete);
        backButton = findViewById(R.id.backButtonDetailAdmin);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to navigate to the AdminPanelActivity
                Intent intent = new Intent(DetailActivity.this, AdminPanel.class);
                startActivity(intent);
            }
        });

        // Retrieve the product data and product key from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("product") && intent.hasExtra("productKey")) {
            ProductModel product = (ProductModel) intent.getSerializableExtra("product");
            String productKey = intent.getStringExtra("productKey");

            // Display the product data in the DetailActivity
            Glide.with(this).load(product.getImageUrl()).into(productImageView);
            productNameTextView.setText(product.getName());
            productBarcodeTextView.setText(product.getBarcode());
            productCategoryTextView.setText(product.getCategory());
            productPriceTextView.setText(String.format("RM %.2f", product.getPrice()));
            productDescriptionTextView.setText(product.getDescription());

            // Set up the DatabaseReference for the specific product
            productRef = FirebaseDatabase.getInstance().getReference().child("products").child(productKey);

            // Set up the ValueEventListener to listen for changes in the product data
            productValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Data in Firebase has changed for the specific product
                    // Update the local ProductModel object and the UI accordingly
                    ProductModel updatedProduct = dataSnapshot.getValue(ProductModel.class);
                    if (updatedProduct != null) {
                        // Update the UI with the new data
                        Glide.with(DetailActivity.this).load(updatedProduct.getImageUrl()).into(productImageView);
                        productNameTextView.setText(updatedProduct.getName());
                        productBarcodeTextView.setText(updatedProduct.getBarcode());
                        productCategoryTextView.setText(updatedProduct.getCategory());
                        productPriceTextView.setText(String.format("RM %.2f", updatedProduct.getPrice()));
                        productDescriptionTextView.setText(updatedProduct.getDescription());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Error occurred while fetching the data
                    Toast.makeText(DetailActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

            // Add the ValueEventListener to the DatabaseReference
            productRef.addValueEventListener(productValueEventListener);

            // Set click listener for the "Edit" button
            editButton.setOnClickListener(v -> showUpdateDialog(product, productKey));

            // Set click listener for the "Delete" button
            deleteButton.setOnClickListener(v -> showDeleteDialog(productKey));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove the ValueEventListener to prevent memory leaks
        if (productRef != null && productValueEventListener != null) {
            productRef.removeEventListener(productValueEventListener);
        }
    }

    // Method to show the update dialog
    private void showUpdateDialog(ProductModel product, String productKey) {
        // Create a dialog to show the update form
        final DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.update_popup))
                .setExpanded(true, 1200)
                .create();

        // Get the dialog view and initialize views inside it
        View dialogView = dialogPlus.getHolderView();
        EditText imageUrlEditText = dialogView.findViewById(R.id.updateImg); // EditText for the Image URL
        EditText productNameEditText = dialogView.findViewById(R.id.updateName);
        EditText productBarcodeEditText = dialogView.findViewById(R.id.updateBarcode);
        Spinner productCategorySpinner = dialogView.findViewById(R.id.updateCat);
        EditText productPriceEditText = dialogView.findViewById(R.id.updatePrice);
        EditText productDescriptionEditText = dialogView.findViewById(R.id.updateDesc);
        Button updateButton = dialogView.findViewById(R.id.btn_update);

        // Pre-fill the fields with existing data
        imageUrlEditText.setText(product.getImageUrl());
        productNameEditText.setText(product.getName());
        productBarcodeEditText.setText(product.getBarcode());
        productPriceEditText.setText(String.valueOf(product.getPrice()));
        productDescriptionEditText.setText(product.getDescription());

        // Initialize the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.predefined_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productCategorySpinner.setAdapter(adapter);

        // Find the index of the product's category in the predefined categories array
        int categoryIndex = adapter.getPosition(product.getCategory());
        if (categoryIndex >= 0) {
            // Set the selected category in the spinner
            productCategorySpinner.setSelection(categoryIndex);
        }

        // Set click listener for the "Update" button
        updateButton.setOnClickListener(v -> {
            // Get updated values from the EditText and Spinner fields
            String updatedImageUrl = imageUrlEditText.getText().toString().trim(); // Fetch the Image URL
            String updatedName = productNameEditText.getText().toString().trim();
            String updatedBarcode = productBarcodeEditText.getText().toString().trim();
            String updatedCategory = productCategorySpinner.getSelectedItem().toString();
            String updatedPriceString = productPriceEditText.getText().toString().trim();
            String updatedDescription = productDescriptionEditText.getText().toString().trim();

            // Perform validation, ensure all fields are filled (you can add more validation if needed)
            if (updatedImageUrl.isEmpty() || updatedName.isEmpty() || updatedBarcode.isEmpty() || updatedPriceString.isEmpty() || updatedDescription.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert the updated price string to a double
            double updatedPrice = Double.parseDouble(updatedPriceString);

            // Create a HashMap to hold the updated values
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("imageUrl", updatedImageUrl); // Update the ImageUrl
            updateMap.put("name", updatedName);
            updateMap.put("barcode", updatedBarcode);
            updateMap.put("category", updatedCategory);
            updateMap.put("price", updatedPrice);
            updateMap.put("description", updatedDescription);

            // Update the product data in the Firebase Realtime Database
            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("products").child(productKey);
            productRef.updateChildren(updateMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Product Updated Successfully.", Toast.LENGTH_SHORT).show();
                        // Close the update dialog after successful update
                        dialogPlus.dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error while updating.", Toast.LENGTH_SHORT).show());
        });

        // Show the dialog
        dialogPlus.show();
    }


    // Method to show the delete confirmation dialog
    // Method to show the delete confirmation dialog
    private void showDeleteDialog(String productKey) {
        // Show a confirmation dialog before deleting the data
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete the product data from the Firebase Realtime Database
                    DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("products").child(productKey);
                    productRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Product Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                // Go back to the MainActivity after successful deletion
                                startActivity(new Intent(this, AdminPanel.class));
                                finish(); // Optional: Close DetailActivity after going back
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error while deleting.", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
