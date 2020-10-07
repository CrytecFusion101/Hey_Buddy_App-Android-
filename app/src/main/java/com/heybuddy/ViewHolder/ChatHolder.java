package com.heybuddy.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.heybuddy.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.txtMessage)
    public TextView txtMessage;

    public ChatHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        txtMessage = (TextView)itemView.findViewById(R.id.txtMessage);

    }
}
