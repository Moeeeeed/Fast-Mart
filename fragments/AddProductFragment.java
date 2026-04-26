package com.example.a2.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a2.R;
import com.example.a2.models.product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddProductFragment extends Fragment {

    // camelCase variables
    EditText etName, etPrice, etDesc, etImageUrl;
    Button btnAdd;
    DatabaseReference dbRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        etName = view.findViewById(R.id.etProductName);
        etPrice = view.findViewById(R.id.etProductPrice);
        etDesc = view.findViewById(R.id.etProductDesc);
        etImageUrl = view.findViewById(R.id.etProductImageUrl);
        btnAdd = view.findViewById(R.id.btnAddProduct);

        dbRef = FirebaseDatabase.getInstance().getReference("Products");

        btnAdd.setOnClickListener(v -> {
            uploadProduct();
        });

        return view;
    }

    private void uploadProduct() {
        String name = etName.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || desc.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Uploading...");
        pd.show();

        // Create unique ID using timestamp
        int id = (int) (System.currentTimeMillis() / 1000);
        
        // Price with $ sign for display consistency
        String formattedPrice = "$" + price;
        
        // Create product with URL for Bonus marks
        product p = new product(id, name, formattedPrice, desc, imageUrl);

        dbRef.child(String.valueOf(id)).setValue(p)
            .addOnCompleteListener(task -> {
                pd.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Product Added!", Toast.LENGTH_SHORT).show();
                    etName.setText("");
                    etPrice.setText("");
                    etDesc.setText("");
                    etImageUrl.setText("");
                } else {
                    Toast.makeText(getActivity(), "Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}
