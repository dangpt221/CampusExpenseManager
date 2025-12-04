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
    private static final String KEY_USER_ID = "ID_USER";
    private static final String TAG = "HomeActivity";

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
            Log.d(TAG, "Not logged in, redirecting to Login");
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

        // Retrieve username from prefs
        String username = prefs.getString(KEY_USERNAME, "");
        int userId = prefs.getInt(KEY_USER_ID, 0);

        Log.d(TAG, "From SharedPreferences - Username: " + username + ", UserId: " + userId);

        // Nếu không có trong prefs, lấy từ intent (first time login)
        if (TextUtils.isEmpty(username) || userId == 0) {
            Intent intentHome = getIntent();
            Bundle bundleHome = intentHome.getExtras();
            if (bundleHome != null) {
                username = bundleHome.getString("ACCOUNT", "");
                userId = bundleHome.getInt("ID_USER", 0);
                String email = bundleHome.getString("EMAIL", "");
                int role = bundleHome.getInt("ROLE_USER", 0);

                if (!TextUtils.isEmpty(username) && userId > 0) {
                    saveLoginState(username, email, userId, role);
                    Log.d(TAG, "Saved user from intent: " + username + " (ID: " + userId + ")");
                }
            }
        }

        Log.d(TAG, "Final Username: '" + username + "', UserId: " + userId + ", IsLoggedIn: " + isLoggedIn());

        if (TextUtils.isEmpty(username) || userId == 0 || !isLoggedIn()) {
            Log.d(TAG, "Redirecting to Login: Missing data");
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Setup drawer menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Setup ViewPager
        setupViewPager();

        // Setup bottom navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int page = 0;
            if (item.getItemId() == R.id.menu_home) {
                page = 0;
            } else if (item.getItemId() == R.id.menu_express) {
                page = 1;
            } else if (item.getItemId() == R.id.menu_budget) {
                page = 2;
            } else if (item.getItemId() == R.id.menu_reportbudget) {
                page = 3;
            } else if (item.getItemId() == R.id.menu_profile) {
                page = 4;
            } else if (item.getItemId() == R.id.menu_setting) {
                page = 5;
            }
            viewPager2.setCurrentItem(page);
            return true;
        });

        // Setup logout
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
                return true;
            }
        });
    }

    private void setupViewPager() {
        ViewPagerApdapter adapter = new ViewPagerApdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(adapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                MenuItem item = null;
                switch (position) {
                    case 0:
                        item = bottomNavigationView.getMenu().findItem(R.id.menu_home);
                        break;
                    case 1:
                        item = bottomNavigationView.getMenu().findItem(R.id.menu_express);
                        break;
                    case 2:
                        item = bottomNavigationView.getMenu().findItem(R.id.menu_budget);
                        break;
                    case 3:
                        item = bottomNavigationView.getMenu().findItem(R.id.menu_reportbudget);
                        break;
                    case 4:
                        item = bottomNavigationView.getMenu().findItem(R.id.menu_profile);
                        break;
                    case 5:
                        item = bottomNavigationView.getMenu().findItem(R.id.menu_setting);
                        break;
                }
                if (item != null) {
                    item.setChecked(true);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int page = 0;
        if (menuItem.getItemId() == R.id.menu_home) {
            page = 0;
        } else if (menuItem.getItemId() == R.id.menu_express) {
            page = 1;
        } else if (menuItem.getItemId() == R.id.menu_budget) {
            page = 2;
        } else if (menuItem.getItemId() == R.id.menu_reportbudget) {
            page = 3;
        } else if (menuItem.getItemId() == R.id.menu_profile) {
            page = 4;
        } else if (menuItem.getItemId() == R.id.menu_setting) {
            page = 5;
        }
        viewPager2.setCurrentItem(page);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void saveLoginState(String username, String email, int userId, int role) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString("email", email);
        editor.putInt(KEY_USER_ID, userId);
        editor.putInt("role", role);
        editor.apply();

        Log.d(TAG, "Saved login state - ID_USER: " + userId);
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        Log.d(TAG, "Logged out, redirecting to Login");
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    // --- Thêm hàm này vào cuối file HomeActivity.java ---
    public void goToHome() {
        if (viewPager2 != null) {
            viewPager2.setCurrentItem(0); // 0 là vị trí tab Trang chủ
        }
    }
}