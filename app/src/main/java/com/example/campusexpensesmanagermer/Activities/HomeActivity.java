package com.example.campusexpensesmanagermer.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.campusexpensesmanagermer.Adapters.ViewPagerApdapter;
import com.example.campusexpensesmanagermer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String PREFS_NAME = "CampusExpensesPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";

    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    ViewPager2 viewPager2;
    Toolbar toolbar;
    NavigationView navigationView;
    SharedPreferences prefs;

    @Override
    protected void onStart() {
        super.onStart();
        // Check persistent session
        if (!isLoggedIn()) {
            Log.d("HomeDebug", "Not logged in, redirecting to Login");
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        viewPager2 = findViewById(R.id.viewPager);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);

        // Retrieve username from prefs or intent (for initial setup)
        String username = prefs.getString(KEY_USERNAME, "");
        if (TextUtils.isEmpty(username)) {
            // Fallback to intent if not in prefs
            Intent intentHome = getIntent();
            Bundle bundleHome = intentHome.getExtras();
            if (bundleHome != null) {
                username = bundleHome.getString("ACCOUNT", "");
                if (!TextUtils.isEmpty(username)) {
                    saveLoginState(username); // Persist it
                    Log.d("HomeDebug", "Saved username from intent: " + username);
                }
            }
        }

        Log.d("HomeDebug", "Username: '" + username + "', IsLoggedIn: " + isLoggedIn());

        if (TextUtils.isEmpty(username) || !isLoggedIn()) {
            Log.d("HomeDebug", "Redirecting to Login: Missing data");
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return; // Dừng setup UI
        }

        // Xu ly hien thi drawer menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Xu ly khi bam menu bottom navigation
        setupViewPager();
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int page = 0;
            if (item.getItemId() == R.id.menu_home) {
                page = 0;
            } else if (item.getItemId() == R.id.menu_budget) {
                page = 1;
            } else if (item.getItemId() == R.id.menu_expenses) {
                page = 2;
            } else if (item.getItemId() == R.id.menu_report) {
                page = 3;
            } else if (item.getItemId() == R.id.menu_profile) {
                page = 4; // Fixed: Match drawer
            } else if (item.getItemId() == R.id.menu_setting) {
                page = 5; // Fixed: Match drawer
            } else {
                page = 0;
            }
            viewPager2.setCurrentItem(page);
            return true;
        });

        // Xu ly logout App
        Menu menu = navigationView.getMenu();
        MenuItem logout = menu.findItem(R.id.menu_Logout);
        MenuItem account = menu.findItem(R.id.tvAccount);
        if (!TextUtils.isEmpty(username)) {
            account.setTitle("Hi: " + username);
        }
        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                logoutUser();
                return true; // Fixed: Return true for proper handling
            }
        });
    }

    private void setupViewPager() {
        ViewPagerApdapter apdapter = new ViewPagerApdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(apdapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                MenuItem item = null;
                if (position == 0) {
                    item = bottomNavigationView.getMenu().findItem(R.id.menu_home);
                } else if (position == 1) {
                    item = bottomNavigationView.getMenu().findItem(R.id.menu_budget);
                } else if (position == 2) {
                    item = bottomNavigationView.getMenu().findItem(R.id.menu_expenses);
                } else if (position == 3) {
                    item = bottomNavigationView.getMenu().findItem(R.id.menu_report);
                } else if (position == 4) {
                    item = bottomNavigationView.getMenu().findItem(R.id.menu_profile);
                } else if (position == 5) {
                    item = bottomNavigationView.getMenu().findItem(R.id.menu_setting);
                } else {
                    item = bottomNavigationView.getMenu().findItem(R.id.menu_home);
                }
                if (item != null) {
                    item.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int page = 0;
        if (menuItem.getItemId() == R.id.menu_home) {
            page = 0;
        } else if (menuItem.getItemId() == R.id.menu_budget) {
            page = 1;
        } else if (menuItem.getItemId() == R.id.menu_expenses) {
            page = 2;
        } else if (menuItem.getItemId() == R.id.menu_report) {
            page = 3;
        } else if (menuItem.getItemId() == R.id.menu_profile) {
            page = 4;
        } else if (menuItem.getItemId() == R.id.menu_setting) {
            page = 5;
        }
        viewPager2.setCurrentItem(page);
        drawerLayout.closeDrawer(GravityCompat.START); // Dong lai
        return true;
    }

    private boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void saveLoginState(String username) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Clear all prefs
        editor.apply();
        Log.d("HomeDebug", "Logged out, redirecting to Login");
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
