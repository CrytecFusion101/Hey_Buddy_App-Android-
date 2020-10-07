package com.heybuddy.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brandongogetap.stickyheaders.exposed.StickyHeaderHandler;
import com.heybuddy.Model.ChatMessage;
import com.heybuddy.R;
import com.heybuddy.enumeration.MessageType;
import com.heybuddy.listener.BottomReachedListener;
import com.heybuddy.utility.AppHelper;
import com.heybuddy.utility.DebugLog;
import com.heybuddy.utility.Util;

import java.util.List;

import static android.view.LayoutInflater.from;

final public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements StickyHeaderHandler {


    private List<ChatMessage> items;
    private BottomReachedListener bottomReachedListener;

    public ChatAdapter(List<ChatMessage> data) {
        this.items = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MessageType.SENDER.getId()) {
            View view = from(parent.getContext()).inflate(R.layout.row_chat_right, parent, false);
            return new SenderHolder(view);

        } else if (viewType == MessageType.RECEIVER.getId()) {
            View view = from(parent.getContext()).inflate(R.layout.row_chat_left, parent, false);
            return new ReceiverViewHolder(view);

        } else if (viewType == MessageType.HEADER.getId()) {
            View header = from(parent.getContext()).inflate(R.layout.in_chat_header_date, parent, false);
            return new HeaderViewHolder(header);

        } else {
            View header = from(parent.getContext()).inflate(R.layout.row_note, parent, false);
            return new NoteViewHeader(header);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        bottomReachedListener.onBottomReached(position >= items.size() - 2);
        ChatMessage item = items.get(position);
        switch (getItemViewType(position)) {
            case 4:
                ((NoteViewHeader) holder).onBind(item);
                break;
            case 3:
                ((HeaderViewHolder) holder).onBind(item);
                break;
            case 2:
                ((ReceiverViewHolder) holder).onBind(item);
                break;
            case 1:
                ((SenderHolder) holder).onBind(item);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getMessageType().getId();
    }

    @Override
    public List<?> getAdapterData() {
        return items;
    }


    public void setItems(List<ChatMessage> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    public ChatMessage getItem(int position) {
        if (position > -1 && position < items.size())
            return items.get(position);
        else return null;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView txtHeader;

        HeaderViewHolder(View itemView) {
            super(itemView);
            txtHeader = itemView.findViewById(R.id.txtHeaderDate);
        }

        void onBind(ChatMessage data) {
            DebugLog.d("Date::::" + AppHelper.getInstance().getFormattedDate(Util.parseLong(data.getTimestamp())));
            txtHeader.setText(AppHelper.getInstance().getFormattedDate(Util.parseLong(data.getTimestamp())));
        }
    }

    static class NoteViewHeader extends RecyclerView.ViewHolder {
        TextView txtMsg;
        TextView txtTime;

        NoteViewHeader(View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
        }

        void onBind(ChatMessage item) {
//            txtTime.setText(item.getFormattedTime());
            txtMsg.setText(item.getMessage());
        }
    }


    static class SenderHolder extends RecyclerView.ViewHolder {
        TextView txtMsg;
//        TextView txtTime;

        SenderHolder(View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.txtMessage);
//            txtTime = itemView.findViewById(R.id.txtTime);
        }

        void onBind(ChatMessage item) {
//            txtTime.setText(item.getFormattedTime());
            txtMsg.setText(item.getMessage());

//                switch (item.getMessageState()) {
//                    case PENDING:
//                        imgMessageStatus.setImageResource(R.drawable.ic_pending);
//                        break;
//                    case SENT:
//                        imgMessageStatus.setImageResource(R.drawable.ic_sent);
//                        break;
//                    case RECEIVED:
//                        imgMessageStatus.setImageResource(R.drawable.ic_received);
//                        break;
//                    case READ:
//                        imgMessageStatus.setImageResource(R.drawable.ic_read);
//                        break;
//                    case ERROR:
//                        imgMessageStatus.setImageResource(R.drawable.ic_info_chat);
//                        break;
//                }

        }
    }

    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView txtMsg;
//        TextView txtTime;

        ReceiverViewHolder(View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.txtMessage);
//            txtTime = itemView.findViewById(R.id.txtTime);
        }

        void onBind(ChatMessage item) {
//            txtTime.setText(item.getFormattedTime());
            txtMsg.setText(item.getMessage());
        }
    }


    public void setBottomReachedListener(BottomReachedListener bottomReachedListener) {
        this.bottomReachedListener = bottomReachedListener;
    }
}
