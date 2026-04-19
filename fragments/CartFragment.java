package com.example.a2.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.a2.models.CartItem;
import com.example.a2.models.product;
import com.example.a2.models.productsdata;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    RecyclerView rvCart;
    TextView tvTotalPrice;
    Button btnCheckout;
    CartAdapter cartAdapter;
    List<CartItem> myCartItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initializes the screen and the list of items in the cart.
        View view = inflater.inflate(R.layout.fragmentcart, container, false);

        rvCart = view.findViewById(R.id.rvCart);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));

        myCartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(myCartItems, () -> calculateTotal());
        rvCart.setAdapter(cartAdapter);

        btnCheckout.setOnClickListener(v -> {
            if (myCartItems.size() == 0) {
                Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            sendCheckoutSMS();
        });

        return view;
    }

    @Override
    public void onResume() {
        // Reloads the cart items from memory whenever the tab is opened.
        super.onResume();
        myCartItems.clear();
        myCartItems.addAll(loadCartFromPrefs());
        cartAdapter.notifyDataSetChanged();
        calculateTotal();
    }

    private List<CartItem> loadCartFromPrefs() {
        // Uses a simple loop to check which products are currently in the cart.
        List<CartItem> list = new ArrayList<>();
        SharedPreferences prefs = getActivity().getSharedPreferences("MyCart", Context.MODE_PRIVATE);
        List<product> allProducts = productsdata.getAllProducts();

        for (int i = 0; i < allProducts.size(); i++) {
            product p = allProducts.get(i);
            String data = prefs.getString("cart_" + p.getId(), null);
            if (data != null) {
                String[] parts = data.split(",");
                if (parts.length == 5) {
                    int quantity = Integer.parseInt(parts[4]);
                    list.add(new CartItem(p, quantity));
                }
            }
        }
        return list;
    }

    private void calculateTotal() {
        // Loops through the cart items to add up the final price.
        double total = 0.0;
        for (int i = 0; i < myCartItems.size(); i++) {
            CartItem item = myCartItems.get(i);
            String rawPrice = item.getProduct().getPrice().replace("$", "");
            try {
                total += (Double.parseDouble(rawPrice) * item.getQuantity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tvTotalPrice.setText(String.format("Total: $%.2f", total));
    }

    private void sendCheckoutSMS() {
        // Opens the SMS app with the order summary using an implicit intent.
        StringBuilder orderSummary = new StringBuilder("FastMart Order Details:\n");
        double total = 0.0;

        for (int i = 0; i < myCartItems.size(); i++) {
            CartItem item = myCartItems.get(i);
            orderSummary.append(item.getQuantity()).append("x ").append(item.getProduct().getName()).append("\n");
            total += (Double.parseDouble(item.getProduct().getPrice().replace("$", "")) * item.getQuantity());
        }
        orderSummary.append(String.format("Total: $%.2f", total));

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:+923214441218"));
        intent.putExtra("sms_body", orderSummary.toString());

        try {
            startActivity(intent);
            getActivity().getSharedPreferences("MyCart", Context.MODE_PRIVATE).edit().clear().apply();
            myCartItems.clear();
            cartAdapter.notifyDataSetChanged();
            calculateTotal();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to open SMS app.", Toast.LENGTH_SHORT).show();
        }
    }
}
