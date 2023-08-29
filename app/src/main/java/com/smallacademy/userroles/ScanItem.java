package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

public class ScanItem extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout linearLayout;
    private Button btnCart;
    private CardView btnMinus;
    private CardView btnPlus;
    private TextView tvQuantity;
    private int quantity = 1; // Default quantity is set to 1
    private ProductScanModel selectedProduct;

    private ImageView ExitButton;
    private ImageView CartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_item);

        setTheme(R.style.Base_Theme_HompageDesign);

        linearLayout = findViewById(R.id.linearLayout);
        btnCart = findViewById(R.id.btn_add_to_cart);
        btnMinus = findViewById(R.id.btn_minus);
        btnPlus = findViewById(R.id.btn_plus);
        tvQuantity = findViewById(R.id.tv_quantity);

        btnCart.setOnClickListener(this);
        btnMinus.setOnClickListener(this);
        btnPlus.setOnClickListener(this);

        ExitButton = findViewById(R.id.ScanExitButton);
        ExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHomeFragment();
            }
        });

        CartButton = findViewById(R.id.scan_cart_button);
        CartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCartView();
            }
        });

        tvQuantity.setText(String.valueOf(quantity)); // Set the default quantity value

        // Retrieve the selected product details from the intent
        selectedProduct = getIntent().getParcelableExtra("product");

        if (selectedProduct != null) {
            displayProductDetails();
        }
    }


    private void displayProductDetails() {
        ImageView imgProduct = findViewById(R.id.prd_img_scan);
        TextView txtBarcode = findViewById(R.id.prd_barcode_scan);
        TextView txtName = findViewById(R.id.prd_name_scan);
        TextView txtCategory = findViewById(R.id.prd_cat_scan);
        TextView txtDescription = findViewById(R.id.prd_desc_dt);
        TextView txtPrice = findViewById(R.id.prd_price_scan);

        txtBarcode.setText(selectedProduct.getBarcode());
        txtName.setText(selectedProduct.getName());
        txtCategory.setText(selectedProduct.getCategory());
        txtDescription.setText(selectedProduct.getDescription());

        // Format the price with two decimal places
        String formattedPrice = String.format("%.2f", selectedProduct.getPrice());
        txtPrice.setText("RM" + formattedPrice);

        // Load product image using Glide or any other image loading library
        Glide.with(this)
                .load(selectedProduct.getImageUrl())
                .into(imgProduct);
    }




    @Override
    public void onClick(View view) {
        int id = view.getId(); // Get the ID of the clicked view

        if (id == R.id.btn_add_to_cart) {
            // Handle "Add to Cart" button click
            addToCart();
        } else if (id == R.id.btn_minus) {
            // Handle "-" button click
            decreaseQuantity();
        } else if (id == R.id.btn_plus) {
            // Handle "+" button click
            increaseQuantity();
        }
    }

    private void addToCart() {
        // Insert the product details into the database
        ProductDatabaseHelper databaseHelper = new ProductDatabaseHelper(this);

        // Get the values to be inserted
        String name = selectedProduct.getName();
        double price = selectedProduct.getPrice();
        String barcode = selectedProduct.getBarcode();
        String desc = selectedProduct.getDescription();
        String category = selectedProduct.getCategory();
        String imageUrl = selectedProduct.getImageUrl();

        long rowId = databaseHelper.insertProduct(name, price, barcode, desc, category, imageUrl, quantity);

        if (rowId != -1) {
            // Product details inserted successfully

            // Navigate to CartView activity
            Intent intent = new Intent(ScanItem.this, CartView.class);
            startActivity(intent);
        } else {
            // Failed to insert product details
        }
    }

    private void decreaseQuantity() {
        if (quantity > 1) {
            quantity--;
            tvQuantity.setText(String.valueOf(quantity));
        }
    }

    private void increaseQuantity() {
        quantity++;
        tvQuantity.setText(String.valueOf(quantity));
    }

    private void navigateToHomeFragment() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish the current activity to avoid coming back to it.
    }

    private void navigateToCartView() {
        Intent intent = new Intent(ScanItem.this, CartView.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Navigate to HomeFragment
        navigateToHomeFragment();
    }
}
