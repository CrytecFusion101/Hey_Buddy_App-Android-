package com.heybuddy.ui.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.heybuddy.Model.Conv;
import com.heybuddy.R;
import com.heybuddy.utility.PreferanceHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChatScreen extends Fragment {

    Unbinder unbinder;
    private FirebaseAuth firebaseAuth;
    EditText edtMessage;
    ImageView imgSendMessage;
    RecyclerView mConvList;
    private DatabaseReference myDatabse;
    public List<String> chatList;

    private DatabaseReference mConvDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mMessageDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;


    public static ChatScreen newInstance() {

        ChatScreen fragment = new ChatScreen();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatting_screen, container, false);
        super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);


        edtMessage = view.findViewById(R.id.edtMessage);
        imgSendMessage = view.findViewById(R.id.imgSendMessage);

        //--DEFINING RECYCLERVIEW OF THIS FRAGMENT---
        mConvList = view.findViewById(R.id.rvChat);

        //--GETTING CURRENT USER ID---
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = PreferanceHelper.getInstance().getUserDetails().getUid();

        //---REFERENCE TO CHATS CHILD IN FIREBASE DATABASE-----
        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("chats").child(mCurrent_user_id);

        //---OFFLINE FEATURE---
        mConvDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);

        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);

        //---SETTING LAYOUT FOR RECYCLER VIEW----
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);


        /*
        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
//        Log.d("login Uid",+firebaseAuth.getInstance.collection("users"));

       myDatabse =  FirebaseDatabase.getInstance().getReference("message");

       myDatabse.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//               Toast.makeText(getActivity(), "message::::"+dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
//               chatList.add(dataSnapshot.getValue().toString());
//               setChatList(chatList);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               Toast.makeText(getActivity(), "onCancelled", Toast.LENGTH_SHORT).show();
           }
       });

       imgSendMessage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               DebugLog.d("user:::"+  FirebaseAuth.getInstance().getCurrentUser().getUid());

               sendMessage();
           }
       });*/

       /*   imgSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new ChatMessage(edtMessage.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );

                // Clear the input
                edtMessage.setText("");
            }
        });*/
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        //---ADDING THE RECYCLERVIEW TO FIREBASE DATABASE DIRECTLY----

        //--ORDERING THE MESSAGE BY TIME----
        Query conversationQuery = mConvDatabase.orderByChild("time_stamp");
        FirebaseRecyclerAdapter<Conv, ConvViewHolder> friendsConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(

                //--CLASS FETCHED FROM DATABASE-- LAYOUT OF THE SINGLE ITEM--- HOLDER CLASS(DEFINED BELOW)---QUERY
                Conv.class,
                R.layout.row_chat,
                ConvViewHolder.class,
                conversationQuery
        ) {

            //---- GETTING DATA FROM DATABSE AND ADDING TO VIEWHOLDER-----
            @Override
            protected void populateViewHolder(final ConvViewHolder convViewHolder,
                                              final Conv conv, int position) {

                final String list_user_id = getRef(position).getKey();
                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                //---IT WORKS WHENEVER CHILD OF mMessageDatabase IS CHANGED---
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        convViewHolder.setMessage(data, conv.isSeen());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                //---ADDING NAME , IMAGE, ONLINE FEATURE , AND OPENING CHAT ACTIVITY ON CLICK----
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            convViewHolder.setUserOnline(userOnline);

                        }
                        convViewHolder.setName(userName);
                        convViewHolder.setUserImage(userThumb, getContext());

                        //--OPENING CHAT ACTIVITY FOR CLICKED USER----
                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

//                                Intent chatIntent = new Intent(getContext(),ChatActivity.class);
//                                chatIntent.putExtra("user_id",list_user_id);
//                                chatIntent.putExtra("user_name",userName);
//                                startActivity(chatIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mConvList.setAdapter(friendsConvAdapter);

    }


    //--- DATA IS ADDING WITHIN SINGLE HOLDER-----
    public static class ConvViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ConvViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setMessage(String message, boolean isSeen) {
          /*  TextView userStatusView = (TextView) mView.findViewById(R.id.textViewSingleListStatus);
            userStatusView.setText(message);

            //--SETTING BOLD FOR NOT SEEN MESSAGES---
            if(isSeen){
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            }
            else{
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            }*/

        }

        public void setName(String name) {
//            TextView userNameView = (TextView) mView.findViewById(R.id.textViewSingleListName);
//            userNameView.setText(name);
        }


        public void setUserImage(String userThumb, Context context) {

//            CircleImageView userImageView = (CircleImageView)mView.findViewById(R.id.circleImageViewUserImage);

            //--SETTING IMAGE FROM USERTHUMB TO USERIMAGEVIEW--- IF ERRORS OCCUR , ADD USER_IMG----
//            Picasso.with(context).load(userThumb).placeholder(R.drawable.user_img).into(userImageView);
        }


        public void setUserOnline(String onlineStatus) {

//            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.userSingleOnlineIcon);
//            if(onlineStatus.equals("true")){
//                userOnlineView.setVisibility(View.VISIBLE);
//            }
//            else{
//                userOnlineView.setVisibility(View.INVISIBLE);
//            }
        }
    }


    public void sendMessage() {
//        myDatabse.push().setValue(edtMessage.getText().toString());
        myDatabse.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + " : User").setValue(edtMessage.getText().toString());
        edtMessage.setText("");


    }

    /* public void setChatList(List<String> chatList) {
     *//* this.chatList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            this.chatList.add("msg" + i);
        }*//*
        GenericAdapter<String, ChattingHolder> adapter = new GenericAdapter<String, ChattingHolder>(R.layout.row_chat, ChattingHolder.class, chatList) {
            @Override
            public void loadMore() {
            }

            @Override
            public void setViewHolderData(ChattingHolder holder, final String data, final int position) {
//                holder.txtMsg.setText(data);
            }
        };
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);
    }*/
}
