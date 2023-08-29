package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminPanel extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<ProductModel> productList;
    private FloatingActionButton fabAddProduct;
    private EditText searchEditText;
    private TextView adminNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        recyclerView = findViewById(R.id.rv);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(productAdapter);

        adminNameText = findViewById(R.id.AdminNameTextMain);
        ImageView PaymentHistoryIcon = findViewById(R.id.LogoutImageIcon);

        // Find the AdminIcon ImageView
        ImageView adminIconImageView = findViewById(R.id.AdminIcon);

        // Set an OnClickListener on the AdminIcon
        adminIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the AdminProfileActivity when the AdminIcon is clicked
                Intent intent = new Intent(AdminPanel.this, AdminProfile.class);
                startActivity(intent);
            }
        });

        searchEditText = findViewById(R.id.SearchByProductName);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = charSequence.toString().toLowerCase();
                List<ProductModel> filteredList = new ArrayList<>();

                if (searchText.isEmpty()) {
                    filteredList.addAll(productList);
                } else {
                    for (ProductModel product : productList) {
                        if (product.getName().toLowerCase().contains(searchText) || product.getCategory().toLowerCase().contains(searchText)) {
                            filteredList.add(product);
                        }
                    }
                }

                productAdapter.updateData(filteredList);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Initialize Firebase and get reference to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference productsRef = database.getReference("products");

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String productKey = snapshot.getKey();
                    ProductModel product = snapshot.getValue(ProductModel.class);
                    product.setKey(productKey);
                    productList.add(product);
                }

                productAdapter.notifyDataSetChanged();

                Log.d("MainActivity", "Number of products: " + productList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AdminPanel.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        fabAddProduct = findViewById(R.id.floatingActionButton);
        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanel.this, AdminAddActivity.class);
                startActivity(intent);
            }
        });

        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ProductModel product) {
                Intent intent = new Intent(AdminPanel.this, DetailActivity.class);
                intent.putExtra("product", product);
                intent.putExtra("productKey", product.getKey());
                startActivity(intent);
            }
        });

        // Fetch admin's name and display it in the AdminNameTextMain TextView
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String fullName = dataSnapshot.child("FullName").getValue(String.class);
                        adminNameText.setText(fullName);
                    } else {
                        Toast.makeText(AdminPanel.this, "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(AdminPanel.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Set click listener for the payment history icon
        PaymentHistoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the PaymentHistoryActivity when the PaymentHistory is clicked
                Intent intent = new Intent(AdminPanel.this, PaymentHistoryActivity.class);
                startActivity(intent);

            }
        });
    }
}