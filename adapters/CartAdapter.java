package com.example.a2.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.a2.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    
    // Use camelCase naming
    private List<CartItem> cartList;
    private CartUpdateListener listener;
    private FastMartDatabase db;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemcart, parent, false);
        db = new FastMartDatabase(parent.getContext());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem currentItem = cartList.get(position);
        
        holder.tvCartName.setText(currentItem.getProduct().getName());
        holder.tvCartPrice.setText(currentItem.getProduct().getPrice());
        holder.tvQuantity.setText(String.valueOf(currentItem.getQuantity()));
        
        // Bonus: Use Glide for Cart images
        if (currentItem.getProduct().getImageUrl() != null && !currentItem.getProduct().getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(currentItem.getProduct().getImageUrl())
                .placeholder(R.drawable.vert)
                .into(holder.ivCartImage);
        } else {
            holder.ivCartImage.setImageResource(currentItem.getProduct().getImageResId());
        }

        // INCREASE Quantity (+)
        holder.btnPlus.setOnClickListener(v -> {
            int newQty = currentItem.getQuantity() + 1;
            currentItem.setQuantity(newQty);
            holder.tvQuantity.setText(String.valueOf(newQty));
            
            db.open();
            db.updateCartQuantity(String.valueOf(currentItem.getProduct().getId()), newQty);
            db.close();
            
            listener.onCartUpdated(); 
        });

        // DECREASE Quantity (-)
        holder.btnMinus.setOnClickListener(v -> {
            if (currentItem.getQuantity() > 1) {
                int newQty = currentItem.getQuantity() - 1;
                currentItem.setQuantity(newQty);
                holder.tvQuantity.setText(String.valueOf(newQty));
                
                db.open();
                db.updateCartQuantity(String.valueOf(currentItem.getProduct().getId()), newQty);
                db.close();
                
                listener.onCartUpdated(); 
            }
        });

        // DELETE Item with AlertDialog
        holder.ivCartDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            builder.setTitle("Remove Item");
            builder.setMessage("Are you sure you want to delete this from your cart?");
            
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.open();
                    db.removeFromCart(String.valueOf(currentItem.getProduct().getId()));
                    db.close();

                    int currentPos = holder.getAdapterPosition();
                    cartList.remove(currentPos);
                    notifyItemRemoved(currentPos);
                    notifyItemRangeChanged(currentPos, cartList.size());
                    
                    listener.onCartUpdated();
                    Toast.makeText(holder.itemView.getContext(), "Item Removed", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
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
