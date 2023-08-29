package com.smallacademy.userroles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class CatProductAdapter extends RecyclerView.Adapter<CatProductAdapter.ProductViewHolder> {

    private List<CatProductModel> products;
    private OnItemClickListener itemClickListener; // Item click listener interface

    // Constructor to set the item click listener
    public CatProductAdapter(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // Setter method to set the products list
    public void setProducts(List<CatProductModel> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_list_view, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CatProductModel product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImageView;
        private TextView productNameTextView;
        private TextView productBarcodeTextView;
        private TextView productCategoryTextView;
        private TextView productPriceTextView;
        private TextView productDescriptionTextView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.prd_img);
            productNameTextView = itemView.findViewById(R.id.prd_name);
            productBarcodeTextView = itemView.findViewById(R.id.prd_barcode);
            productCategoryTextView = itemView.findViewById(R.id.prd_cat);
            productPriceTextView = itemView.findViewById(R.id.prd_price);
            productDescriptionTextView = itemView.findViewById(R.id.prd_desc);

            // Set click listener for the item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && itemClickListener != null) {
                        itemClickListener.onItemClick(products.get(position));
                    }
                }
            });
        }

        public void bind(CatProductModel product) {
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(productImageView);

            productNameTextView.setText(product.getName());
            productBarcodeTextView.setText(product.getBarcode());
            productCategoryTextView.setText(product.getCategory());

            // Format the price with two decimal places
            String formattedPrice = String.format("%.2f", product.getPrice());
            productPriceTextView.setText("RM" + formattedPrice);
            productDescriptionTextView.setText(product.getDescription());
        }
    }

    // Item click listener interface
    public interface OnItemClickListener {
        void onItemClick(CatProductModel product);
    }
}
