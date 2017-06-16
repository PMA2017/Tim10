package com.example.pma_tim10.chatapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.callback.IFirebaseProgressCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.service.ConversationService;
import com.example.pma_tim10.chatapp.service.IConversationService;
import com.example.pma_tim10.chatapp.service.IUserService;
import com.example.pma_tim10.chatapp.service.UserService;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.example.pma_tim10.chatapp.utils.Utility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Daniel on 5/23/2017.
 */

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = UserDetailsActivity.class.getSimpleName();

    private String userId;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private IUserService userService;
    private IConversationService conversationService;

    private ImageButton ibtnAddFriend;
    private ImageButton ibtnOpenChat;
    private ImageView ivUserPhoto;
    private EditText etUserFullName;
    private TextView tvUserEmail;
    private EditText etUserAboutMe;

    private MenuItem miSave;

    private User userProfile;


    private final String btnAddTag = "addTag";
    private final String btnRemoveTag = "removeTag";


    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        userService = new UserService();
        conversationService = new ConversationService();

        userId = getIntent().getStringExtra(Constants.IE_USER_ID_KEY);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ivUserPhoto = (ImageView) findViewById(R.id.user_profile_photo);
        etUserFullName = (EditText) findViewById(R.id.user_profile_name);
        tvUserEmail = (TextView) findViewById(R.id.user_email);
        etUserAboutMe = (EditText) findViewById(R.id.user_about_me);
        ibtnAddFriend = (ImageButton) findViewById(R.id.add_friend);
        ibtnOpenChat = (ImageButton) findViewById(R.id.open_chat);

        ibtnAddFriend.setOnClickListener(this);
        ibtnOpenChat.setOnClickListener(this);
        ibtnOpenChat.setClickable(false);
        ibtnOpenChat.setEnabled(false);
        etUserAboutMe.setEnabled(false);
        etUserFullName.setEnabled(false);

        setButtonTag(userId);
        getUsersDetails(userId);

        setTitle("");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_update_user:
                updateUserDetails();
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUserDetails() {
        if(etUserFullName.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Name is required",Toast.LENGTH_SHORT).show();
            return;
        }
        // update user fields
        userService.updateUserProfile(etUserFullName.getText().toString().trim(), etUserAboutMe.getText().toString(), new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                getUsersDetails(currentUser.getUid());
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                Toast.makeText(UserDetailsActivity.this,"Profile updated",Toast.LENGTH_SHORT);
            }
        });
    }

    private void getUsersDetails(String userId){
        userService.getUserDetails(userId, new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                userProfile = ((List<User>)data).get(0);
                updateUI(userProfile);
                enableMessageButton();
                enableEditFields();
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_details, menu);

        this.miSave = menu.findItem(R.id.action_update_user);
        return true;
    }

    private void enableEditFields() {
        if(isCurrentUserProfile()){
            etUserAboutMe.setEnabled(true);
            etUserFullName.setEnabled(true);
            miSave.setVisible(true);
        }
    }

    private boolean isCurrentUserProfile(){
        return currentUser.getUid().equals(userProfile.getUid());
    }

    private void enableMessageButton() {
        ibtnOpenChat.setClickable(true);
        ibtnOpenChat.setEnabled(true);
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
        etUserFullName.setText(user.getFullName());
        tvUserEmail.setText(user.getEmail());
        etUserAboutMe.setText(user.getAboutMe());

        // profile photo loading
        Animation anim = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(1000);
        ivUserPhoto.startAnimation(anim);

        Bitmap bitmap = user.getUserProfilePhoto();
        if(bitmap!=null) {
            ivUserPhoto.setAnimation(null);
            ivUserPhoto.setImageBitmap(Utility.getCircleBitmap(bitmap));
        }

        if(isCurrentUserProfile()) {
            ivUserPhoto.setOnClickListener(this);
            ibtnAddFriend.setVisibility(View.GONE);
            ibtnOpenChat.setVisibility(View.GONE);
        }
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
            getConversationId();
        }

        //upload image
        if (i == R.id.user_profile_photo){
            selectImage();
        }

    }

    private void getConversationId() {
        // get conversation id or create new conversation if not exists
        conversationService.getConversationIdForUserId(userProfile.getUid(), new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                List<Conversation> conversations = data;
                if (conversations.size() > 0) {
                    goToMessageActivity(conversations.get(0).getId());
                }else{
                    // create new conversation
                    final Conversation newConversation = new Conversation();
                    newConversation.setId(UUID.randomUUID().toString());
                    newConversation.setMembers(new HashMap<String, Boolean>(){{put(userProfile.getUid(),false);
                                                                                put(currentUser.getUid(),true);}});
                    newConversation.setName(currentUser.getDisplayName() + " " + userProfile.getFullName());
                    conversationService.createConversation(newConversation, new IFirebaseCallback() {
                        @Override
                        public void notifyUI(List data) {
                            goToMessageActivity(newConversation.getId());
                        }
                    });
                }

            }
        });
    }

    private void goToMessageActivity(final String conversationId){
        Log.d(TAG,"Going to chat activity");
        Intent intent = new Intent(this,MessagesActivity.class);
        intent.putExtra(Constants.IE_CONVERSATION_ID_KEY, conversationId);
        startActivity(intent);
        finish();
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



    // PHOTO UPLOAD
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(UserDetailsActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(UserDetailsActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                openDialog();
                userService.uploadPhoto(bm, uploadProgressCallback, uploadSuccessCallback, uploadErrorCallback);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bm = (Bitmap) data.getExtras().get("data");
        openDialog();
        userService.uploadPhoto(bm, uploadProgressCallback, uploadSuccessCallback, uploadErrorCallback);
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//        File destination = new File(Environment.getExternalStorageDirectory(),
//                System.currentTimeMillis() + ".jpg");
//        FileOutputStream fo;
//        try {
//            destination.createNewFile();
//            fo = new FileOutputStream(destination);
//            fo.write(bytes.toByteArray());
//            fo.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void openDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading photo...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private IFirebaseProgressCallback uploadProgressCallback = new IFirebaseProgressCallback() {
        @Override
        public void changeProgressBarStatus(double value) {
            progressDialog.setProgress((int) value);
        }
    };

    private IFirebaseCallback uploadSuccessCallback = new IFirebaseCallback() {
        @Override
        public void notifyUI(List data) {
            progressDialog.dismiss();
            Toast.makeText(UserDetailsActivity.this,"Photo uploaded",Toast.LENGTH_SHORT).show();
            getUsersDetails(currentUser.getUid());
        }
    };

    private IFirebaseCallback uploadErrorCallback = new IFirebaseCallback() {
        @Override
        public void notifyUI(List data) {
            progressDialog.dismiss();
            Toast.makeText(UserDetailsActivity.this,"Photo is not uploaded",Toast.LENGTH_SHORT).show();
        }
    };

}
