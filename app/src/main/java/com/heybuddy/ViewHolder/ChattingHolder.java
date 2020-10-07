package com.heybuddy.ViewHolder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.ButterKnife;

public class ChattingHolder extends RecyclerView.ViewHolder  {


    public ChattingHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
