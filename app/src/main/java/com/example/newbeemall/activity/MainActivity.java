package com.example.newbeemall.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.newbeemall.R;
import com.example.newbeemall.fragment.CartFragment;
import com.example.newbeemall.fragment.CategoryFragment;
import com.example.newbeemall.fragment.HomeFragment;
import com.example.newbeemall.fragment.MyFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            switchFragment(new HomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                switchFragment(new HomeFragment());
            } else if (id == R.id.nav_category) {
                switchFragment(new CategoryFragment());
            } else if (id == R.id.nav_cart) {
                switchFragment(new CartFragment());
            } else if (id == R.id.nav_my) {
                switchFragment(new MyFragment());
            }
            return true;
        });
    }

    public void switchFragment(Fragment fragment) {
        currentFragment = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void switchToCategory() {
        bottomNav.setSelectedItemId(R.id.nav_category);
    }

    public void switchToCart() {
        bottomNav.setSelectedItemId(R.id.nav_cart);
    }

    public void switchToHome() {
        bottomNav.setSelectedItemId(R.id.nav_home);
    }
}
