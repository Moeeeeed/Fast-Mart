package com.example.a2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2.R;
import com.example.a2.adapters.FavouriteAdapter;
import com.example.a2.models.product;
import com.example.a2.models.productsdata;
import com.example.a2.CartBadgeListener;
import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    RecyclerView rvFavourites;
    FavouriteAdapter favouritesAdapter;
    List<product> favouriteProducts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Sets up the user interface for the Favorites screen.
        View view = inflater.inflate(R.layout.fragmentfavourites, container, false);

        rvFavourites = view.findViewById(R.id.rvFavourites);
        rvFavourites.setLayoutManager(new LinearLayoutManager(getContext()));

        favouriteProducts = new ArrayList<>();
        // Because MainActivity implements CartBadgeListener, we can safely cast getActivity()
        CartBadgeListener listener = (CartBadgeListener) getActivity();

        favouritesAdapter = new FavouriteAdapter(getContext(), favouriteProducts, listener);
        rvFavourites.setAdapter(favouritesAdapter);

        return view;
    }

    @Override
    public void onResume() {
        // Refreshes the list every time the user comes back to this tab.
        super.onResume();
        favouriteProducts.clear();
        favouriteProducts.addAll(loadFavouritesFromPrefs());
        favouritesAdapter.notifyDataSetChanged();
    }


    private List<product> loadFavouritesFromPrefs() {
        // Loops through all products to find which ones were saved as favorites.
        List<product> list = new ArrayList<>();
        SharedPreferences prefs = getActivity().getSharedPreferences("MyFavourites", Context.MODE_PRIVATE);
        List<product> allProducts = productsdata.getAllProducts();

        for (int i = 0; i < allProducts.size(); i++) {
            product p = allProducts.get(i);
            if (prefs.contains("fav_" + p.getId())) {
                p.setFavourite(true);
                list.add(p);
            }
        }
        return list;
    }
}
