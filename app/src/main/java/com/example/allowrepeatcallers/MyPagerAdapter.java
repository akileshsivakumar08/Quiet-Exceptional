package com.example.allowrepeatcallers;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import AlertMissedCalls.FragmentAlertMissedCalls;

public class MyPagerAdapter extends FragmentStateAdapter {
    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentPrioContacts();

            case 1:
                return new FragmentAlertMissedCalls();
            case 2:
                return new FragmentQuickSwitch();
            case 3:
                return new FragmentAllowRepeatCallers();
            case 4:
                return new FragmentSMSAlarm();
            default:
                throw new IllegalArgumentException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
