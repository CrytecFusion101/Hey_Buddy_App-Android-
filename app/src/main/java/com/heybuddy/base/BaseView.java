package com.heybuddy.base;

/**
 * Created by Dhara on 01/01/2019.
 */

public interface BaseView<P extends BasePresenter> {

    void showSnackBar(String str);

    void showToast(String str);

    void hideKeyBoard();


  /*  void onFailure(BaseModel baseModel, int code);

    void onError(Call<? extends BaseModel> responseCall, Throwable t);

    void hideKeyBoard();*/
}
