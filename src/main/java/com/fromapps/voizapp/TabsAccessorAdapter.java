package com.fromapps.voizapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
//import androidx.fragment.app.FragmentStatePagerAdapter;
//import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }



    @Override
    public Fragment getItem(int i) {

        switch (i){
            case 0:
                VChatsFragment VChatsFragment=new VChatsFragment();
                return VChatsFragment;
            case 1:
                VGroupsFragment VGroupsFragment=new VGroupsFragment();
                return VGroupsFragment;
            case 2:
                VContactsFragment VContactsFragment=new VContactsFragment();
                return VContactsFragment;
            case 3:
                VRequestFragment VRequestFragment=new VRequestFragment();
                return VRequestFragment;
            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Talks";
            case 1:
                return "Groups";
            case 2:
                return "Contacts";
            case 3:
                return "Requests";
            default:
                return null;
        }

    }
}
