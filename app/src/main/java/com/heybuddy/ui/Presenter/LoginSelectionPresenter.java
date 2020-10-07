package com.heybuddy.ui.Presenter;

import com.heybuddy.base.BaseFragment;
import com.heybuddy.base.BasePresenter;
import com.heybuddy.ui.Fragment.loginMessageVPagerFragment;
import com.heybuddy.ui.view.LoginSelectionView;

import java.util.ArrayList;
import java.util.List;

public class LoginSelectionPresenter extends BasePresenter<LoginSelectionView> {

    public LoginSelectionPresenter(LoginSelectionView mView) {
        super(mView);
    }


    public List<BaseFragment> getFragmentList() {

        List<BaseFragment> list = new ArrayList<>();
        list.add(loginMessageVPagerFragment.newInstance("Feeling Low?", "Relax. Have a chat with your buddy."));
        list.add(loginMessageVPagerFragment.newInstance("Fretting over something?", "Breathe in.out.Your buddy will sort you out."));
        list.add(loginMessageVPagerFragment.newInstance("Need your Wise owl?", "Sit in.Discuss. ANYTHING."));
        list.add(loginMessageVPagerFragment.newInstance("Hey Buddy!", "I am here for you. ALWAYS."));
        return list;
    }
}
