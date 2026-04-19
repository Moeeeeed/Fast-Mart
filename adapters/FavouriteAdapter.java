package com.example.a2.adapters;

import android.app.AlertDialog;
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
import com.example.a2.CartBadgeListener; // THE CLEAN IMPORT

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    private List<product> favList;
    private Context context;
    private CartBadgeListener badgeListener; // Use the standalone interface

    // Clean constructor
    public FavouriteAdapter(Context context, List<product> favList, CartBadgeListener badgeListener) {
        this.context = context;
        this.favList = favList;
        this.badgeListener = badgeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemfavourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        product currentItem = favList.get(position);

        holder.ivFavImage.setImageResource(currentItem.getImageResId());
        holder.tvFavName.setText(currentItem.getName());
        holder.tvFavPrice.setText(currentItem.getPrice());

        holder.ivFavMore.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setMessage("Do you want to delete this product from favourites?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        SharedPreferences prefs = context.getSharedPreferences("MyFavourites", Context.MODE_PRIVATE);
                        prefs.edit().remove("fav_" + currentItem.getId()).apply();
                        favList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, favList.size());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.ivFavCart.setOnClickListener(v -> {
            SharedPreferences cartPrefs = context.getSharedPreferences("MyCart", Context.MODE_PRIVATE);
            String cartData = currentItem.getName() + "," + currentItem.getPrice() + "," +
                    currentItem.getDescription() + "," + currentItem.getImageResId() + ",1";
            cartPrefs.edit().putString("cart_" + currentItem.getId(), cartData).apply();

            Toast.makeText(context, "Added to Cart!", Toast.LENGTH_SHORT).show();

            // Shout through the interface!
            if (badgeListener != null) {
                badgeListener.onCartBadgeUpdated();
            }
        });
    }

    @Override
    public int getItemCount() {
        return favList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFavImage, ivFavCart, ivFavMore;
        TextView tvFavName, tvFavPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFavImage = itemView.findViewById(R.id.ivFavImage);
            ivFavCart = itemView.findViewById(R.id.ivFavCart);
            ivFavMore = itemView.findViewById(R.id.ivFavMore);
            tvFavName = itemView.findViewById(R.id.tvFavName);
            tvFavPrice = itemView.findViewById(R.id.tvFavPrice);
        }
    }
}