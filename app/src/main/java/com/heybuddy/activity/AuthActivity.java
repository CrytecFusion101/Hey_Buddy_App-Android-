package com.heybuddy.activity;

import android.os.Bundle;

import com.heybuddy.R;
import com.heybuddy.enumeration.BackStack;
import com.heybuddy.enumeration.FragmentState;

public class AuthActivity extends AppNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openLoginSelactionFragment(FragmentState.REPLACE, BackStack.YES);
//        openChatFragment(FragmentState.REPLACE);

    }

    @Override
    public void onBackPressed() {
        hideKeyBoard();
        int i = getSupportFragmentManager().getBackStackEntryCount();
        if (i > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            this.finish();
        }
    }

}
