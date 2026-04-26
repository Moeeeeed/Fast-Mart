package com.example.a2.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2.R;
import com.example.a2.adapters.CartAdapter;
import com.example.a2.database.FastMartDatabase;
import com.example.a2.models.CartItem;
import com.example.a2.models.product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {

    RecyclerView rvCart;
    TextView tvTotalPrice;
    Button btnCheckout;
    CartAdapter cartAdapter;
    List<CartItem> myCartItems;
    FastMartDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentcart, container, false);

        rvCart = view.findViewById(R.id.rvCart);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        db = new FastMartDatabase(getContext());
        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));

        myCartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(myCartItems, () -> calculateTotal());
        rvCart.setAdapter(cartAdapter);

        btnCheckout.setOnClickListener(v -> {
            if (myCartItems.size() == 0) {
                Toast.makeText(getContext(), getString(R.string.cartEmptyMsg), Toast.LENGTH_SHORT).show();
                return;
            }
            sendCheckoutSMS();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartFromDB();
    }

    private void loadCartFromDB() {
        myCartItems.clear();
        try {
            db.open();
            Cursor cursor = db.getAllCartItems();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(FastMartDatabase.COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(FastMartDatabase.COLUMN_NAME));
                    String price = cursor.getString(cursor.getColumnIndexOrThrow(FastMartDatabase.COLUMN_PRICE));
                    int image = cursor.getInt(cursor.getColumnIndexOrThrow(FastMartDatabase.COLUMN_IMAGE));
                    String url = cursor.getString(cursor.getColumnIndexOrThrow(FastMartDatabase.COLUMN_IMAGE_URL));
                    int qty = cursor.getInt(cursor.getColumnIndexOrThrow(FastMartDatabase.COLUMN_QUANTITY));

                    product p = new product(Integer.parseInt(id), name, price, "", image);
                    p.setImageUrl(url); // Set the URL for Glide
                    myCartItems.add(new CartItem(p, qty));
                } while (cursor.moveToNext());
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cartAdapter.notifyDataSetChanged();
        calculateTotal();
    }

    private void calculateTotal() {
        double total = 0.0;
        for (CartItem item : myCartItems) {
            String rawPrice = item.getProduct().getPrice().replace("$", "");
            try {
                total += (Double.parseDouble(rawPrice) * item.getQuantity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tvTotalPrice.setText(getString(R.string.cartTotal) + ": $" + total);
    }

    private void sendCheckoutSMS() {
        StringBuilder orderSummary = new StringBuilder("Order Details:\n");
        double total = 0.0;

        for (CartItem item : myCartItems) {
            String pricePerItem = item.getProduct().getPrice();
            orderSummary.append("- ")
                        .append(item.getProduct().getName())
                        .append(" (Qty: ")
                        .append(item.getQuantity())
                        .append(") Price: ")
                        .append(pricePerItem)
                        .append("\n");
            
            total += (Double.parseDouble(pricePerItem.replace("$", "")) * item.getQuantity());
        }
        orderSummary.append("Final Total: $" + total);

        saveOrderToFirebase(orderSummary.toString(), total);

        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:+923214441218"));
        intent.putExtra("sms_body", orderSummary.toString());

        try {
            startActivity(intent);
            db.open();
            for(CartItem item : myCartItems) {
                db.removeFromCart(String.valueOf(item.getProduct().getId()));
            }
            db.close();

            myCartItems.clear();
            cartAdapter.notifyDataSetChanged();
            calculateTotal();
        } catch (Exception e) {
            Toast.makeText(getContext(), getString(R.string.smsFailedMsg), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveOrderToFirebase(String summary, double total) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders").push();
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("buyerId", uid);
        orderMap.put("summary", summary);
        orderMap.put("totalPrice", total);
        orderMap.put("timestamp", System.currentTimeMillis());
        orderRef.setValue(orderMap);
    }
}
