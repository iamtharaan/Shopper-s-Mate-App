package com.smallacademy.userroles;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder> {

    private Context context;
    private List<PaymentHistoryModel> paymentHistoryList;

    public PaymentHistoryAdapter(Context context, List<PaymentHistoryModel> paymentHistoryList) {
        this.context = context;
        this.paymentHistoryList = paymentHistoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentHistoryModel payment = paymentHistoryList.get(position);
        holder.bindData(payment);
    }

    @Override
    public int getItemCount() {
        return paymentHistoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textDate;
        private TextView textTime;
        private TextView textAmount;
        private ImageView imageDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_date);
            textTime = itemView.findViewById(R.id.text_time);
            textAmount = itemView.findViewById(R.id.text_amount);
            imageDelete = itemView.findViewById(R.id.image_delete);

            imageDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        PaymentHistoryModel payment = paymentHistoryList.get(position);
                        paymentHistoryList.remove(position);
                        notifyItemRemoved(position);
                        savePaymentHistory();
                        updateEmptyState();
                    }
                }
            });
        }

        public void bindData(PaymentHistoryModel payment) {
            textDate.setText(payment.getDate());
            textTime.setText(payment.getTime());
            textAmount.setText(String.format("RM %.2f", payment.getAmount()));
        }
    }

    private void savePaymentHistory() {
        JSONArray jsonArray = new JSONArray();
        try {
            for (PaymentHistoryModel payment : paymentHistoryList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("date", payment.getDate());
                jsonObject.put("time", payment.getTime());
                jsonObject.put("amount", payment.getAmount());
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("payment_history", jsonArray.toString());
        editor.apply();
    }

    private void updateEmptyState() {
        PaymentHistoryActivity activity = (PaymentHistoryActivity) context;
        activity.showEmptyState(paymentHistoryList.isEmpty());
    }
}