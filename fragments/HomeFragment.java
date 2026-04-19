package com.example.a2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2.R;
import com.example.a2.adapters.DealsAdapter;
import com.example.a2.adapters.RecommendedAdapter;
import com.example.a2.models.product;
import com.example.a2.models.productsdata;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView rvDeals, rvRecommended;
    DealsAdapter dealsAdapter;
    RecommendedAdapter recommendedAdapter;

    private BadgeListener badgeListener;

    public interface BadgeListener {
        void onBadgeChange(boolean isAdded);
        void onCartBadgeChange(); // New method for Cart badge
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

        rvDeals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        List<product> dealsList = productsdata.getDealsData();
        dealsAdapter = new DealsAdapter(dealsList);
        rvDeals.setAdapter(dealsAdapter);

        rvRecommended.setLayoutManager(new GridLayoutManager(getContext(), 2));
        List<product> recommendedList = productsdata.getRecommendedData();

        // Pass both listeners to the adapter
        recommendedAdapter = new RecommendedAdapter(recommendedList, 
            isAdded -> {
                if (badgeListener != null)
                    badgeListener.onBadgeChange(isAdded);
            },
            () -> {
                if (badgeListener != null)
                    badgeListener.onCartBadgeChange();
            }
        );

        rvRecommended.setAdapter(recommendedAdapter);

        return view;
    }
}
