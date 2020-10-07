package com.heybuddy.base;

/**
 * Created by Dhara on 01/01/2019.
 */

public abstract class BasePresenter<V extends BaseView> {
    public V baseView;

    public BasePresenter(V mView) {
        this.baseView = mView;
    }

   /* public abstract void start();

    public abstract void stop();

    public abstract void detechView();*/




}
