package com.heybuddy.base;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.heybuddy.activity.AppNavigationActivity;
import com.heybuddy.activity.AuthActivity;
import com.heybuddy.activity.BaseActivity;
import com.heybuddy.activity.HomeActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dhara on 01/01/2019.
 */
public abstract class BaseFragment<V extends BaseView, P extends BasePresenter<V>> extends Fragment implements BaseView<P> {
    public P presenter;
    protected BaseActivity baseActivity;
    private ConnectivityManager connectivityManager;
    protected HomeActivity homeActivity;
    protected AuthActivity authActivity;
    protected AppNavigationActivity appNavigationActivity;
    protected int networkConnectionCheckerCounter = 0;
    protected Boolean isNetworkPopupShowing = false;

    public abstract P initView();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initView();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            baseActivity = (BaseActivity) context;
        }
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
        if (context instanceof AuthActivity) {
            authActivity = (AuthActivity) context;
        }
        if (context instanceof AppNavigationActivity) {
            appNavigationActivity = (AppNavigationActivity) context;
        }
    }

    public boolean isNetworkAvailable(View view) {
        boolean connected = false;
        try {
            connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
            if (!connected) {
//                openAlertDialog();
//                showSnackbar(view, getString(R.string.please_check_internet_connectivity));
            }
            return connected;
        } catch (Exception e) {
            Log.e("connectivity", e.toString());
        }
        return connected;
    }


    protected void showSnackbar(View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        snackbar.show();

    }


    protected List<String> getDummyList() {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            stringList.add("Test" + i);
        }
        return stringList;
    }

   /* protected List<ChatMessage> getDummyChatList() {
        List<ChatMessage> chats = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            ChatMessage message = new ChatMessage();
            message.setMessage("Hello, How are you?");
            if (i % 2 == 0) {
                message.setMessageType(MessageType.RECEIVER);
            } else {
                message.setMessageType(MessageType.SENDER);
            }
            chats.add(message);
        }
        return chats;
    }*/


    @Override
    public void showSnackBar(String str) {
        if (baseActivity != null)
            baseActivity.showSnackBar(getView(), str);
    }

    @Override
    public void showToast(String str) {

    }

    @Override
    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }


}
