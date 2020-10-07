package com.heybuddy.ui.Dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.heybuddy.R;
import com.heybuddy.activity.HomeActivity;
import com.heybuddy.listener.iDialogButtonClick;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class ConfirmationDialog extends BaseDialog {

    Unbinder unbinder;
    @BindView(R.id.txtAlertMessage)
    AppCompatTextView txtAlertMessage;
    @BindView(R.id.btnYes)
    AppCompatButton btnYes;
    @BindView(R.id.btnNo)
    AppCompatButton btnNo;
    private HomeActivity homeActivity;
    private iDialogButtonClick callBack;
    private String positiveText;
    private String nagativeText;
    private String message;

    public void setPositiveText(String positiveText) {
        this.positiveText = positiveText;
    }

    public void setNagativeText(String nagativeText) {
        this.nagativeText = nagativeText;
    }

    public void setCallBack(iDialogButtonClick callBack) {
        this.callBack = callBack;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ConfirmationDialog newInstance(iDialogButtonClick callBack, String message, String positiveText, String nagativeText) {
        ConfirmationDialog fragment = new ConfirmationDialog();
        fragment.setMessage(message);
        fragment.setCallBack(callBack);
        fragment.setPositiveText(positiveText);
        fragment.setNagativeText(nagativeText);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirmation, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) context;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        txtAlertMessage.setText(message);
        btnNo.setText(nagativeText);
        btnYes.setText(positiveText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btnYes, R.id.btnNo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnYes:
                callBack.positiveClick();
                dismiss();
                break;
            case R.id.btnNo:
                callBack.negativeClick();
                dismiss();
                break;
        }
    }
}
