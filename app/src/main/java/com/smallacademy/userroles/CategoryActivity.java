package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CatProductAdapter adapter;
    private TextView categoryNameTextView;
    private List<CatProductModel> allProducts;
    private List<CatProductModel> filteredProducts;

    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryNameTextView = findViewById(R.id.cardViewCatName);
        backButton = findViewById(R.id.backButton);

        recyclerView = findViewById(R.id.rvCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Pass the item click listener to the CatProductAdapter constructor
        adapter = new CatProductAdapter(new CatProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CatProductModel product) {
                openDetailProductView(product);
            }
        });

        recyclerView.setAdapter(adapter);

        // Retrieve the search type and value from the intent
        String searchType = getIntent().getStringExtra("search_type");
        String searchValue = getIntent().getStringExtra("search_value");

        if (searchType != null && searchType.equals("Search Results")) {
            categoryNameTextView.setText(searchType + ": " + searchValue);
            queryProductsByName(searchValue);
        } else {
            // Retrieve the category name from the intent
            String categoryName = getIntent().getStringExtra("category_name");
            categoryNameTextView.setText(categoryName);
            // Query the Firebase database for products in the selected category
            queryProductsByCategory(categoryName);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Call onBackPressed when the back icon is clicked
            }
        });
    }

    private void queryProductsByCategory(String categoryName) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        Query query = productsRef.orderByChild("category").equalTo(categoryName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allProducts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CatProductModel product = snapshot.getValue(CatProductModel.class);
                    allProducts.add(product);
                }
                adapter.setProducts(allProducts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void queryProductsByName(String productName) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        Query query = productsRef.orderByChild("name").startAt(productName).endAt(productName + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allProducts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CatProductModel product = snapshot.getValue(CatProductModel.class);
                    allProducts.add(product);
                }

                if (allProducts.isEmpty()) {
                    // No products found with the given name
                    // Perform your desired action (e.g., show a message)
                } else {
                    adapter.setProducts(allProducts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish(); // Finish the current activity when the back button is pressed
    }

    private void openDetailProductView(CatProductModel product) {
        Intent intent = new Intent(this, DetailProductVIew.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }
}
