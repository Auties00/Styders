package it.auties.styders.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import it.auties.styders.fragment.HomeFragment;
import it.auties.styders.fragment.NotchFragment;
import it.auties.styders.fragment.SettingsFragment;
import it.auties.styders.fragment.TimerFragment;
import it.auties.styders.main.MainActivity;

public class PageAdapter extends FragmentPagerAdapter {
    private final MainActivity mainActivity;
    private final int numOfTabs;

    public PageAdapter(MainActivity mainActivity, FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
        this.mainActivity = mainActivity;
    }

    @Override
    @NonNull
    public Fragment getItem(int position) {
        switch (position) {
            default:
            case 0:
                return new HomeFragment(mainActivity);
            case 1:
                return new NotchFragment();
            case 2:
                return new TimerFragment(mainActivity);
            case 3:
                return new SettingsFragment(mainActivity);
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

}