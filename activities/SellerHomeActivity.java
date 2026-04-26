package com.example.a2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.a2.R;
import com.example.a2.fragments.AddProductFragment;
import com.example.a2.fragments.HomeFragment;
import com.example.a2.fragments.OrderHistoryFragment;
import com.example.a2.fragments.ChatListFragment;
import com.example.a2.fragments.AccountFragment;
import com.example.a2.CartBadgeListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class SellerHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.BadgeListener, CartBadgeListener {

    // camelCase variables
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar toolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate
        applySavedTheme();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitysellerhome);

        initViews();
        setupDrawer();
        updateHeaderInfo();

        // Default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerSeller, new AddProductFragment())
                .commit();
            navView.setCheckedItem(R.id.nav_add_product);
        }
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navView = findViewById(R.id.navViewSeller);
        toolbar = findViewById(R.id.toolbarSeller);
        
        setSupportActionBar(toolbar);
        navView.setNavigationItemSelectedListener(this);
    }

    private void setupDrawer() {
        // Using your syntax for Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.appName, R.string.appName);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void updateHeaderInfo() {
        View headerView = navView.getHeaderView(0);
        TextView tvName = headerView.findViewById(R.id.tvHeaderSellerName);
        
        SharedPreferences spref = getSharedPreferences("FastMartPrefs", MODE_PRIVATE);
        String name = spref.getString("userName", "Seller");
        tvName.setText(name);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment selectedFragment = null;

        if (id == R.id.nav_seller_home) {
            selectedFragment = new HomeFragment();
        } else if (id == R.id.nav_add_product) {
            selectedFragment = new AddProductFragment();
        } else if (id == R.id.nav_order_history) {
            selectedFragment = new OrderHistoryFragment();
        } else if (id == R.id.nav_seller_chats) {
            selectedFragment = new ChatListFragment();
        } else if (id == R.id.nav_seller_account) {
            selectedFragment = new AccountFragment();
        } else if (id == R.id.nav_theme_toggle) {
            toggleTheme();
        } else if (id == R.id.nav_logout) {
            performLogout();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerSeller, selectedFragment)
                .commit();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void toggleTheme() {
        SharedPreferences spref = getSharedPreferences("FastMartPrefs", MODE_PRIVATE);
        boolean isDark = spref.getBoolean("isDarkMode", false);
        
        SharedPreferences.Editor editor = spref.edit();
        editor.putBoolean("isDarkMode", !isDark);
        editor.apply();
        
        // Recreate activity to apply theme
        recreate();
    }

    private void applySavedTheme() {
        SharedPreferences spref = getSharedPreferences("FastMartPrefs", MODE_PRIVATE);
        boolean isDark = spref.getBoolean("isDarkMode", false);
        
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void performLogout() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences spref = getSharedPreferences("FastMartPrefs", MODE_PRIVATE);
        spref.edit().putBoolean("isLogin", false).apply();
        
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    @Override
    public void onBadgeChange(boolean isAdded) {
        // Seller doesn't use badges, but needed for HomeFragment compatibility
    }

    @Override
    public void onCartBadgeChange() {
        // Seller doesn't use badges, but needed for HomeFragment compatibility
    }

    @Override
    public void onCartBadgeUpdated() {
        // Seller doesn't use badges, but needed for HomeFragment compatibility
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
