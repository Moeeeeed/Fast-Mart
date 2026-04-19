package com.example.a2.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a2.R;
import com.example.a2.models.product;
import com.example.a2.models.productsdata;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchFragment extends Fragment {

    // ASSIGNMENT REQUIREMENT 1: Create variable SearchHashKey
    public static final String SearchHashKey = "SearchHistoryKey";
    private static final String PREFS_NAME = "SearchPrefs";

    EditText etSearchBox;
    ImageView ivBackArrow;
    Button btnClearAll;
    ListView lvSearchHistory;

    List<product> allProducts;
    List<String> searchHistoryList;
    ArrayAdapter<String> historyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentsearch, container, false);

        etSearchBox = view.findViewById(R.id.etSearchBox);
        ivBackArrow = view.findViewById(R.id.ivBackArrow);
        btnClearAll = view.findViewById(R.id.btnClearAll);
        lvSearchHistory = view.findViewById(R.id.lvSearchHistory);

        allProducts = productsdata.getAllProducts();

        loadSearchHistory(); // Load history from SharedPreferences

        //  When user presses back arrow, hide the soft keyboard
        ivBackArrow.setOnClickListener(v -> hideKeyboard(view));

        // Listen for the "Search" button on the phone's keyboard
        etSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                    String query = etSearchBox.getText().toString().trim();
                    if (!query.isEmpty()) {
                        performSearch(query);
                        saveToHistory(query);
                        hideKeyboard(view); // Hide keyboard after searching
                    }
                    return true;
                }
                return false;
            }
        });

        // Clear All button
        btnClearAll.setOnClickListener(v -> clearSearchHistory());

        return view;
    }

    // --- SEARCH ALGORITHM & ALERT DIALOG ---
    private void performSearch(String query) {
        boolean isFound = false;


        for (product p : allProducts) {
            if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                isFound = true;
                break; // Stop searching once we find a match
            }
        }

        //  Display AlertDialog
        if (isFound) {
            new AlertDialog.Builder(getContext())
                    .setMessage(getString(R.string.dialogProductFound))
                    .setPositiveButton(getString(R.string.dialogOK), null)
                    .show();
        } else {
            new AlertDialog.Builder(getContext())
                    .setMessage(getString(R.string.dialogProductNotFound))
                    .setPositiveButton(getString(R.string.dialogOK), null)
                    .show();
        }
    }


    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        etSearchBox.clearFocus();
    }

    // --- SHARED PREFERENCES LOGIC ---
    private void saveToHistory(String query) {
        // Add to the top of our list if it's not already there
        if (!searchHistoryList.contains(query)) {
            searchHistoryList.add(0, query);
            historyAdapter.notifyDataSetChanged();

            // Save the updated list to SharedPreferences as a comma-separated string
            SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < searchHistoryList.size(); i++) {
                sb.append(searchHistoryList.get(i));
                if (i < searchHistoryList.size() - 1) sb.append(",");
            }
            prefs.edit().putString(SearchHashKey, sb.toString()).apply();
        }
    }

    private void loadSearchHistory() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String historyString = prefs.getString(SearchHashKey, "");

        searchHistoryList = new ArrayList<>();
        if (!historyString.isEmpty()) {
            searchHistoryList.addAll(Arrays.asList(historyString.split(",")));
        }

        historyAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, searchHistoryList);
        lvSearchHistory.setAdapter(historyAdapter);
    }

    private void clearSearchHistory() {
        // Clear SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(SearchHashKey).apply();

        // Clear the visible list
        searchHistoryList.clear();
        historyAdapter.notifyDataSetChanged();
    }
}
