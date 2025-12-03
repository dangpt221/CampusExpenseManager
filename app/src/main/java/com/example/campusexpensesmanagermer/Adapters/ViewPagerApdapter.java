package com.example.campusexpensesmanagermer.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.campusexpensesmanagermer.Fragments.BudgetFragment;
import com.example.campusexpensesmanagermer.Fragments.ExpressFragment;
import com.example.campusexpensesmanagermer.Fragments.HomeFragment;
import com.example.campusexpensesmanagermer.Fragments.SettingFragment;
import com.example.campusexpensesmanagermer.Fragments.ProfileFragment;
import com.example.campusexpensesmanagermer.Fragments.ReportFragment;


public class ViewPagerApdapter extends FragmentStateAdapter {
    public ViewPagerApdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            return new HomeFragment();
        } else if (position ==1) {
            return new ExpressFragment();
        } else if (position == 2) {
            return new BudgetFragment();
        } else if (position == 3) {
            return new ReportFragment();
        } else if (position == 4) {
            return new ProfileFragment();
        } else if (position == 5) {
            return new SettingFragment();
        }else {
            return new HomeFragment();
        }

    }

    @Override
    public int getItemCount() {
        return 6;
    }
}

