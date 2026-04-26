package com.example.a2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a2.R;
import com.example.a2.models.product;
import java.util.List;

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.ViewHolder> {
    private List<product> dealsList;

    public DealsAdapter(List<product> dealsList) { this.dealsList = dealsList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemdeal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        product product = dealsList.get(position);
        
        // Bonus: Use Glide for consistency
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.vert)
                .into(holder.ivDealImage);
        } else {
            holder.ivDealImage.setImageResource(product.getImageResId());
        }

        holder.tvDealName.setText(product.getName());
        holder.tvDealPrice.setText(product.getPrice());
    }

    @Override
    public int getItemCount() { return dealsList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDealImage;
        TextView tvDealName, tvDealPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDealImage = itemView.findViewById(R.id.ivDealImage);
            tvDealName = itemView.findViewById(R.id.tvDealName);
            tvDealPrice = itemView.findViewById(R.id.tvDealPrice);
        }
    }
}
