package com.smallacademy.userroles;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private static final String SECRET_KEY = "sk_test_51NTAWKJSwh80Zq7hDSqUWQwnupCgTFpUWVTGGJi2Ph17TdZg9rqUrvycXZ1IKP3dGIVKgJiC1lutBC97tyA7Gq4e00RIgulPKG";
    private static final String PUBLISH_KEY = "pk_test_51NTAWKJSwh80Zq7hHKOk9pXHoOvUY2w1nzkud8cHafHk8A0oR9IZEwPSTeMh2I0dToGVq3V183U3F1sGDnmWRUKY00jX0cVQxy";

    private PaymentSheet paymentSheet;
    private String customerID;
    private String ephemeralKey;
    private String clientSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Base_Theme_HompageDesign);

        PaymentConfiguration.init(this, PUBLISH_KEY);

        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        double totalCartPrice = getIntent().getDoubleExtra("total_cart_price", 0.0);

        // Call createCustomer() to start the payment flow
        createCustomer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) { // The request code used in startActivityForResult()
            if (resultCode == RESULT_OK) {
                // Payment successful, you can show a success message or perform necessary actions
                Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Payment canceled, you can show a canceled message or perform necessary actions
                Toast.makeText(this, "Payment Canceled", Toast.LENGTH_SHORT).show();
            } else {
                // Payment failed, you can show an error message or perform necessary actions
                Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Inside PaymentActivity class

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Payment successful, set the result as RESULT_OK
            setResult(RESULT_OK);

            // Get the total cart price
            double totalCartPrice = getIntent().getDoubleExtra("total_cart_price", 0.0);

            // Save payment history in SharedPreferences
            savePaymentHistory(totalCartPrice);
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            // Payment canceled, set the result as RESULT_CANCELED
            setResult(RESULT_CANCELED);
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            // Payment failed, set the result as RESULT_CANCELED (or you can handle differently based on your use case)
            setResult(RESULT_CANCELED);
        }

        // Finish the PaymentActivity to return to CartView
        finish();
    }

    private void savePaymentHistory(double amount) {
        String currentDateTime = getCurrentDateTime();
        PaymentHistoryModel payment = new PaymentHistoryModel(currentDateTime, "", amount);

        // Get SharedPreferences instance
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the existing payment history as JSON string
        String paymentHistoryJson = preferences.getString("payment_history", "[]");

        // Convert the JSON string to a List of PaymentHistoryModel
        List<PaymentHistoryModel> paymentHistoryList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(paymentHistoryJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String date = jsonObject.getString("date");
                String time = jsonObject.getString("time");
                double paymentAmount = jsonObject.getDouble("amount"); // Rename the variable here
                paymentHistoryList.add(new PaymentHistoryModel(date, time, paymentAmount));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Add the new payment to the list
        paymentHistoryList.add(payment);

        // Convert the updated list back to JSON string
        JSONArray jsonArray = new JSONArray();
        for (PaymentHistoryModel paymentModel : paymentHistoryList) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("date", paymentModel.getDate());
                jsonObject.put("time", paymentModel.getTime());
                jsonObject.put("amount", paymentModel.getAmount());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Save the updated payment history back to SharedPreferences
        preferences.edit().putString("payment_history", jsonArray.toString()).apply();
    }

    private String getCurrentDateTime() {
        // Get the current date and time using Calendar
        Calendar calendar = Calendar.getInstance();
        // Format the date and time using SimpleDateFormat
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }


    private void createCustomer() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            customerID = object.getString("id");
                            // Toast.makeText(PaymentActivity.this, customerID, Toast.LENGTH_SHORT).show(); // Remove or comment out this line
                            // Call getEphemeralKey after creating the customer
                            getEphemeralKey(customerID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error here if needed.
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(PaymentActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getEphemeralKey(String customerID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ephemeralKey = object.getString("id");
                            // Toast.makeText(PaymentActivity.this, ephemeralKey, Toast.LENGTH_SHORT).show(); // Remove or comment out this line
                            // Call getClientSecret with the retrieved customerID and ephemeralKey
                            getClientSecret(customerID, ephemeralKey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error here if needed.
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                header.put("Stripe-Version", "2022-11-15");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(PaymentActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getClientSecret(String customerID, String ephemeralKey) {
        double totalCartPrice = getIntent().getDoubleExtra("total_cart_price", 0.0);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            clientSecret = object.getString("client_secret");
                            // Toast.makeText(PaymentActivity.this, clientSecret, Toast.LENGTH_SHORT).show(); // Remove or comment out this line
                            // Call PaymentFlow with the retrieved clientSecret
                            PaymentFlow(clientSecret);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer " + SECRET_KEY);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                // Set the amount dynamically based on totalCartPrice
                int amountInCents = (int) (totalCartPrice * 100); // Convert to cents (Stripe requires the amount in the smallest currency unit)
                params.put("amount", String.valueOf(amountInCents));
                params.put("currency", "myr");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(PaymentActivity.this);
        requestQueue.add(stringRequest);
    }

// ... (Other parts of the code remain unchanged)


    private void PaymentFlow(String clientSecret) {
        if (customerID == null || ephemeralKey == null || clientSecret == null) {
            // If customerID, ephemeralKey, or clientSecret is null, handle the error here.
            Toast.makeText(this, "Error: Customer ID, ephemeral key, or client secret is null.", Toast.LENGTH_SHORT).show();
            return;
        }

        // All required parameters are available, proceed with PaymentSheet.
        paymentSheet.presentWithPaymentIntent(
                clientSecret, new PaymentSheet.Configuration("Shopper's Mate",
                        new PaymentSheet.CustomerConfiguration(customerID, ephemeralKey))
        );
    }

}
