package com.example.campusexpensesmanagermer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    ViewPager2 viewPager2;
    Toolbar toolbar;
    NavigationView navigationView;
    Intent intentHome;
    Bundle bundleHome;
    String username;

    @Override
    protected void onStart() {
        super.onStart();
        if (bundleHome == null || username == null){
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        viewPager2 = findViewById(R.id.viewPager);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);
        intentHome = getIntent();
        bundleHome = intentHome.getExtras();
        if (bundleHome != null) {
            username = bundleHome.getString("ACCOUNT","");
        }
        // xu ly hien thi drawer menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        // Xu ly khi bam menu bottom navigation
        setupViewPager();
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home){
                viewPager2.setCurrentItem(0);
            } else if (item.getItemId() == R.id.menu_budget) {
                viewPager2.setCurrentItem(1);
            } else if (item.getItemId() == R.id.menu_expenses) {
                viewPager2.setCurrentItem(2);
            } else if (item.getItemId() == R.id.menu_report) {
                viewPager2.setCurrentItem(3);
            } else if (item.getItemId() == R.id.menu_profile) {
                viewPager2.setCurrentItem(5);
            } else if (item.getItemId() == R.id.menu_setting) {
                viewPager2.setCurrentItem(6);
            } else {
                viewPager2.setCurrentItem(0);
            }
            return true ;
        });
        // xu ly logout App
        Menu menu = navigationView.getMenu();
        MenuItem logout = menu.findItem(R.id.menu_Logout);
        MenuItem account = menu.findItem(R.id.tvAccount);
        if (!TextUtils.isEmpty(username)){
            account.setTitle("Hi: "+ username);
        }
        logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);
                //quay ve tran login
                if (bundleHome != null){
                    intentHome.removeExtra("ACCOUNT");
                    intentHome.removeExtra("EMAIL");
                    intentHome.removeExtra("ID_USER");
                    intentHome.removeExtra("AGe_USER");
                }
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                finish();
                return false;
            }
        });
    }
    private void setupViewPager(){
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
                if (position == 0) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                } else if (position == 1) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_budget).setChecked(true);
                } else if (position == 2) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_expenses).setChecked(true);
                } else if (position == 3) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_report).setChecked(true);
                } else if (position == 4) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_profile).setChecked(true);
                } else if (position == 5) {
                    bottomNavigationView.getMenu().findItem(R.id.menu_setting).setChecked(true);
                } else  {
                    bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
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
        if (menuItem.getItemId() == R.id.menu_home){
            viewPager2.setCurrentItem(0);
        } else if ( menuItem.getItemId() == R.id.menu_budget) {
            viewPager2.setCurrentItem(1);
        } else if (menuItem.getItemId() == R.id.menu_expenses) {
            viewPager2.setCurrentItem(2);
        } else if (menuItem.getItemId() == R.id.menu_report) {
            viewPager2.setCurrentItem(3);
        } else if (menuItem.getItemId() == R.id.menu_profile) {
            viewPager2.setCurrentItem(4);
        } else if (menuItem.getItemId() == R.id.menu_setting){
            viewPager2.setCurrentItem(5);
        }
        drawerLayout.closeDrawer(GravityCompat.START);// dong laij
        return true;
    }
}