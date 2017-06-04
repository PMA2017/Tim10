package com.example.pma_tim10.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.service.ConversationService;
import com.example.pma_tim10.chatapp.service.IConversationService;
import com.example.pma_tim10.chatapp.service.IUserService;
import com.example.pma_tim10.chatapp.service.UserService;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniel on 5/23/2017.
 */

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = UserDetailsActivity.class.getSimpleName();

    private String userId;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private IUserService IUserService;
    private IConversationService conversationService;

    private ImageButton ibtnAddFriend;
    private ImageButton ibtnOpenChat;
    private ImageView ivUserPhoto;
    private TextView tvUserFullName;
    private TextView tvUserEmail;
    private TextView tvUserAboutMe;

    private User userProfile;


    private final String btnAddTag = "addTag";
    private final String btnRemoveTag = "removeTag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        IUserService = new UserService();
        conversationService = new ConversationService();

        userId = getIntent().getStringExtra(Constants.IE_USER_ID_KEY);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ivUserPhoto = (ImageView) findViewById(R.id.user_profile_photo);
        tvUserFullName = (TextView) findViewById(R.id.user_profile_name);
        tvUserEmail = (TextView) findViewById(R.id.user_email);
        tvUserAboutMe = (TextView) findViewById(R.id.user_about_me);
        ibtnAddFriend = (ImageButton) findViewById(R.id.add_friend);
        ibtnOpenChat = (ImageButton) findViewById(R.id.open_chat);

        ibtnAddFriend.setOnClickListener(this);
        ibtnOpenChat.setOnClickListener(this);

        setButtonTag(userId);
        getUsersDetails(userId);
    }

    private void getUsersDetails(String userId){
        databaseReference.child(Constants.USERS).child(userId).addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfile = dataSnapshot.getValue(User.class);
                updateUI(userProfile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setButtonTag(String userId){
        databaseReference.child(Constants.FRIENDSHIPS).child(currentUser.getUid()).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // already friends
                if (dataSnapshot.exists()){
                    updateUIforIbtnAddFriend(btnRemoveTag);
                }else{
                    updateUIforIbtnAddFriend(btnAddTag);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateUIforIbtnAddFriend(String type){
        switch (type){
            case btnAddTag:
                ibtnAddFriend.setImageResource(R.drawable.ic_add_friend);
                ibtnAddFriend.setTag(btnAddTag);
                break;
            case btnRemoveTag:
                ibtnAddFriend.setImageResource(R.drawable.ic_remove_friend);
                ibtnAddFriend.setTag(btnRemoveTag);
                break;
        }
    }

    private void updateUI(User user) {
        tvUserFullName.setText(user.getName() + " " + user.getSurname());
        tvUserEmail.setText(user.getEmail());
        tvUserAboutMe.setText(user.getAboutMe());
    }

    private void goToMainActivity(){
        Log.d(TAG,"Going to main activity");
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        //btn add / remove friend
        if (i == R.id.add_friend) {
            String tag = (String) view.getTag();
            switch (tag) {
                case btnAddTag:
                    addFriend();
                    updateUIforIbtnAddFriend(btnRemoveTag);
                    break;
                case btnRemoveTag:
                    removeFriend();
                    updateUIforIbtnAddFriend(btnAddTag);
                    break;
            }
        }
        //btn open chat
        if (i == R.id.open_chat){
            final Conversation[] conversation = new Conversation[1];
            conversationService.getConversations(new IFirebaseCallback() {
                @Override
                public void notifyUI(List data) {
                    conversation[0] = findConversation((List<Conversation>)data);
                }
            });
            checkConversationAndDoAction(conversation[0]);
        }

    }

    private void checkConversationAndDoAction(Conversation currentConversation) {
        if (currentConversation == null) {
            ArrayList<String> usersInChat = new ArrayList<>();
            usersInChat.add(userProfile.getUid());
            goToChatActivity(null,usersInChat);
        } else {
            goToChatActivity(currentConversation.getId(),null);
        }
    }

    private Conversation findConversation(List<Conversation> conversations){
        for (Conversation conversation : conversations) {
            if (    conversation.getMembers().containsKey(currentUser.getUid()) &&
                    conversation.getMembers().containsKey(userProfile.getUid()) &&
                    conversation.getMembers().size() == 2)
                return conversation;
        }
        return null;
    }

    private void goToChatActivity(String conversationId, ArrayList<String> usersInChat) {
        Log.d(TAG,"Going to chat activity");
        Intent intent = new Intent(this,ConversationActivity.class);
        intent.putExtra(Constants.CONVERSATION_ID_PARAM,conversationId);
        intent.putStringArrayListExtra(Constants.USERS_IN_CHAT, usersInChat);
        startActivity(intent);
        finish();

    }

    private void addFriend() {
        IUserService.addFriend(userProfile.getUid());
    }

    private void removeFriend() {
        IUserService.removeFriend(userProfile.getUid());
    }

    @Override
    public void onBackPressed() {
        goToMainActivity();
    }

}
