package com.example.a2.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.a2.R;

public class SplashActivity extends AppCompatActivity {

    ImageView ivCart, ivHeadphone, ivExtraItem, ivSmartWatch, ivhf, ivcharger, ivHeadset;
    TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Sets up the splash screen with a slide-in cart, dropping items, and a fading welcome message.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitysplash);

        ivCart = findViewById(R.id.ivCart);
        ivHeadphone = findViewById(R.id.ivHeadphone);
        ivExtraItem = findViewById(R.id.ivExtraItem);
        ivSmartWatch = findViewById(R.id.ivWatch);
        tvWelcome = findViewById(R.id.Welcome);
        ivhf = findViewById(R.id.ivhf);
        ivcharger = findViewById(R.id.ivmeow);
        ivHeadset = findViewById(R.id.ivheadset);

        Animation cartAnim = AnimationUtils.loadAnimation(this, R.anim.cart_slide_in);
        Animation dropAnim = AnimationUtils.loadAnimation(this, R.anim.bouncedrop);
        Animation dropAnimDelayed = AnimationUtils.loadAnimation(this, R.anim.delaybounce);
        Animation dropAnimDelayed2 = AnimationUtils.loadAnimation(this, R.anim.delaybounce1);
        Animation dropAnimDelayed3 = AnimationUtils.loadAnimation(this, R.anim.delaybounce2);
        Animation dropAnimDelayed4 = AnimationUtils.loadAnimation(this, R.anim.delaybounce3);
        Animation dropAnimDelayed5 = AnimationUtils.loadAnimation(this, R.anim.delaybounce4);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        ivCart.startAnimation(cartAnim);
        ivHeadphone.startAnimation(dropAnim);
        ivExtraItem.startAnimation(dropAnimDelayed);
        ivSmartWatch.startAnimation(dropAnimDelayed2);
        ivcharger.startAnimation(dropAnimDelayed3);
        ivhf.startAnimation(dropAnimDelayed4);
        ivHeadset.startAnimation(dropAnimDelayed5);
        tvWelcome.startAnimation(fadeIn);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("FastMartPrefs", MODE_PRIVATE);
            boolean isFirstTime = prefs.getBoolean("isFirstTime", true);
           // getSharedPreferences("FastMartPrefs", MODE_PRIVATE).edit().clear().apply();

            if (isFirstTime) {
                startActivity(new Intent(SplashActivity.this, OnBoardActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
            }
            finish();
        }, 6500);
    }
}
