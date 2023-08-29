package com.smallacademy.userroles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private CardView[] cards = new CardView[8];
    private EditText searchEditText;
    private TextView cartQuantity;
    private Button scanButton;
    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;
    private String[] categories = {
            "Food&Beverages", "Stationary", "Clothes", "Automobiles",
            "Home&Living", "Health", "Books&Magazines", "Mom&Baby"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mainActivity = (MainActivity) requireActivity();

        // Initialize card views and set click listeners for all cards
        int[] cardIds = {
                R.id.card1, R.id.card2, R.id.card3, R.id.card4,
                R.id.card5, R.id.card6, R.id.card7, R.id.card8
        };
        for (int i = 0; i < cards.length; i++) {
            cards[i] = rootView.findViewById(cardIds[i]);
            cards[i].setOnClickListener(this);
        }

        cartQuantity = rootView.findViewById(R.id.textViewCartQuantity);
        ImageView imageViewCart = rootView.findViewById(R.id.imageViewCart);
        imageViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCartView();
            }
        });

        searchEditText = rootView.findViewById(R.id.searchEditText);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        scanButton = rootView.findViewById(R.id.ScanButtonHome);

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to ScannerFragment using the bottom navigation
                mainActivity.bottomNavigationView.setSelectedItemId(R.id.scanner);
            }
        });

        // Retrieve the overall quantity from SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("CartPrefs", MODE_PRIVATE);
        updateCartQuantity();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartQuantity(); // Update the cart quantity every time the fragment resumes
    }

    private void navigateToCartView() {
        Intent intent = new Intent(requireContext(), CartView.class);
        startActivity(intent);
    }

    private void updateCartQuantity() {
        int overallQuantity = sharedPreferences.getInt("overall_quantity", 0);
        cartQuantity.setText(String.valueOf(overallQuantity));
    }

    private void navigateToCategoryActivity(String categoryName) {
        Intent intent = new Intent(requireContext(), CategoryActivity.class);
        intent.putExtra("category_name", categoryName);
        startActivity(intent);
    }

    private void performSearch() {
        String searchTerm = searchEditText.getText().toString().trim();
        if (!searchTerm.isEmpty()) {
            Intent intent = new Intent(requireContext(), CategoryActivity.class);
            intent.putExtra("search_type", "Search Results");
            intent.putExtra("search_value", searchTerm);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < cards.length; i++) {
            if (v == cards[i]) {
                if (i < categories.length) {
                    navigateToCategoryActivity(categories[i]);
                }
                break;
            }
        }
    }
}