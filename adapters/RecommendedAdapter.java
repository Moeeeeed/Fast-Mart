package com.example.a2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a2.R;
import com.example.a2.database.FastMartDatabase;
import com.example.a2.models.product;
import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {
    
    private List<product> productList;
    private OnFavouriteClickListener favListener;
    private OnCartClickListener cartListener;
    private FastMartDatabase db;

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
        db = new FastMartDatabase(parent.getContext());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        product product = productList.get(position);
        
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.vert) 
                .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(product.getImageResId());
        }

        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(product.getPrice());
        holder.tvProductDesc.setText(product.getDescription());

        updateHeartIcon(holder.ivHeart, product.isFavourite());

        holder.ivHeart.setOnClickListener(v -> {
            boolean newState = !product.isFavourite();
            product.setFavourite(newState);
            updateHeartIcon(holder.ivHeart, newState);

            if (favListener != null) {
                favListener.onFavouriteClick(newState);
            }

            try {
                db.open();
                if (newState) {
                    db.addFavourite(String.valueOf(product.getId()), product.getName(), product.getPrice(), product.getImageResId(), product.getImageUrl());
                } else {
                    db.removeFavourite(String.valueOf(product.getId()));
                }
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        holder.ivHomeCart.setOnClickListener(v -> {
            try {
                db.open();
                db.addToCart(String.valueOf(product.getId()), product.getName(), product.getPrice(), product.getImageResId(), product.getImageUrl(), 1);
                db.close();
                Toast.makeText(v.getContext(), v.getContext().getString(R.string.addedToCartMsg), Toast.LENGTH_SHORT).show();

                if (cartListener != null) {
                    cartListener.onCartClick();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
