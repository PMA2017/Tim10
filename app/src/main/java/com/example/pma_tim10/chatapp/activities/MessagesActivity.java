package com.example.pma_tim10.chatapp.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.pma_tim10.chatapp.ChatApp;
import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.adapters.MessagesArrayAdapter;
import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.fragments.ManageUsersDialogFragment;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.model.Message;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.service.ConversationService;
import com.example.pma_tim10.chatapp.service.GPSTracker;
import com.example.pma_tim10.chatapp.service.IConversationService;
import com.example.pma_tim10.chatapp.service.IMessageService;
import com.example.pma_tim10.chatapp.service.IUserService;
import com.example.pma_tim10.chatapp.service.MessageService;
import com.example.pma_tim10.chatapp.service.UserService;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dorian on 5/16/2017.
 */

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MessagesActivity.class.getSimpleName();

    //
    private String conversationId;
    private Conversation mConversation;

    //
    private List<Message> mMessages;
    private HashMap<String,User> mUsersInChat;

    //
    private MessagesArrayAdapter messagesArrayAdapter;
    private RecyclerView recyclerView;

    //
    private IMessageService messageService;
    private IConversationService conversationService;
    private IUserService userService;
    private GPSTracker gpsTracker;

    //
    private FirebaseUser currentUser;

    //
    private EditText etNewMessageText;
    private ImageButton btnSendMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        bindViews();
        initRecyclerViewComponents();
        initServices();

        // get current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // get conversation id
        conversationId = getIntent().getStringExtra(Constants.IE_CONVERSATION_ID_KEY);

        initConversation();
        initConversationUsers();
    }

    private void bindViews() {
        etNewMessageText = (EditText) findViewById(R.id.text_field_for_message);
        btnSendMessage = (ImageButton) findViewById(R.id.send_message_button);
        btnSendMessage.setOnClickListener(this);
    }

    private void initRecyclerViewComponents() {
        mMessages = new ArrayList<>();
        mUsersInChat = new HashMap<>();
        messagesArrayAdapter = new MessagesArrayAdapter(mMessages, mUsersInChat, this);
        recyclerView = (RecyclerView) findViewById(R.id.message_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messagesArrayAdapter);
    }

    private void initServices() {
        messageService = new MessageService();
        conversationService = new ConversationService();
        userService = new UserService();
        gpsTracker = new GPSTracker(getApplicationContext());
    }

    private void initConversation() {
        conversationService.getConversation(conversationId, new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                mConversation = (Conversation) data.get(0);
                setTitle(mConversation.getName().replace(currentUser.getDisplayName(), ""));
            }
        });
    }

    private void initConversationUsers() {
        if(mUsersInChat.size() == 0 && conversationId != null)
            conversationService.getConversationUsers(conversationId, new IFirebaseCallback() {
                @Override
                public void notifyUI(List data) {
                    initUsers((List<User>)data);
                    populateMessages(conversationId);
                }
            });
    }

    private void initUsers(List<User> users) {
        mUsersInChat.clear();
        for(User u : users)
            mUsersInChat.put(u.getUid(), u);
    }

    private void populateMessages(final String conversationId){
        messageService.getMessages(conversationId, new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                if (data.size() > 0)
                    updateUI((List<Message>) data);
            }
        });
    }

    private void updateUI(List<Message> messages) {
        this.mMessages.clear();
        this.mMessages.addAll(messages);
        this.messagesArrayAdapter.notifyDataSetChanged();
        scrollDown();
    }

    private void goToMainActivity(){
        Log.d(TAG,"Going to main activity");
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goToMainActivity();
    }


    @Override
    public void onClick(View v) {
        int btnId = v.getId();
        switch (btnId){
            case R.id.send_message_button:
                sendMessage();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_manage_users:
                manageUsers();
        }
        return true;
    }

    private void manageUsers() {
        FragmentManager fm = getFragmentManager();
        ManageUsersDialogFragment mudf = new ManageUsersDialogFragment();
        mudf.show(fm,"ManageUsersFragment");
    }

    private void sendMessage() {
        String msgText = etNewMessageText.getText().toString();
        if(msgText.trim().isEmpty()){
            //neki alert
            return;
        }

        double latitude;
        double longitude;

        final Message newMsg = new Message();
        newMsg.setContent(msgText);
        newMsg.setSenderName(currentUser.getDisplayName());
        newMsg.setSender(currentUser.getUid());
        etNewMessageText.setText("");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isLocation = prefs.getBoolean(Constants.LOCATION_STATE,true);
        if (isLocation) {
            Location location = gpsTracker.getLocation();
            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                newMsg.setLatitude(latitude);
                newMsg.setLongitude(longitude);
            } else {
                //Toast.makeText(MessagesActivity.this, "Cannot find location", Toast.LENGTH_SHORT).show();
            }
        }

        messageService.sendMessage(conversationId, newMsg, mUsersInChat, new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                if(mMessages.size() <= 1) {
                    for(String userId : mUsersInChat.keySet())
                        if(!userId.equals(currentUser.getUid())){
                            conversationService.updateUserStatusInConversation(conversationId, userId, new IFirebaseCallback() {
                                @Override
                                public void notifyUI(List data) {
                                }
                            });
                            break;
                        }
                }
            }
        });
    }

    private void scrollDown(){
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(mMessages.size()-1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_message, menu);
        return true;
    }

    public void showMapDialog(final double latitude, final double longitude) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(R.layout.dialogmap);

        GoogleMap googleMap;


        MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(this);

        mMapView = (MapView) dialog.findViewById(R.id.mapView);
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng posisiabsen = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(posisiabsen).title("Yout title"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        });

        Button dialogButton = (Button) dialog.findViewById(R.id.cancel_map_btn);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatApp.setChatActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ChatApp.setChatActivityOpen(false);
    }
}
