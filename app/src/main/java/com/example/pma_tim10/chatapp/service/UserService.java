package com.example.pma_tim10.chatapp.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.example.pma_tim10.chatapp.utils.Utility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniel on 5/22/2017.
 */

public class UserService implements IUserService {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public UserService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.storage = FirebaseStorage.getInstance();
        this.storageReference = storage.getReference();
    }

    @Override
    public void getPeople(final IFirebaseCallback callback) {
        FirebaseDatabase.getInstance().getReference().child(Constants.USERS).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<User> people = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    // exclude current user
                    if(currentUser != null & user.getUid().equals(currentUser.getUid()))
                        continue;
                    // TO-DO : find and exclude friends
                    people.add(user);
                }

                //update ui
                callback.notifyUI(people);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getFriends(final IFirebaseCallback callback) {
        // get uids of current user friends
        databaseReference.child(Constants.FRIENDSHIPS)
                .child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // clear adapter list if friendships are changed
                final ArrayList<User> friends = new ArrayList<>();

                Map<String, Boolean> objects = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if (objects == null)
                    return;

                // get details for friends
                // or update friend if changed
                for(final String friendId : objects.keySet()){
                    databaseReference.child(Constants.USERS).child(friendId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            // friend updated
                            if(friends.contains(user)){
                                int idx = friends.indexOf(user);
                                friends.remove(idx);
                                friends.add(idx,user);
                            }else{
                                friends.add(user);
                            }
                            callback.notifyUI(friends);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                //update UI
                callback.notifyUI(friends);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getUserDetails(String userId, final IFirebaseCallback callback) {
        databaseReference.child(Constants.USERS).child(userId).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<User> users = new ArrayList<User>();
                final User user = dataSnapshot.getValue(User.class);

                String path = Constants.USER_PROFILE_PHOTO_PATH + user.getUid();
                final long ONE_MEGABYTE = 1024 * 1024;
                storageReference.child(path).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Data for "images/island.jpg" is returns, use this as needed
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
                        user.setUserProfilePhoto(bitmap);
                        users.clear();
                        users.add(user);
                        callback.notifyUI(users);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        users.clear();
                        users.add(user);
                        callback.notifyUI(users);
                    }
                });

//                users.add(user);
//                callback.notifyUI(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void addFriend(String friendsUid) {
        // friendships:
        //logged_in_id:
        // friendsUid : true
        //friendsUid:
        // logged_in_id : true

        // add friend to logged in user
        databaseReference.child(Constants.FRIENDSHIPS).child(currentUser.getUid()).child(friendsUid).setValue(true);

        // add logged in user to friend
        databaseReference.child(Constants.FRIENDSHIPS).child(friendsUid).child(currentUser.getUid()).setValue(true);

    }

    @Override
    public void removeFriend(String friendsUid) {
        // friendships:
        //logged_in_id:
        // friendsUid : true
        //friendsUid:
        // logged_in_id : true

        // remove friend from logged in user
        databaseReference.child(Constants.FRIENDSHIPS).child(currentUser.getUid()).child(friendsUid).removeValue();

        // remove logged in user from friend
        databaseReference.child(Constants.FRIENDSHIPS).child(friendsUid).child(currentUser.getUid()).removeValue();
    }

    @Override
    public void setOnline() {
        databaseReference.child(Constants.USERS).child(currentUser.getUid()).child(Constants.USER_ONLINE_FIELD).setValue(true);
    }

    @Override
    public void setOffline() {
        databaseReference.child(Constants.USERS).child(currentUser.getUid()).child(Constants.USER_ONLINE_FIELD).setValue(false);
    }

    @Override
    public void uploadPhoto(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            bitmap = Utility.getResizedBitmap(bitmap,480,640);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            String path = Constants.USER_PROFILE_PHOTO_PATH + currentUser.getUid();
            UploadTask uploadTask = storageReference.child(path).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });
        }catch (Exception e)
        {

        }
        finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
