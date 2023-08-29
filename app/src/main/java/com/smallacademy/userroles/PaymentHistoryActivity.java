package com.smallacademy.userroles;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PaymentHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textPaymentHistory;
    private PaymentHistoryAdapter paymentHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_history_activity);

        recyclerView = findViewById(R.id.recycler_view_payment_history);
        textPaymentHistory = findViewById(R.id.text_payment_history);
        ImageView backButton = findViewById(R.id.image_back_home);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Load and display payment history
        loadPaymentHistory();
    }

    private void loadPaymentHistory() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String paymentHistoryJson = preferences.getString("payment_history", "[]");

        List<PaymentHistoryModel> paymentHistoryList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(paymentHistoryJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String date = jsonObject.getString("date");
                String time = jsonObject.getString("time");
                double amount = jsonObject.getDouble("amount");
                paymentHistoryList.add(new PaymentHistoryModel(date, time, amount));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        paymentHistoryAdapter = new PaymentHistoryAdapter(this, paymentHistoryList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(paymentHistoryAdapter);

        showEmptyState(paymentHistoryList.isEmpty());
    }

    // Method to show/hide message if payment history is empty
    public void showEmptyState(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            textPaymentHistory.setText("No payment history available.");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textPaymentHistory.setText("Payment History");
        }
    }
}