package com.example.a2.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2.R;
import com.example.a2.activities.ChatActivity;
import com.example.a2.adapters.DealsAdapter;
import com.example.a2.adapters.RecommendedAdapter;
import com.example.a2.models.product;
import com.example.a2.models.productsdata;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // camelCase variables
    RecyclerView rvDeals, rvRecommended;
    DealsAdapter dealsAdapter;
    RecommendedAdapter recommendedAdapter;
    FloatingActionButton fabChat;
    
    List<product> recommendedList;
    DatabaseReference dbRef;

    private BadgeListener badgeListener;

    public interface BadgeListener {
        void onBadgeChange(boolean isAdded);
        void onCartBadgeChange();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BadgeListener) {
            badgeListener = (BadgeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement BadgeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        badgeListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmenthome, container, false);

        rvDeals = view.findViewById(R.id.lvDeals);
        rvRecommended = view.findViewById(R.id.rvRecommended);
        fabChat = view.findViewById(R.id.fabChat);

        // Horizontal list for deals
        rvDeals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<product> dealsList = productsdata.getDealsData();
        dealsAdapter = new DealsAdapter(dealsList);
        rvDeals.setAdapter(dealsAdapter);

        // Grid for recommended products from Firebase
        rvRecommended.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recommendedList = new ArrayList<>();
        
        recommendedAdapter = new RecommendedAdapter(recommendedList, 
            isAdded -> {
                if (badgeListener != null) badgeListener.onBadgeChange(isAdded);
            },
            () -> {
                if (badgeListener != null) badgeListener.onCartBadgeChange();
            }
        );
        rvRecommended.setAdapter(recommendedAdapter);

        // Fetch products from Firebase Realtime Database
        dbRef = FirebaseDatabase.getInstance().getReference("Products");
        
        // TEMPORARY: If database is empty, add some sample products so you can see them!
        checkAndAddSampleProducts();

        fetchProductsFromFirebase();

        fabChat.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ChatActivity.class));
        });

        return view;
    }

    private void fetchProductsFromFirebase() {
        // Using ValueEventListener for real-time sync as per assignment
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recommendedList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        product p = data.getValue(product.class);
                        if (p != null) {
                            recommendedList.add(p);
                        }
                    }
                } else {
                    // If no products in DB yet, show a message
                    Toast.makeText(getContext(), "No products found in database!", Toast.LENGTH_SHORT).show();
                }
                recommendedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAndAddSampleProducts() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Database is empty! Adding sample items with URLs for Bonus marks
                    List<product> samples = new ArrayList<>();
                    samples.add(new product(4, "Smart Watch", "$19.99", "Digital watch", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500"));
                    samples.add(new product(5, "Headset", "$29.99", "Over-ear headset", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500"));
                    samples.add(new product(6, "Gaming Headset", "$24.99", "Wireless gaming", "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=500"));
                    
                    for (product p : samples) {
                        dbRef.child(String.valueOf(p.getId())).setValue(p);
                    }
                    Toast.makeText(getContext(), "Added sample products with URLs!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
