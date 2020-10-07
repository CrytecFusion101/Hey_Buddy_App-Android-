package com.heybuddy.ui.Dialog;

import android.content.Context;

import androidx.fragment.app.DialogFragment;

import com.heybuddy.activity.AppNavigationActivity;

public class BaseDialog extends DialogFragment {

    protected AppNavigationActivity appNavigationActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AppNavigationActivity) {
            appNavigationActivity = (AppNavigationActivity) context;
        }
    }

/*
    public void onError(Call<? extends BaseModel> responseCall, Throwable t) {
        ((AppNavigationActivity) getActivity()).hideProgressDialog();
        appNavigationActivity.showSnackBar(getView(), getString(R.string.something_went_wrong));
    }


    public void onFailure(BaseModel baseModel, int code) {
        if (getActivity() != null)
            ((AppNavigationActivity) getActivity()).hideProgressDialog();
        DebugLog.e("Error Code : " + code);
        if (baseModel != null) {
            appNavigationActivity.showSnackBar(getView(), baseModel.getMessage());
        } else {
            appNavigationActivity.showSnackBar(getView(), getString(R.string.something_went_wrong));
        }

    }*/

}
