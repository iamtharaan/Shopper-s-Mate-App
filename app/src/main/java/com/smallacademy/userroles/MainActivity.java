package com.smallacademy.userroles;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;

    private HomeFragment homeFragment = new HomeFragment();
    private ShoppingListFragment listFragment = new ShoppingListFragment();
    private ScannerFragment scannerFragment = new ScannerFragment();
    private LocationFragment locationFragment = new LocationFragment();
    private AccountFragment accountFragment = new AccountFragment();

    private static final long EXIT_CONFIRMATION_THRESHOLD = 2000;
    private long lastBackPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                    return true;
                } else if (itemId == R.id.list) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, listFragment).commit();
                    return true;
                } else if (itemId == R.id.scanner) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, scannerFragment).commit();
                    return true;
                } else if (itemId == R.id.location) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, locationFragment).commit();
                    return true;
                } else if (itemId == R.id.account) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, accountFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressedTime > EXIT_CONFIRMATION_THRESHOLD) {
            showExitConfirmationToast();
            lastBackPressedTime = currentTime;
        } else {
            moveTaskToBack(true); // Minimize the app
        }
    }

    private void showExitConfirmationToast() {
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
    }
}
