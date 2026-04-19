package com.example.a2.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2.R;
import com.example.a2.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> cartList;
    private CartUpdateListener listener;

    public interface CartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(List<CartItem> cartList, CartUpdateListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Updated to use the correct layout name from your project (itemcart.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemcart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem currentItem = cartList.get(position);
        Context context = holder.itemView.getContext();
        
        holder.tvCartName.setText(currentItem.getProduct().getName());
        holder.tvCartPrice.setText(currentItem.getProduct().getPrice());
        holder.tvQuantity.setText(String.valueOf(currentItem.getQuantity()));
        holder.ivCartImage.setImageResource(currentItem.getProduct().getImageResId());

        SharedPreferences prefs = context.getSharedPreferences("MyCart", Context.MODE_PRIVATE);

        // INCREASE (+)
        holder.btnPlus.setOnClickListener(v -> {
            currentItem.setQuantity(currentItem.getQuantity() + 1);
            holder.tvQuantity.setText(String.valueOf(currentItem.getQuantity()));
            saveToMemory(prefs, currentItem);
            listener.onCartUpdated(); 
        });

        // DECREASE (-)
        holder.btnMinus.setOnClickListener(v -> {
            if (currentItem.getQuantity() > 1) {
                currentItem.setQuantity(currentItem.getQuantity() - 1);
                holder.tvQuantity.setText(String.valueOf(currentItem.getQuantity()));
                saveToMemory(prefs, currentItem);
                listener.onCartUpdated(); 
            }
        });

        // DELETE (3-Dots)
        holder.ivCartDelete.setOnClickListener(v -> {
            // Remove from SharedPreferences
            prefs.edit().remove("cart_" + currentItem.getProduct().getId()).apply();
            
            // Remove from screen
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                cartList.remove(currentPos); 
                notifyItemRemoved(currentPos);
                notifyItemRangeChanged(currentPos, cartList.size());
                listener.onCartUpdated();
            }
        });
    }

    private void saveToMemory(SharedPreferences prefs, CartItem item) {
        String data = item.getProduct().getName() + "," + item.getProduct().getPrice() + "," + 
                      item.getProduct().getDescription() + "," + item.getProduct().getImageResId() + "," + item.getQuantity();
        prefs.edit().putString("cart_" + item.getProduct().getId(), data).apply();
    }

    @Override
    public int getItemCount() { return cartList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCartImage, ivCartDelete, btnPlus, btnMinus;
        TextView tvCartName, tvCartPrice, tvQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCartImage = itemView.findViewById(R.id.ivCartImage);
            ivCartDelete = itemView.findViewById(R.id.ivCartDelete);
            tvCartName = itemView.findViewById(R.id.tvCartName);
            tvCartPrice = itemView.findViewById(R.id.tvCartPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}
