package com.heybuddy.ui.Dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.heybuddy.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AlertDialog extends DialogFragment {


    @BindView(R.id.txtAlertMessage)
    TextView txtMessage;
    @BindView(R.id.btnok)
    Button btnok;
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public static AlertDialog newInstance(String message) {
        AlertDialog fragment = new AlertDialog();
        fragment.setMessage(message);
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
        View view = inflater.inflate(R.layout.alert_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        ButterKnife.bind(this, view);
        txtMessage.setText(message);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
    }

    private void initUI() {

    }

    @OnClick({R.id.btnok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnok:
                dismiss();
                break;

        }
    }
}
