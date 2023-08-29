package com.smallacademy.userroles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartView extends AppCompatActivity implements ProductCartAdapter.OnClearClickListener {
    private ProductDatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private ProductCartAdapter cartAdapter;
    private List<ProductCartModel> productList;
    private TextView totalCartPriceTextView;
    private double totalCartPrice = 0.0;
    private Button proceedPaymentButton;

    private int overallQuantity = 0;

    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view);

        databaseHelper = new ProductDatabaseHelper(this);

        recyclerView = findViewById(R.id.rvCartView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = getProductsFromDatabase();
        cartAdapter = new ProductCartAdapter(productList, this);
        recyclerView.setAdapter(cartAdapter);

        totalCartPriceTextView = findViewById(R.id.TotalCartPrice);
        updateTotalCartPrice();

        backButton = findViewById(R.id.CartBackButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call finish to go back to the previous activity
                finish();
            }
        });

        proceedPaymentButton = findViewById(R.id.ProceedPayment_Btn);
        proceedPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the overall quantity is greater than 0
                if (overallQuantity > 0) {
                    proceedToPayment();
                } else {
                    // If the overall quantity is empty, show a toast message
                    Toast.makeText(CartView.this, "Add items to the cart before proceeding to payment.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void proceedToPayment() {
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra("total_cart_price", totalCartPrice); // Pass the total cart price
        startActivityForResult(intent, 123); // Use a unique request code (e.g., 123)
    }

    private List<ProductCartModel> getProductsFromDatabase() {
        List<ProductCartModel> productList = new ArrayList<>();
        Map<String, ProductCartModel> productMap = new HashMap<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ProductDatabaseHelper.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_NAME));
                double price = cursor.getDouble(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_PRICE));
                String barcode = cursor.getString(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_BARCODE));
                String desc = cursor.getString(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_DESC));
                String category = cursor.getString(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_CATEGORY));
                String image = cursor.getString(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_IMAGE));
                int quantity = cursor.getInt(cursor.getColumnIndex(ProductDatabaseHelper.COLUMN_QUANTITY));

                if (productMap.containsKey(barcode)) {
                    ProductCartModel existingProduct = productMap.get(barcode);
                    int existingQuantity = existingProduct.getQuantity();
                    double existingTotalPrice = existingProduct.getPrice();
                    double totalPrice = existingTotalPrice + (price * quantity); // Calculate the new total price
                    existingProduct.setQuantity(existingQuantity + quantity);
                    existingProduct.setPrice(totalPrice);
                } else {
                    double totalPrice = price * quantity; // Calculate the total price for the new product
                    ProductCartModel product = new ProductCartModel(name, totalPrice, barcode, desc, category, image, quantity);
                    productMap.put(barcode, product);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        productList.addAll(productMap.values());

        return productList;
    }

    private int calculateOverallQuantity() {
        int overallQuantity = 0;
        for (ProductCartModel product : productList) {
            if (product.getQuantity() > 0) {
                overallQuantity++;
            }
        }
        return overallQuantity;
    }

    private void updateTotalCartPrice() {
        totalCartPrice = 0.0;
        int totalQuantity = 0; // Initialize the total quantity

        for (ProductCartModel product : productList) {
            totalCartPrice += product.getPrice();
            totalQuantity += product.getQuantity(); // Calculate the total quantity
        }

        totalCartPriceTextView.setText("RM " + String.format("%.2f", totalCartPrice));

        // Save the total quantity in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("total_quantity", totalQuantity);
        editor.apply();

        // Calculate the overall quantity
        overallQuantity = calculateOverallQuantity();

        // Save the overall quantity in SharedPreferences
        editor.putInt("overall_quantity", overallQuantity);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) { // The request code used in startActivityForResult()
            if (resultCode == RESULT_OK) {
                // Payment successful, clear the items in the RecyclerView
                clearCartItems();
                showThankYouDialog();
            } else if (resultCode == RESULT_CANCELED) {
                // Payment canceled, you can show a canceled message or perform necessary actions
                Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show();
            } else {
                // Payment failed, you can show an error message or perform necessary actions
                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showThankYouDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_thank_you, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialog.show();
    }



    private void clearCartItems() {
        // Clear the items from the database
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(ProductDatabaseHelper.TABLE_NAME, null, null);
        db.close();

        // Clear the items from the list and update the RecyclerView
        productList.clear();
        cartAdapter.notifyDataSetChanged();

        // Update the total cart price (since the cart is now empty)
        updateTotalCartPrice();
    }

    @Override
    public void onClearClick(int position) {
        ProductCartModel product = productList.get(position);

        // Remove the item from the database
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(ProductDatabaseHelper.TABLE_NAME, ProductDatabaseHelper.COLUMN_BARCODE + "=?", new String[]{product.getBarcode()});
        db.close();

        // Remove the item from the list
        productList.remove(position);

        // Update the total cart price
        updateTotalCartPrice();

        // Notify the adapter about the item removal
        cartAdapter.notifyItemRemoved(position);
    }
}
