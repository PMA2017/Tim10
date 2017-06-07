package com.example.pma_tim10.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.adapters.MessagesArrayAdapter;
import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.model.Message;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.service.ConversationService;
import com.example.pma_tim10.chatapp.service.IConversationService;
import com.example.pma_tim10.chatapp.service.IMessageService;
import com.example.pma_tim10.chatapp.service.IUserService;
import com.example.pma_tim10.chatapp.service.MessageService;
import com.example.pma_tim10.chatapp.service.UserService;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dorian on 5/16/2017.
 */

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = MessagesActivity.class.getSimpleName();

    public static List<Message> messages;
    public static HashMap<String,User> usersInChat;

    private MessagesArrayAdapter messagesArrayAdapter;

    private String secondUserId;
    private String conversationId;

    private IMessageService messageService;
    private IConversationService conversationService;
    private IUserService userService;

    private FirebaseUser currentUser;

    private RecyclerView listView;

    private EditText etNewMessageText;
    private ImageButton btnSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        etNewMessageText = (EditText) findViewById(R.id.text_field_for_message);
        btnSendMessage = (ImageButton) findViewById(R.id.send_message_button);
        btnSendMessage.setOnClickListener(this);

        messages = new ArrayList<>();
        usersInChat = new HashMap<>();
        messagesArrayAdapter = new MessagesArrayAdapter(messages);
        listView = (RecyclerView) findViewById(R.id.message_list);
        listView.setAdapter(messagesArrayAdapter);


        messageService = new MessageService();
        conversationService = new ConversationService();
        userService = new UserService();

        secondUserId = getIntent().getStringExtra(Constants.IE_USER_ID_KEY);
        conversationId = getIntent().getStringExtra(Constants.IE_CONVERSATION_ID_KEY);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        populateMessages();
    }

    private void populateMessages() {
        if(conversationId == null)
            conversationService.getConversationIdForUserId(secondUserId, new IFirebaseCallback() {
                @Override
                public void notifyUI(List data) {
                    if(data.size() > 0)
                        getConversationData(((Conversation)data.get(0)).getId());
                    else
                        conversationId = UUID.randomUUID().toString();
                }
            });
        else
            getConversationData(conversationId);


    }

    private void getConversationData(final String conversationId){
        conversationService.getConversationUsers(conversationId, new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                initUsers((List<User>)data);
                getMessages(conversationId);
            }
        });
    }

    private void initUsers(List<User> users) {
        usersInChat.clear();
        for(User u : users)
            usersInChat.put(u.getUid(), u);
    }

    private void getMessages(String conversationId){
        messageService.getMessages(conversationId, new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                if (data.size() > 0)
                    updateUI((List<Message>) data);
            }
        });
    }

    private void updateUI(List<Message> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        this.messagesArrayAdapter.notifyDataSetChanged();
        scrollDown();
    }

    private void goToMainActivity(){
        Log.d(TAG,"Going to main activity");
        Intent intent = new Intent(this,MainActivity.class);
        usersInChat = null;
        messages = null;
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

    private void sendMessage() {
        String msgText = etNewMessageText.getText().toString();
        if(msgText.trim().isEmpty()){
            //neki alert
            return;
        }

        Message newMsg = new Message();
        newMsg.setContent(msgText);
        newMsg.setTimestamp(System.currentTimeMillis());
        newMsg.setSenderName(currentUser.getDisplayName());
        newMsg.setSender(currentUser.getUid());
        Collection<String> usersInChatIds = new ArrayList<>();
        if(usersInChat.isEmpty()){
            usersInChatIds.add(currentUser.getUid());
            usersInChatIds.add(secondUserId);
        }else
            usersInChatIds = usersInChat.keySet();
        messageService.sendMessage(newMsg, usersInChatIds , conversationId, new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                //scrollDown();
                etNewMessageText.setText("");
            }
        });
    }

    private void scrollDown(){
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.scrollToPosition(messages.size()-1);
            }
        });
    }

}
