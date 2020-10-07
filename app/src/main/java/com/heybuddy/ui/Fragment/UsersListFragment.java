package com.heybuddy.ui.Fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.heybuddy.Model.ChatMessage;
import com.heybuddy.Model.UserData;
import com.heybuddy.R;
import com.heybuddy.ViewHolder.RecentChatHolder;
import com.heybuddy.base.BaseFragment;
import com.heybuddy.constant.AppConstant;
import com.heybuddy.constant.DbConstant;
import com.heybuddy.enumeration.BackStack;
import com.heybuddy.enumeration.FragmentState;
import com.heybuddy.generic_adapter.GenericAdapter;
import com.heybuddy.listener.iDialogButtonClick;
import com.heybuddy.ui.Presenter.ChatListPresenter;
import com.heybuddy.ui.view.ChatListView;
import com.heybuddy.utility.AppHelper;
import com.heybuddy.utility.DebugLog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UsersListFragment extends BaseFragment<ChatListView, ChatListPresenter> implements ChatListView {


    @BindView(R.id.txtLogout)
    AppCompatTextView txtLogout;
    @BindView(R.id.txtNewChat)
    AppCompatTextView txtNewChat;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.txtNoUser)
    TextView txtNoUser;

    private List<UserData> userList;
    FirebaseFirestore mUserDatabse;

    @Override
    public ChatListPresenter initView() {
        return new ChatListPresenter(this);
    }

    public static UsersListFragment newInstance() {
        return new UsersListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_list_fragment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        initUI();

        //  remove notification
        NotificationManager notifManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
    }

    private void initUI() {
        userList = new ArrayList<>();
//        filterListBaseOnStatus();
        getUserList();


    }


    public void setProgressbarVisibillity(int visibillity) {
        progressBar.setVisibility(visibillity);
    }

    private void getUserList() {
        setProgressbarVisibillity(View.VISIBLE);
        mUserDatabse = FirebaseFirestore.getInstance();

        mUserDatabse.collection(DbConstant.USERS)
                .whereEqualTo(DbConstant.USER_TYPE, AppHelper.getInstance().getLoginUserType().equals(AppConstant.NEEDABUDY) ? AppConstant.BEABUDDY : AppConstant.NEEDABUDY).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Log.d("db data", document.getId() + " => " + document.getData());
                            JSONObject jsonObject = new JSONObject(document.getData());
                            try {
                                Gson gson = new Gson();
                                UserData userData = gson.fromJson(jsonObject.toString(), UserData.class);
                                if (userData.getUid().equals(AppHelper.getInstance().getUid()))
                                    continue;

//                                if (!AppHelper.getInstance().getLoginUserType().equals(userData.getLoginType()))
                                userList.add(userData);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        filterListBaseOnStatus();
                    }
//                    homeActivity.hideProgressDialog();
                })
                .addOnFailureListener(e -> {
//                    homeActivity.hideProgressDialog();
                    showSnackBar(e.getMessage());
                });
    }

    private void manageErrorText() {
        txtNoUser.setVisibility(userList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void filterListBaseOnStatus() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(DbConstant.USER_STATUS);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.getValue() == null) continue;
                    DebugLog.d("snap : " + snap);
                    try {

                        String online = new JSONObject(String.valueOf(snap.getValue())).getString(DbConstant.ONLINE);
                        UserData userData = new UserData();
                        userData.setUid(snap.getKey());
                        int index = userList.indexOf(userData);

                        if (index == -1) continue;

                        userList.get(index).setOnline(online);

                       /* if (!online.equals("true")) {
                            DebugLog.d("remove : " + snap.getKey());
                            userList.remove(index);
                        }*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                setRecentChatAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DebugLog.v("data " + dataSnapshot);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DebugLog.v("data " + dataSnapshot);
                if (dataSnapshot.getValue() == null) return;
                try {
//                    String userType = new JSONObject(String.valueOf(dataSnapshot.getValue())).getString(DbConstant.USER_TYPE);

                    String online = new JSONObject(String.valueOf(dataSnapshot.getValue())).getString(DbConstant.ONLINE);
                    UserData userData = new UserData();
                    userData.setUid(dataSnapshot.getKey());
                    int index = userList.indexOf(userData);
                    if (index == -1) return;
                    userList.get(index).setOnline(online);
                    adapter.notifyItemChanged(index);
/*
                    if (!userType.equals(AppConstant.BEABUDDY)) {
                        if (index == -1) return;
//                        userList.get(index).setOnline(online);
                        userList.remove(index);
                        adapter.notifyItemRemoved(index);

                        manageErrorText();
                    } else {
                        if (index != -1) {
//                            userList.get(index).setOnline(online);
                            adapter.notifyItemChanged(index);
                        } else {
                            mUserDatabse.collection(DbConstant.USERS)
                                    .whereEqualTo(DbConstant.UID, userData.getUid())
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                Log.d("db data", document.getId() + " => " + document.getData());
                                                JSONObject jsonObject = new JSONObject(document.getData());
                                                try {
                                                    Gson gson = new Gson();
                                                    UserData userData1 = gson.fromJson(jsonObject.toString(), UserData.class);
                                                    if (userData1.getUid().equals(AppHelper.getInstance().getUid()))
                                                        continue;

                                                    int checkUserIsExists = userList.indexOf(userData);
                                                    if (checkUserIsExists == -1) {
                                                        userList.add(userData1);
                                                        adapter.notifyItemInserted(userList.size() - 1);
                                                        manageErrorText();
                                                    } else {
                                                        userList.set(checkUserIsExists, userData1);
                                                        adapter.notifyItemChanged(checkUserIsExists);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }
                                    });
                        }
                    }*/


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                DebugLog.v("data " + dataSnapshot);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filterListBaseOnUserType() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(DbConstant.USER_STATUS);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.getValue() == null) continue;
                    DebugLog.d("snap : " + snap);
                    try {
                        String online = new JSONObject(String.valueOf(snap.getValue())).getString(DbConstant.ONLINE);
                        UserData userData = new UserData();
                        userData.setUid(snap.getKey());
                        int index = userList.indexOf(userData);

                        if (index == -1) continue;

                        userList.get(index).setOnline(online);

                       /* if (!online.equals("true")) {
                            DebugLog.d("remove : " + snap.getKey());
                            userList.remove(index);
                        }*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                setRecentChatAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DebugLog.v("data " + dataSnapshot);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DebugLog.v("data " + dataSnapshot);
                if (dataSnapshot.getValue() == null) return;
                try {
                    String online = new JSONObject(String.valueOf(dataSnapshot.getValue())).getString(DbConstant.ONLINE);
                    UserData userData = new UserData();
                    userData.setUid(dataSnapshot.getKey());
                    int index = userList.indexOf(userData);

                    if (!online.equals("true")) {
                        if (index == -1) return;
                        userList.get(index).setOnline(online);
//                        userList.remove(index);
                        adapter.notifyItemRemoved(index);

                        manageErrorText();
                    } else {
                        if (index != -1) {
                            userList.get(index).setOnline(online);
                            adapter.notifyItemChanged(index);
                        } else {
                            mUserDatabse.collection(DbConstant.USERS)
                                    .whereEqualTo(DbConstant.UID, userData.getUid())
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {

                                                Log.d("db data", document.getId() + " => " + document.getData());
                                                JSONObject jsonObject = new JSONObject(document.getData());
                                                try {
                                                    Gson gson = new Gson();
                                                    UserData userData1 = gson.fromJson(jsonObject.toString(), UserData.class);
                                                    if (userData1.getUid().equals(AppHelper.getInstance().getUid()))
                                                        continue;

                                                    int checkUserIsExists = userList.indexOf(userData);
                                                    if (checkUserIsExists == -1) {
                                                        userList.add(userData1);
                                                        adapter.notifyItemInserted(userList.size() - 1);
                                                        manageErrorText();
                                                    } else {
                                                        userList.set(checkUserIsExists, userData1);
                                                        adapter.notifyItemChanged(checkUserIsExists);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }
                                    });
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                DebugLog.v("data " + dataSnapshot);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private GenericAdapter<UserData, RecentChatHolder> adapter;

    private void setRecentChatAdapter() {
        setProgressbarVisibillity(View.GONE);

        adapter = new GenericAdapter<UserData, RecentChatHolder>(R.layout.row_recent_chat, RecentChatHolder.class, userList) {

            @Override
            public void loadMore() {
            }

            @Override
            public void setViewHolderData(RecentChatHolder holder, final UserData data, final int position) {

                holder.txtName.setText(data.getUsername());

                if (data.isTyping()) {
                    holder.txtMessage.setText(getString(R.string.typing));
                } else {
                    holder.txtMessage.setText(data.getLastMsg());
                }

                if (data.getOnline().equals(AppConstant.ONLINE_TRUE)) {
                    holder.imgOnline.setVisibility(View.VISIBLE);
                    Glide.with(baseActivity)
                            .load(R.drawable.ic_online)
                            .into(holder.imgOnline);
                } else {
                    holder.imgOnline.setVisibility(View.VISIBLE);
                    Glide.with(baseActivity)
                            .load(R.drawable.ic_offline)
                            .into(holder.imgOnline);
                }

                Glide.with(baseActivity)
                        .load(data.getProfile())
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.ic_place_holder)
                        .into(holder.imgProfile);
                holder.llRow.setOnClickListener(view -> homeActivity.openChatFragment(FragmentState.REPLACE, BackStack.YES, data));
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        manageErrorText();

        getLastMsg();
    }

    private void getLastMsg() {
        for (UserData userData : userList) {
            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference(DbConstant.MESSAGES)
                    .child(AppHelper.getInstance().compareUidWithCurrentUser(userData.getUid()));

            reference.child(DbConstant.CHAT).orderByChild(DbConstant.TIMESTAMP)
                    .limitToLast(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    int index = userList.indexOf(userData);

                    if (index == -1 || dataSnapshot.getValue() == null) return;
                    Gson gson = new Gson();
                    ChatMessage sendMessage = gson.fromJson(dataSnapshot.getValue().toString()
                                    .replace("{", "{\"")
                                    .replace("}", "\"}")
                                    .replace("=", "\":\"")
                                    .replace(",", "\",\"")
                            , ChatMessage.class);

                    if (sendMessage == null) return;
                    userList.get(index).setLastMsg(sendMessage.getMessage());
                    adapter.notifyItemChanged(index);

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            reference.child(DbConstant.TYPEING_STATUS).child(userData.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int index = userList.indexOf(userData);
                    if (index == -1 || dataSnapshot.getValue() == null) return;
                    boolean isTyping = (Boolean) dataSnapshot.getValue();
                    userList.get(index).setTyping(isTyping);
                    adapter.notifyItemChanged(index);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }


    @OnClick({R.id.txtLogout, R.id.txtNewChat})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txtLogout:
                homeActivity.openConfirmationDialog(getString(R.string.are_you_sure_you_want_to_logout), getString(R.string.yes), getString(R.string.no), new iDialogButtonClick() {
                    @Override
                    public void negativeClick() {
                    }

                    @Override
                    public void positiveClick() {
                        homeActivity.logout();
                    }
                });

                break;
            case R.id.txtNewChat:
                break;
        }
    }
}
