package com.heybuddy.listener;

import com.heybuddy.Model.UserData;
import com.heybuddy.enumeration.BackStack;
import com.heybuddy.enumeration.FragmentState;

public interface iNavigator {
    void showProgressDialog();

    void hideProgressDialog();

    void openHomeActivity();

    void openAuthActivity();

    void openLoginFragment(FragmentState fragmentState, String loginType, BackStack backStack);

    void openChatFragment(FragmentState fragmentState, BackStack backStack, UserData userDetail);

    void openRegistrationFragment(FragmentState fragmentState, BackStack backStack);

    void openRecentChatListFragment(FragmentState fragmentState, BackStack backStack);

    void openLoginSelactionFragment(FragmentState fragmentState, BackStack backStack);

    void openGetStartFragment(FragmentState fragmentState, BackStack backStack);

    void openChatScreen(FragmentState fragmentState, BackStack backStack);

    void openAlertDialog(String message);

    void openConfirmationDialog(String message, String positiveText, String nagativeText, iDialogButtonClick callback);
}
