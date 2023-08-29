package com.smallacademy.userroles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductScanAdapter {
    private Context context;
    private List<ProductScanModel> productList;
    private View.OnClickListener addToCartClickListener;

    private ProductScanAdapter productScanAdapter;

    public ProductScanAdapter(Context context, List<ProductScanModel> productList, View.OnClickListener addToCartClickListener) {
        this.context = context;
        this.productList = productList;
        this.addToCartClickListener = addToCartClickListener;
    }

    public void bindData(LinearLayout linearLayout) {
        LayoutInflater inflater = LayoutInflater.from(context);
        linearLayout.removeAllViews();

        for (ProductScanModel product : productList) {
            View itemView = inflater.inflate(R.layout.activity_scan_item, linearLayout, false);
            ImageView imgProduct = itemView.findViewById(R.id.prd_img_scan);
            TextView txtBarcode = itemView.findViewById(R.id.prd_barcode_scan);
            TextView txtName = itemView.findViewById(R.id.prd_name_scan);
            TextView txtCategory = itemView.findViewById(R.id.prd_cat_scan);
            TextView txtDescription = itemView.findViewById(R.id.prd_desc_dt);
            TextView txtPrice = itemView.findViewById(R.id.prd_price_scan);
            Button btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
            CardView btnMinus = itemView.findViewById(R.id.btn_minus);
            CardView btnPlus = itemView.findViewById(R.id.btn_plus);
            TextView tvQuantity = itemView.findViewById(R.id.tv_quantity);

            txtBarcode.setText(product.getBarcode());
            txtName.setText(product.getName());
            txtCategory.setText(product.getCategory());
            txtDescription.setText(product.getDescription());
            txtPrice.setText(String.valueOf(product.getPrice())); // Convert price to string

            // Load product image using Glide or any other image loading library
            Glide.with(context)
                    .load(product.getImageUrl())
                    .into(imgProduct);

            btnAddToCart.setOnClickListener(addToCartClickListener);

            linearLayout.addView(itemView);
        }
    }
}
