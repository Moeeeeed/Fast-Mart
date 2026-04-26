package com.example.a2.models;

import com.example.a2.R;
import java.util.ArrayList;
import java.util.List;

public class productsdata {

    public static List<CartItem> globalCart = new ArrayList<>();

    public static List<product> getDealsData() {
        List<product> list = new ArrayList<>();
        list.add(new product(1, "Earphones", "$49.99", "High quality wired earphones for daily use", R.drawable.i1));
        list.add(new product(2, "Earphones", "$89.99", "Premium bass boosting earphones with clear sound", R.drawable.i2));
        list.add(new product(3, "Earphones", "$59.99", "Comfortable noise cancelling earphones for your music", R.drawable.i3));
        return list;
    }

    public static List<product> getRecommendedData() {
        List<product> list = new ArrayList<>();
        list.add(new product(4, "Smart Watch", "$19.99", "Digital smart watch with health tracking features", R.drawable.i4));
        list.add(new product(5, "Headset", "$29.99", "Stylish over-ear headset with a built-in microphone", R.drawable.i5));
        list.add(new product(6, "Headset ", "$24.99", "Comfortable wireless headset for long gaming sessions", R.drawable.i6));
        list.add(new product(7, "Handfree", "$15.99", "Tangle free wired handfree for easy listening", R.drawable.i7));
        list.add(new product(8, "pink headset for girls", "$25.00", "Cute pink colored headset with soft earpads", R.drawable.i8));
        list.add(new product(9, "handfree", "$45.00", "Clear audio handfree with an inline microphone", R.drawable.i9));
        list.add(new product(10, "charger", "$35.99", "Fast charging wall adapter for your devices", R.drawable.i10));
        list.add(new product(11, "slow charger", "$40.00", "Standard speed wall charger for safe charging", R.drawable.i11));
        list.add(new product(12, "watch", "$29.99", "Modern wrist watch with a classic dial", R.drawable.i12));
        list.add(new product(13, "Headset", "$59.99", "Elegant light pink headset with premium audio", R.drawable.i13));
        list.add(new product(14, "headset", "$19.99", "Lightweight and comfortable over ear listening headset", R.drawable.i14));
        list.add(new product(15, "watch", "$12.99", "Sleek fitness tracker watch for active lifestyles", R.drawable.i15));
        list.add(new product(16, "watch", "$15.99", "Durable stainless steel watch for everyday wear", R.drawable.i16));
        list.add(new product(17, "watch", "$49.99", "Stylish digital watch with a comfortable strap", R.drawable.i17));
        list.add(new product(18, "watch", "$22.00", "Premium water resistant watch with analog dial", R.drawable.i18));
        list.add(new product(19, "headset light pink", "$55.00", "Beautiful light pink headset with adjustable band", R.drawable.i19));
        list.add(new product(20, "handfree", "$75.00", "Lightweight wired handfree with crystal clear sound", R.drawable.i20));
        list.add(new product(21, "charger", "$12.99", "Durable and portable universal device charging adapter", R.drawable.i21));
        list.add(new product(22, "Earphones", "$49.99", "Ergonomic earphones for comfortable all day listening", R.drawable.i1));
        list.add(new product(23, "Earphones", "$89.99", "Premium stereo earphones delivering crisp audio quality", R.drawable.i2));
        list.add(new product(24, "Earphones", "$59.99", "Durable noise isolating earphones for daily commutes", R.drawable.i3));

        return list;
    }

    public static List<product> getAllProducts() {
        List<product> all = new ArrayList<>();
        List<product> deals = getDealsData();
        for (int i = 0; i < deals.size(); i++) {
            all.add(deals.get(i));
        }
        List<product> recommended = getRecommendedData();
        for (int i = 0; i < recommended.size(); i++) {
            all.add(recommended.get(i));
        }
        return all;
    }
}