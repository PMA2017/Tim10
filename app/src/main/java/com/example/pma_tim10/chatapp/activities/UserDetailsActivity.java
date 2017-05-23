package com.example.pma_tim10.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.service.UserService;
import com.example.pma_tim10.chatapp.service.UserServiceImpl;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

/**
 * Created by Daniel on 5/23/2017.
 */

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = UserDetailsActivity.class.getSimpleName();

    private String userId;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private UserService userService;

    private ImageButton ibtnAddFriend;
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

        userService = new UserServiceImpl();

        userId = getIntent().getStringExtra(Constants.IE_USER_ID_KEY);

        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ivUserPhoto = (ImageView) findViewById(R.id.user_profile_photo);
        tvUserFullName = (TextView) findViewById(R.id.user_profile_name);
        tvUserEmail = (TextView) findViewById(R.id.user_email);
        tvUserAboutMe = (TextView) findViewById(R.id.user_about_me);
        ibtnAddFriend = (ImageButton) findViewById(R.id.add_friend);

        ibtnAddFriend.setOnClickListener(this);

        setButtonTag(userId);
        getUsersDetails(userId);
    }

    private void getUsersDetails(String userId){
        databaseReference.child(Constants.USER_TABLE).child(userId).addValueEventListener(new ValueEventListener(){

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
    }

    private void addFriend() {
        userService.addFriend(userProfile.getUid());
    }

    private void removeFriend() {
        userService.removeFriend(userProfile.getUid());
    }

    @Override
    public void onBackPressed() {
        goToMainActivity();
    }

}