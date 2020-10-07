package com.heybuddy.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.heybuddy.base.BaseFragment;

import java.util.List;

public class LoginSelectionVpagerAdapter extends FragmentStatePagerAdapter {

    public LoginSelectionVpagerAdapter(FragmentManager fm, List<BaseFragment> baseFragments) {
        super(fm);
        this.baseFragments = baseFragments;
    }

    List<BaseFragment> baseFragments;

    @Override
    public Fragment getItem(int i) {
        return baseFragments.get(i);
    }

    @Override
    public int getCount() {
        return baseFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position + "";
    }
}
