package com.smallacademy.userroles;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<ProductModel> productList;
    private OnItemClickListener itemClickListener;

    private List<ProductModel> originalProductList; // Store the original unfiltered list



    public interface OnItemClickListener {
        void onItemClick(ProductModel product);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public ProductAdapter(List<ProductModel> productList) {
        this.productList = productList;
        this.originalProductList = new ArrayList<>(productList); // Copy the original list
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item view layout (item_view.xml) and create the ViewHolder
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Bind data to the views in the ViewHolder
        ProductModel product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateData(List<ProductModel> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    // Method to reset the data to the original list
    public void resetData() {
        this.productList = new ArrayList<>(originalProductList);
        notifyDataSetChanged();
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView productBarcodeTextView;
        TextView productCategoryTextView;
        TextView productPriceTextView;
        TextView productDescriptionTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.prd_img);
            productNameTextView = itemView.findViewById(R.id.prd_name);
            productBarcodeTextView = itemView.findViewById(R.id.prd_barcode);
            productCategoryTextView = itemView.findViewById(R.id.prd_cat);
            productPriceTextView = itemView.findViewById(R.id.prd_price);
            productDescriptionTextView = itemView.findViewById(R.id.prd_desc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && itemClickListener != null) {
                        itemClickListener.onItemClick(productList.get(position));
                    }
                }
            });
        }

        public void bind(ProductModel product) {
            productNameTextView.setText(product.getName());
            productBarcodeTextView.setText(product.getBarcode());
            productCategoryTextView.setText(product.getCategory());
            productPriceTextView.setText(String.format("RM %.2f", product.getPrice()));
            productDescriptionTextView.setText(product.getDescription());

            // Load the product image using Glide (make sure to add the Glide dependency in your build.gradle)
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .into(productImageView);
        }


    }
}