package com.example.spendwise;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);

        // ✅ Safe NavController retrieval
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navView, navController);

        // 👇 Resize icon size
        navView.post(() -> {
            for (int i = 0; i < navView.getChildCount(); i++) {
                View item = navView.getChildAt(i);
                if (item instanceof ViewGroup) {
                    ViewGroup itemGroup = (ViewGroup) item;
                    for (int j = 0; j < itemGroup.getChildCount(); j++) {
                        View child = itemGroup.getChildAt(j);
                        if (child instanceof ImageView) {
                            ((ImageView) child).setScaleX(2.5f);  // Increase icon size
                            ((ImageView) child).setScaleY(2.5f);
                        }
                    }
                }
            }
        });
    }
}
