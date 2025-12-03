package com.example.campusexpensesmanagermer.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.campusexpensesmanagermer.Fragments.BudgetFragment;
import com.example.campusexpensesmanagermer.Fragments.ExpressFragment;
import com.example.campusexpensesmanagermer.Fragments.HomeFragment;
import com.example.campusexpensesmanagermer.Fragments.ProfileFragment;
import com.example.campusexpensesmanagermer.Fragments.ReportFragment;
import com.example.campusexpensesmanagermer.Fragments.SettingFragment;

public class ViewPagerApdapter extends FragmentStateAdapter {

    // Constructor PHẢI CÓ cả 2 tham số
    public ViewPagerApdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new ExpressFragment();
            case 2:
                return new BudgetFragment();
            case 3:
                return new ReportFragment();
            case 4:
                return new ProfileFragment();
            case 5:
                return new SettingFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}