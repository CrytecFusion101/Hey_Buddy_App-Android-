package com.heybuddy.ui.Fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.heybuddy.Controller;
import com.heybuddy.Model.ChatMessage;
import com.heybuddy.Model.UserData;
import com.heybuddy.R;
import com.heybuddy.adapter.ChatAdapter;
import com.heybuddy.base.BaseFragment;
import com.heybuddy.constant.AppConstant;
import com.heybuddy.constant.DbConstant;
import com.heybuddy.enumeration.MessageType;
import com.heybuddy.listener.BackPressListener;
import com.heybuddy.listener.BottomReachedListener;
import com.heybuddy.ui.Presenter.ChatPresenter;
import com.heybuddy.ui.view.ChatView;
import com.heybuddy.utility.AppHelper;
import com.heybuddy.utility.DebugLog;
import com.heybuddy.utility.KeyboardUtil;
import com.heybuddy.utility.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.vanniktech.emoji.EmojiPopup;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatFragment extends BaseFragment<ChatView, ChatPresenter> implements ChatView, BackPressListener, KeyboardVisibilityEventListener, BottomReachedListener, TextWatcher {

    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.imgProfile)
    RoundedImageView imgProfile;
    @BindView(R.id.txtTopbarName)
    TextView txtTopbarName;
    @BindView(R.id.txtTyping)
    TextView txtTyping;
    @BindView(R.id.edtMessage)
    EditText edtTypeMsg;
    @BindView(R.id.imgSend)
    ImageView imgSend;
    @BindView(R.id.rvChat)
    RecyclerView rvChat;
    @BindView(R.id.linBottom)
    LinearLayout linBottom;
    @BindView(R.id.txtHeaderDate)
    AppCompatTextView txtHeaderDate;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.txtNoUser)
    TextView txtNoUser;

    private boolean isKeyboardOpened;
    private static long mLastClickTime = 0;
    private ChatAdapter adapter;
    private EmojiPopup emojiPopup;
    private boolean isAtBottom = true;
    private DatabaseReference myDatabse;
    private UserData receiverUserDetail;
    private String mCurrent_user_id;
    private String send_to_user_id;
    private DatabaseReference mRootReference;
    public static final int TOTAL_ITEM_TO_LOAD = 10;
    private int mCurrentPage = 1;
    //Solution for descending list on refresh
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    String msgTypingStatus = AppConstant.NO;
    int typingMsgLength = 0;
    Handler handler;
    Thread thread;
    String type = "no";

    private List<ChatMessage> messagesList = new ArrayList<>();

    public ChatFragment() {
    }


    public void setUserDetail(UserData userDetail) {
        this.receiverUserDetail = userDetail;
        Controller.getInstance().setCurrentChatUser(receiverUserDetail.getUid());
    }

    public static ChatFragment newInstance(UserData userDetail) {
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setUserDetail(userDetail);
        return chatFragment;
    }

    @Override
    public ChatPresenter initView() {
        return new ChatPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.chat_fragment, container, false);
        ButterKnife.bind(this, view);
        emojiPopup = EmojiPopup.Builder.fromRootView(view).build(edtTypeMsg);

        //  remove notification
        NotificationManager notifManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtTypeMsg.addTextChangedListener(this);


        initUI();


        edtTypeMsg.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                msgTypingStatus = AppConstant.YES;
                handler = new Handler();
                thread = new Thread() {
                    public void run() {
                        if (edtTypeMsg.getText().toString().length() > 0) {
                            if (!type.equals("yes")) {
                                updateTypingStatus(true, "yes");
                            } else {
                            }
                        } else if (edtTypeMsg.getText().toString().length() == 0) {
                            if (!type.equals("no")) {
                                updateTypingStatus(false, "no");
                            } else {
                            }
                        }
                        handler.postDelayed(this, 5000);
                    }
                };
                thread.start();
            }
        });


        mRootReference = FirebaseDatabase.getInstance().getReference();

        mCurrent_user_id = AppHelper.getInstance().getUid();

        send_to_user_id = receiverUserDetail.getUid();

        initData(messagesList);

        getTypingStatus();

        loadAllMessage();
    }

    private void getTypingStatus() {
        FirebaseDatabase.getInstance().getReference(DbConstant.MESSAGES).child(compareUid()).child(DbConstant.TYPEING_STATUS).child(receiverUserDetail.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getActivity() == null || !isAdded()) return;

                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getValue().toString().equals(AppConstant.TRUE)) {
                        txtTyping.setVisibility(View.VISIBLE);
                        txtTyping.setText(getString(R.string.typing));
                    } else {
                        txtTyping.setVisibility(View.GONE);
                        txtTyping.setText(getString(R.string.online));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                DebugLog.d("Failed to read app title value." + error.toException());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Controller.getInstance().setCurrentChatUser("");
        FirebaseDatabase.getInstance().getReference(DbConstant.MESSAGES).child(compareUid()).child(DbConstant.TYPEING_STATUS).child(mCurrent_user_id).setValue(false);

        if (handler != null)
            handler.removeCallbacks(thread);
    }


    private String compareUid() {
        int result = mCurrent_user_id.compareTo(send_to_user_id);
        if (result < 0)
            return mCurrent_user_id + "-" + send_to_user_id;
        else
            return send_to_user_id + "-" + mCurrent_user_id;
    }

    private void initUI() {
        txtTopbarName.setText(receiverUserDetail.getUsername());
        Glide.with(baseActivity)
                .load(receiverUserDetail.getProfile())
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_add_image)
                .into(imgProfile);
    }


    private void initData(List<ChatMessage> messagesList) {

        this.messagesList = messagesList;
        if (adapter == null) {
            rvChat.setLayoutManager(new LinearLayoutManager(baseActivity));
            adapter = new ChatAdapter(messagesList);
            rvChat.setAdapter(adapter);
            rvChat.addOnScrollListener(new RecyclerViewScrollChange(txtHeaderDate));
            adapter.setBottomReachedListener(this);
//            updateBadge();
            if (messagesList.size() > 0) {
                rvChat.scrollToPosition(messagesList.size() - 1);
            }
        } else {
            adapter.setItems(messagesList);
            if (messagesList.size() > 0 && isAtBottom) {
                new Handler().postDelayed(() -> rvChat.scrollToPosition(messagesList.size() - 1), 100);
            }
        }
    }

    @Override
    public void onPause() {
        baseActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        homeActivity.setBackPressListener(null);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        baseActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        homeActivity.setBackPressListener(this);
        setKeyboardVisibilityListener();
    }

    private void setKeyboardVisibilityListener() {
        new Handler().postDelayed(() -> {
            try {
                KeyboardVisibilityEvent.setEventListener(homeActivity, ChatFragment.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 200);
    }

    @Override
    public void onVisibilityChanged(boolean isOpen) {
        isKeyboardOpened = isOpen;
        if (getActivity() == null || !isAdded()) return;
        if (isOpen) {
            if (messagesList != null) rvChat.scrollToPosition(messagesList.size() - 1);
        }
    }


    @OnClick({R.id.imgBack, R.id.imgSend, R.id.edtMessage})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                homeActivity.onBackPressed();
                break;
            case R.id.imgSend:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 400) return;
                mLastClickTime = SystemClock.elapsedRealtime();
                String message = edtTypeMsg.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage();
                }
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (emojiPopup.isShowing()) {
            emojiPopup.dismiss();
            return false;
        }
        if (isKeyboardOpened) {
            KeyboardUtil.hideKeyboard(edtTypeMsg);
            return false;
        }
        return true;
    }

    @Override
    public void onBottomReached(boolean isAtBottom) {
        this.isAtBottom = isAtBottom;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    private void sendMessage() {
        myDatabse = FirebaseDatabase.getInstance().getReference(DbConstant.MESSAGES).child(compareUid()).child(DbConstant.CHAT).child(String.valueOf(UUID.randomUUID()));

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put(DbConstant.SENDER_ID, mCurrent_user_id);
        userMap.put(DbConstant.SENDER_NAME, AppHelper.getInstance().getUsername());
        userMap.put(DbConstant.RECEIVER_ID, receiverUserDetail.getUid());
        userMap.put(DbConstant.RECEIVER_NAME, receiverUserDetail.getUsername());
        userMap.put(DbConstant.TEXT, edtTypeMsg.getText().toString());
        userMap.put(DbConstant.TEXT_TYPE, "text");
        userMap.put(DbConstant.TIMESTAMP, ServerValue.TIMESTAMP);
        userMap.put(DbConstant.MSG_STATUS, "read");


        myDatabse.child(mCurrent_user_id + " : User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myDatabse.updateChildren(userMap, (databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        baseActivity.sendPushNotification(receiverUserDetail, edtTypeMsg.getText().toString());
                        edtTypeMsg.setText("");
                        rvChat.scrollToPosition(messagesList.size() - 1);
//                        Toast.makeText(getActivity(), "Successfully Added chats feature", Toast.LENGTH_SHORT).show();
                    } else
                        DebugLog.e("Cannot add message to database");
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void updateTypingStatus(boolean isTyping, String istype) {
        type = istype;
        FirebaseDatabase.getInstance().getReference(DbConstant.MESSAGES).child(compareUid()).child(DbConstant.TYPEING_STATUS).child(mCurrent_user_id).setValue(isTyping);
    }

    private void loadAllMessage() {
//        txtNoUser.setVisibility(View.GONE);
        setProgressbarVisibillity(View.VISIBLE);
        DatabaseReference messageRef = mRootReference.child(DbConstant.MESSAGES).child(compareUid()).child(DbConstant.CHAT);

        messageRef.orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                setProgressbarVisibillity(View.GONE);
                Iterable<DataSnapshot> snapshot = dataSnapshot.getChildren();
                for (DataSnapshot snap : snapshot) {
                    if (snap.getValue() == null) continue;
                    /*DebugLog.d("snap : " + snap.getValue().toString().replace("{", "{\" ")
                            .replace("}", "\"}")
                            .replace("=", "\":\"").replace(",", "\",\""));
*/
                    Gson gson = new Gson();
                    ChatMessage sendMessage = gson.fromJson(snap.getValue().toString().replace("{", "{\" ")
                            .replace("}", "\"}")
                            .replace("=", "\":\"").replace(",", "\",\""), ChatMessage.class);

                    sendMessage.setMessageType(sendMessage.getSenderId().equals(mCurrent_user_id) ? MessageType.SENDER : MessageType.RECEIVER);

//                    messagesList.add(sendMessage);
//                    DebugLog.d("msg list:" + messagesList.size());
                }

                initData(messagesList);
                createNewMessageListener(messageRef);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                setProgressbarVisibillity(View.GONE);
//                txtNoUser.setVisibility(View.VISIBLE);
//                rvChat.setVisibility(View.GONE);
                DebugLog.v("data " + databaseError);
            }
        });
    }

    private void createNewMessageListener(DatabaseReference messageRef) {
        messageRef.orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DebugLog.v("data " + dataSnapshot);

                if (dataSnapshot.getValue() == null) return;

                Gson gson = new Gson();
                ChatMessage sendMessage = gson.fromJson(dataSnapshot.getValue().toString().replace("{", "{\" ")
                        .replace("}", "\"}")
                        .replace("=", "\":\"").replace(",", "\",\""), ChatMessage.class);

                sendMessage.setMessageType(sendMessage.getSenderId().equals(mCurrent_user_id) ? MessageType.SENDER : MessageType.RECEIVER);

                messagesList.add(sendMessage);
                initData(messagesList);

               /* ChatMessage messages = (ChatMessage) dataSnapshot.getValue(ChatMessage.class);

                itemPos++;

                if (itemPos == 1) {
                    String mMessageKey = dataSnapshot.getKey();

                    String mLastKey = mMessageKey;
                    String mPrevKey = mMessageKey;
                }

                messagesList.add(messages);
                adapter.notifyDataSetChanged();
                rvChat.scrollToPosition(messagesList.size() - 1);*/

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DebugLog.v("data " + dataSnapshot);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                DebugLog.v("data " + dataSnapshot);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DebugLog.v("data " + dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                DebugLog.v("data " + databaseError);

            }
        });
    }

    public class RecyclerViewScrollChange extends RecyclerView.OnScrollListener {
        TextView txtHeaderDate;

        RecyclerViewScrollChange(TextView txtHeaderDate) {
            this.txtHeaderDate = txtHeaderDate;
        }

        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                new Handler().postDelayed(() -> {
                    if (txtHeaderDate != null) txtHeaderDate.setVisibility(View.GONE);
                }, 3000);
            } else {
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstVisibleItemPosition = Objects.requireNonNull(linearLayoutManager).findFirstVisibleItemPosition();
            ChatMessage item = adapter.getItem(firstVisibleItemPosition);

            if (item == null) return;
            txtHeaderDate.setText(AppHelper.getInstance().getFormattedDate(Util.parseLong(item.getTimestamp())));
            txtHeaderDate.setVisibility(View.VISIBLE);
        }
    }

    public void setProgressbarVisibillity(int visibillity) {
        progressBar.setVisibility(visibillity);
    }
}
