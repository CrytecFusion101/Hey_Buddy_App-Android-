package com.heybuddy.ui.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.heybuddy.R;
import com.heybuddy.adapter.LoginSelectionVpagerAdapter;
import com.heybuddy.base.BaseFragment;
import com.heybuddy.constant.AppConstant;
import com.heybuddy.enumeration.BackStack;
import com.heybuddy.enumeration.FragmentState;
import com.heybuddy.ui.Presenter.LoginSelectionPresenter;
import com.heybuddy.ui.view.LoginSelectionView;
import com.pixelcan.inkpageindicator.InkPageIndicator;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginSelactionFragment extends BaseFragment<LoginSelectionView, LoginSelectionPresenter> implements LoginSelectionView {


    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.indicator)
    InkPageIndicator indicator;
    @BindView(R.id.btnNeedaBuddy)
    Button btnNeedaBuddy;
    @BindView(R.id.btnBeaBuddy)
    Button btnBeaBuddy;
    private int currentPage = 0;

    Timer swipeTimer;
    Handler handler;
    Runnable Update;

    public static LoginSelactionFragment newInstance() {
        return new LoginSelactionFragment();
    }

    @Override
    public LoginSelectionPresenter initView() {
        return new LoginSelectionPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.login_selection_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void startSwipTimer() {
        handler = new Handler();
        Update = new Runnable() {
            public void run() {
                if (currentPage == presenter.getFragmentList().size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2000, 2000);
    }


    private void stopSwipTimer() {
        swipeTimer.cancel();
        if (handler != null)
            handler.removeCallbacks(Update);

    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        stopSwipTimer();
    }
    @Override
    public void onStop() {
        super.onStop();
        stopSwipTimer();
    }*/

    @Override
    public void onPause() {
        super.onPause();
        stopSwipTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        startSwipTimer();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewPager();
    }

    private void setViewPager() {

        viewPager.setAdapter(new LoginSelectionVpagerAdapter(getChildFragmentManager(), presenter.getFragmentList()));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                appNavigationActivity.hideKeyBoard();

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                DebugLog.d("ncdncdnxcn dxn :: " + state);

            }
        });

        indicator.setViewPager(viewPager);
    }

    @OnClick({R.id.btnNeedaBuddy, R.id.btnBeaBuddy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnNeedaBuddy:
                appNavigationActivity.openLoginFragment(FragmentState.REPLACE, AppConstant.NEEDABUDY, BackStack.YES);
                break;
            case R.id.btnBeaBuddy:
                appNavigationActivity.openLoginFragment(FragmentState.REPLACE, AppConstant.BEABUDDY, BackStack.YES);
                break;
        }
    }
}


