package com.example.a2.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.a2.R;
import com.example.a2.models.product;
import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {
    private List<product> productList;
    private OnFavouriteClickListener favListener;
    private OnCartClickListener cartListener;

    public interface OnFavouriteClickListener {
        void onFavouriteClick(boolean isAdded);
    }

    public interface OnCartClickListener {
        void onCartClick();
    }

    public RecommendedAdapter(List<product> productList, OnFavouriteClickListener favListener, OnCartClickListener cartListener) {
        this.productList = productList;
        this.favListener = favListener;
        this.cartListener = cartListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrecommended, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        product product = productList.get(position);
        holder.ivProductImage.setImageResource(product.getImageResId());
        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(product.getPrice());
        holder.tvProductDesc.setText(product.getDescription());

        updateHeartIcon(holder.ivHeart, product.isFavourite());

        // Favourite logic
        SharedPreferences favPrefs = holder.itemView.getContext().getSharedPreferences("MyFavourites", Context.MODE_PRIVATE);
        holder.ivHeart.setOnClickListener(v -> {
            boolean newState = !product.isFavourite();
            product.setFavourite(newState);
            updateHeartIcon(holder.ivHeart, newState);

            if (favListener != null) {
                favListener.onFavouriteClick(newState);
            }

            if (newState) {
                String metaData = product.getName() + "," + product.getPrice() + "," + product.getDescription() + "," + product.getImageResId();
                favPrefs.edit().putString("fav_" + product.getId(), metaData).apply();
            } else {
                favPrefs.edit().remove("fav_" + product.getId());
                favPrefs.edit().apply();
            }
        });

        // Cart logic
        holder.ivHomeCart.setOnClickListener(v -> {
            SharedPreferences cartPrefs = v.getContext().getSharedPreferences("MyCart", Context.MODE_PRIVATE);
            
            int qty = 1;
            String existingData = cartPrefs.getString("cart_" + product.getId(), null);
            if (existingData != null) {
                String[] parts = existingData.split(",");
                if (parts.length == 5) qty = Integer.parseInt(parts[4]) + 1;
            }

            String cartData = product.getName() + "," + product.getPrice() + "," + 
                              product.getDescription() + "," + product.getImageResId() + "," + qty;
            
            cartPrefs.edit().putString("cart_" + product.getId(), cartData).apply();
            Toast.makeText(v.getContext(), "Added to Cart!", Toast.LENGTH_SHORT).show();

            if (cartListener != null) {
                cartListener.onCartClick();
            }
        });
    }

    private void updateHeartIcon(ImageView imageView, boolean isFavourite) {
        if (isFavourite) {
            imageView.setImageResource(R.drawable.heartminus);
        } else {
            imageView.setImageResource(R.drawable.heart);
        }
    }

    public void setFilteredList(List<product> filteredList) {
        this.productList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() { return productList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage, ivHeart, ivHomeCart;
        TextView tvProductName, tvProductPrice, tvProductDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            ivHeart = itemView.findViewById(R.id.ivHeart);
            ivHomeCart = itemView.findViewById(R.id.ivHomeCart);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductDesc = itemView.findViewById(R.id.tvProductDesc);
        }
    }
}
