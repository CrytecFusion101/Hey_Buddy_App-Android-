package com.heybuddy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.animation.Animation;

import com.heybuddy.Controller;
import com.heybuddy.Model.UserData;
import com.heybuddy.R;
import com.heybuddy.constant.DbConstant;
import com.heybuddy.enumeration.BackStack;
import com.heybuddy.enumeration.FragmentState;
import com.heybuddy.listener.iDialogButtonClick;
import com.heybuddy.listener.iNavigator;
import com.heybuddy.ui.Dialog.AlertDialog;
import com.heybuddy.ui.Dialog.ConfirmationDialog;
import com.heybuddy.ui.Fragment.ChatFragment;
import com.heybuddy.ui.Fragment.ChatScreen;
import com.heybuddy.ui.Fragment.GetStartFragment;
import com.heybuddy.ui.Fragment.LoginFragment;
import com.heybuddy.ui.Fragment.LoginSelactionFragment;
import com.heybuddy.ui.Fragment.UsersListFragment;
import com.heybuddy.ui.Fragment.RegistrationFragment;
import com.heybuddy.utility.AppHelper;
import com.heybuddy.utility.PreferanceHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AppNavigationActivity extends BaseActivity implements iNavigator {

    private ProgressDialog dialog;

    @Override
    public void showProgressDialog() {
        if (dialog == null) {
            dialog = ProgressDialog.show(this, null, null, true);
            dialog.setContentView(R.layout.progressbar);
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        } else {
            dialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    @Override
    public void openHomeActivity() {
        Intent i = new Intent(this, HomeActivity.class);
        this.overridePendingTransition(0, 0);
        startActivity(i);
        finish();
    }

    @Override
    public void openAuthActivity() {
        Intent i = new Intent(this, AuthActivity.class);
        this.overridePendingTransition(Animation.ZORDER_NORMAL, 0);
        startActivity(i);
        finish();
    }

    public void logout() {
  /*      FirebaseFirestore.getInstance()
                .collection(DbConstant.USERS)
                .document(AppHelper.getInstance().getUid())
                .update(DbConstant.ONLINE, "false")
                .addOnSuccessListener(aVoid -> {
                    PreferanceHelper.getInstance().clearPreference();
                    FirebaseAuth.getInstance().signOut();
                    openAuthActivity();
                })
                .addOnFailureListener(e -> {
                    openAlertDialog(e.getMessage());
                });*/


        try {
            showProgressDialog();

            DocumentReference reference = FirebaseFirestore.getInstance().collection(DbConstant.USERS).document(AppHelper.getInstance().getUid());
            Map<String, Object> map = new HashMap<>();
            map.put(DbConstant.DEVICE_TOKEN, "");
            reference.update(map)
                    .addOnSuccessListener(aVoid -> {
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child(DbConstant.USER_STATUS)
                                .child(AppHelper.getInstance().getUid())
                                .child(DbConstant.ONLINE)
                                .setValue(ServerValue.TIMESTAMP)
                                .addOnSuccessListener(task -> {
                                    hideProgressDialog();
                                    Controller.getInstance().cancelDisconnectListner();
                                    PreferanceHelper.getInstance().clearPreference();
                                    FirebaseAuth.getInstance().signOut();
                                    openAuthActivity();
                                })
                                .addOnFailureListener(e -> {
                                    hideProgressDialog();
                                    openAlertDialog(e.getMessage());
                                });

                    })
                    .addOnFailureListener(e -> {
                        hideProgressDialog();
                        openAlertDialog(e.getMessage());
                    });

        } catch (Exception e) {
            hideProgressDialog();
            e.printStackTrace();
        }
    }

    public void afterLogoutAction() {
        PreferanceHelper.getInstance().clearPreference();
        FirebaseAuth.getInstance().signOut();
        openAuthActivity();
    }

    @Override
    public void openLoginFragment(FragmentState fragmentState, String loginType, BackStack backStack) {
        fragmentChange(LoginFragment.newInstance(loginType), fragmentState, backStack);
    }


    @Override
    public void openRegistrationFragment(FragmentState fragmentState, BackStack backStack) {
        fragmentChange(RegistrationFragment.newInstance(), fragmentState, backStack);
    }

    @Override
    public void openRecentChatListFragment(FragmentState fragmentState, BackStack backStack) {
        fragmentChange(UsersListFragment.newInstance(), fragmentState, backStack);
    }

    @Override
    public void openLoginSelactionFragment(FragmentState fragmentState, BackStack backStack) {
        fragmentChange(LoginSelactionFragment.newInstance(), fragmentState, backStack);
    }

    @Override
    public void openGetStartFragment(FragmentState fragmentState, BackStack backStack) {
        fragmentChange(GetStartFragment.newInstance(), fragmentState, backStack);
    }

    @Override
    public void openChatFragment(FragmentState fragmentState, BackStack backStack, UserData userDetail) {
        fragmentChange(ChatFragment.newInstance(userDetail), fragmentState, backStack);
    }

    @Override
    public void openChatScreen(FragmentState fragmentState, BackStack backStack) {
        fragmentChange(ChatScreen.newInstance(), fragmentState, backStack);
    }

    @Override
    public void openAlertDialog(String message) {
        AlertDialog.newInstance(message).show(getSupportFragmentManager(), AlertDialog.class.getSimpleName());
    }


    @Override
    public void openConfirmationDialog(String message, String positiveText, String nagativeText, iDialogButtonClick callback) {
        ConfirmationDialog.newInstance(callback, message, positiveText, nagativeText).show(getSupportFragmentManager(), ConfirmationDialog.class.getSimpleName());
    }

}
