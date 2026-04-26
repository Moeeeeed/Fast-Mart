package com.example.a2.adapters;

import android.app.AlertDialog;
import android.content.Context;
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
import com.example.a2.CartBadgeListener;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    
    private List<product> favList;
    private Context context;
    private CartBadgeListener badgeListener;
    private FastMartDatabase db;

    public FavouriteAdapter(Context context, List<product> favList, CartBadgeListener badgeListener) {
        this.context = context;
        this.favList = favList;
        this.badgeListener = badgeListener;
        this.db = new FastMartDatabase(context);
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

        if (currentItem.getImageUrl() != null && !currentItem.getImageUrl().isEmpty()) {
            Glide.with(context)
                .load(currentItem.getImageUrl())
                .placeholder(R.drawable.vert)
                .into(holder.ivFavImage);
        } else {
            holder.ivFavImage.setImageResource(currentItem.getImageResId());
        }

        holder.tvFavName.setText(currentItem.getName());
        holder.tvFavPrice.setText(currentItem.getPrice());

        holder.ivFavMore.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.favouritesTitle));
            builder.setMessage(context.getString(R.string.dialogDeleteFavMsg));
            
            builder.setPositiveButton(context.getString(R.string.dialogYes), (dialog, which) -> {
                db.open();
                db.removeFavourite(String.valueOf(currentItem.getId()));
                db.close();
                
                int currentPos = holder.getAdapterPosition();
                favList.remove(currentPos);
                notifyItemRemoved(currentPos);
                notifyItemRangeChanged(currentPos, favList.size());
                
                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
            });
            
            builder.setNegativeButton(context.getString(R.string.dialogNo), (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        holder.ivFavCart.setOnClickListener(v -> {
            db.open();
            db.addToCart(String.valueOf(currentItem.getId()), currentItem.getName(), currentItem.getPrice(), currentItem.getImageResId(), currentItem.getImageUrl(), 1);
            db.close();

            Toast.makeText(context, context.getString(R.string.addedToCartMsg), Toast.LENGTH_SHORT).show();

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
