package com.heybuddy.ui.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.heybuddy.R;
import com.heybuddy.base.BaseFragment;
import com.heybuddy.ui.Presenter.GetStartPresenter;
import com.heybuddy.ui.view.GetStartView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GetStartFragment extends BaseFragment<GetStartView, GetStartPresenter> implements GetStartView {


    @BindView(R.id.btnLetsGo)
    Button btnLetsGo;

    public static GetStartFragment newInstance() {
        return new GetStartFragment();
    }

    @Override
    public GetStartPresenter initView() {
        return new GetStartPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.get_start_fragment, container, false);
        ButterKnife.bind(view);


        btnLetsGo = view.findViewById(R.id.btnLetsGo);

        btnLetsGo.setOnClickListener(view1 -> appNavigationActivity.openHomeActivity()/*appNavigationActivity.openRecentChatListFragment(FragmentState.REPLACE, BackStack.YES)*/);
        return view;
    }
}
