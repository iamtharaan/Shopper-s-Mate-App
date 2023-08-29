package com.smallacademy.userroles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductCartAdapter extends RecyclerView.Adapter<ProductCartAdapter.ViewHolder> {
    private final List<ProductCartModel> productList;
    private final OnClearClickListener onClearClickListener;

    public interface OnClearClickListener {
        void onClearClick(int position);
    }

    public ProductCartAdapter(List<ProductCartModel> productList, OnClearClickListener onClearClickListener) {
        this.productList = productList;
        this.onClearClickListener = onClearClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartitemview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductCartModel product = productList.get(position);
        holder.bindData(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public ProductCartModel getItem(int position) {
        return productList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productBarcode;
        private final TextView productCategory;
        private final TextView productPriceTotal;
        private final TextView productQuantity;
        private final ImageView btnClear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.recCardCart);
            productImage = itemView.findViewById(R.id.img_cart);
            productName = itemView.findViewById(R.id.name_cart);
            productBarcode = itemView.findViewById(R.id.barcode_cart);
            productCategory = itemView.findViewById(R.id.prd_cat);
            productPriceTotal = itemView.findViewById(R.id.prd_price_total);
            productQuantity = itemView.findViewById(R.id.prd_quantity);
            btnClear = itemView.findViewById(R.id.btn_clear);

            btnClear.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onClearClickListener.onClearClick(position);
                }
            });
        }

        public void bindData(ProductCartModel product) {
            productName.setText(product.getName());
            productBarcode.setText(product.getBarcode());
            productCategory.setText(product.getCategory());
            productPriceTotal.setText("RM " + String.format("%.2f", product.getPrice()));
            productQuantity.setText("Qty: " + String.valueOf(product.getQuantity())); // Display quantity as "Qty: x"

            // Load product image using Glide library
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .into(productImage);
        }
    }
}
