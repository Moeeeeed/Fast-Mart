package com.example.a2.fragments;

import android.database.Cursor;
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
import com.example.a2.database.FastMartDatabase;
import com.example.a2.models.product;
import com.example.a2.CartBadgeListener;
import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    RecyclerView rvFavourites;
    FavouriteAdapter favouritesAdapter;
    List<product> favouriteProducts;
    FastMartDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentfavourites, container, false);

        rvFavourites = view.findViewById(R.id.rvFavourites);
        rvFavourites.setLayoutManager(new LinearLayoutManager(getContext()));
        
        db = new FastMartDatabase(getContext());
        favouriteProducts = new ArrayList<>();
        
        CartBadgeListener listener = (CartBadgeListener) getActivity();
        favouritesAdapter = new FavouriteAdapter(getContext(), favouriteProducts, listener);
        rvFavourites.setAdapter(favouritesAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavouritesFromDB();
    }

    private void loadFavouritesFromDB() {
        favouriteProducts.clear();
        try {
            db.open();
            Cursor cursor = db.getAllFavourites();
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(FastMartDatabase.COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(FastMartDatabase.COLUMN_NAME));
                    String price = cursor.getString(cursor.getColumnIndexOrThrow(FastMartDatabase.COLUMN_PRICE));
                    int image = cursor.getInt(cursor.getColumnIndexOrThrow(FastMartDatabase.COLUMN_IMAGE));
                    String url = cursor.getString(cursor.getColumnIndexOrThrow(FastMartDatabase.COLUMN_IMAGE_URL));

                    product p = new product(Integer.parseInt(id), name, price, "", image);
                    p.setImageUrl(url);
                    p.setFavourite(true);
                    favouriteProducts.add(p);
                } while (cursor.moveToNext());
                cursor.close();
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        favouritesAdapter.notifyDataSetChanged();
    }
}
