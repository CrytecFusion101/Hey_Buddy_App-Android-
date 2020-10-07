package com.heybuddy.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.heybuddy.R;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecentChatHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.llRow)
    public RelativeLayout llRow;

    @BindView(R.id.txtName)
    public TextView txtName;

    @BindView(R.id.txtMessage)
    public TextView txtMessage;

    @BindView(R.id.imgProfile)
    public RoundedImageView imgProfile;

   @BindView(R.id.imgOnline)
    public ImageView imgOnline;


    public RecentChatHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }
}
