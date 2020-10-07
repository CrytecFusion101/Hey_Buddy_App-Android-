package com.heybuddy.ui.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.heybuddy.R;
import com.heybuddy.base.BaseFragment;
import com.heybuddy.ui.Presenter.LoginSelectionPresenter;
import com.heybuddy.ui.view.LoginSelectionView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class loginMessageVPagerFragment extends BaseFragment<LoginSelectionView, LoginSelectionPresenter> implements LoginSelectionView {

    Unbinder unbinder;
    private String title, message;
    @BindView(R.id.txtTitle)
    AppCompatTextView txtTitle;
    @BindView(R.id.txtMessage)
    AppCompatTextView txtMessage;

    private void setTitle(String title) {
        this.title = title;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public static loginMessageVPagerFragment newInstance(String title, String message) {
        loginMessageVPagerFragment fragment = new loginMessageVPagerFragment();
        fragment.setTitle(title);
        fragment.setMessage(message);
        return fragment;
    }

    @Override
    public LoginSelectionPresenter initView() {
        return new LoginSelectionPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_message_vpager_dialog, container, false);
        super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtTitle.setText(title);
        txtMessage.setText(message);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
