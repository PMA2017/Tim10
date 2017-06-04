package com.example.pma_tim10.chatapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.pma_tim10.chatapp.R;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dorian on 5/16/2017.
 */

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = ConversationActivity.class.getSimpleName();

    private String conversationID;
    private ArrayList<String> usersInChat;
    private IConversationService conversationService;
    private IMessageService messageService;
    private IUserService userService;
    private FirebaseUser currentUser;
    private EditText editText;
    private ScrollView scrollView;
    private ImageButton imageButton;
    private Integer lastId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        conversationService = new ConversationService();
        messageService = new MessageService();
        userService = new UserService();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        lastId = null;

        editText = (EditText) findViewById(R.id.text_field_for_message);
        imageButton = (ImageButton) findViewById(R.id.send_message_button);
        scrollView = (ScrollView) findViewById(R.id.conversation_scrool_view);

        imageButton.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            conversationID = extras.getString(Constants.CONVERSATION_ID_PARAM);
            usersInChat = extras.getStringArrayList(Constants.USERS_IN_CHAT);
        }

        if (conversationID != null)
            getMessages(conversationID);

    }

    private void goToMainActivity(){
        Log.d(TAG,"Going to main activity");
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getMessages(String conversationId) {
        messageService.getMessages(conversationId, new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                if (data.size() != 0)
                    updateUI((List<Message>) data);
            }
        });
    }

    private void updateUI(List<Message> messages) {
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.conversation_field_id);
        relativeLayout.removeAllViews();
        lastId = null;
        for (Message message : messages) {
            if (lastId == null){
                if (message.getSender().equals(currentUser.getUid())) {
                    LinearLayout linearLayout = getCurrentUserMessageUI(message);
                    linearLayout.setId(View.generateViewId());
                    relativeLayout.addView(linearLayout);
                    lastId = linearLayout.getId();
                }else{
                    LinearLayout linearLayout = getUserToChatMessageUI(message);
                    linearLayout.setId(View.generateViewId());
                    relativeLayout.addView(linearLayout);
                    lastId = linearLayout.getId();
                }
            }else if (message.getSender().equals(currentUser.getUid())) {
                LinearLayout linearLayout = getCurrentUserMessageUI(message);
                linearLayout.setId(View.generateViewId());
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.BELOW, lastId);
                relativeLayout.addView(linearLayout,lp);
                lastId = linearLayout.getId();

            }else{
                LinearLayout linearLayout = getUserToChatMessageUI(message);
                linearLayout.setId(View.generateViewId());
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.BELOW, lastId);
                relativeLayout.addView(linearLayout, lp);
                lastId = linearLayout.getId();
            }
        }
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private LinearLayout getCurrentUserMessageUI(Message message){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.right_message_in_conversation, null);

        TextView textView = (TextView)linearLayout.findViewById(R.id.right_message_textview_id);
        textView.setText(message.getContent());

        return linearLayout;
    }

    private LinearLayout getUserToChatMessageUI(Message message) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout)inflater.inflate(R.layout.left_message_in_conversation, null);

        TextView textView = (TextView)linearLayout.findViewById(R.id.left_message_textview_id);
        textView.setText(message.getContent());

        return linearLayout;
    }

    @Override
    public void onBackPressed() {
        goToMainActivity();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.send_message_button) {
             if (conversationID == null && usersInChat != null) {
                final Map<String, Object> members = new HashMap<>();
                for (String u : usersInChat) {
                    members.put(u, true);
                }

                final StringBuilder conversationName = new StringBuilder();

                userService.getFriends(new IFirebaseCallback() {
                    @Override
                    public void notifyUI(List data) {

                        List<User> friends = (List<User>)data;
                        for (int i = 0; i < friends.size(); i++) {
                            if (members.containsKey(friends.get(i).getUid())) {
                                if (i != 0)
                                    conversationName.append(",");
                                conversationName.append(friends.get(i).getName());
                                conversationName.append(" ");
                                conversationName.append(friends.get(i).getSurname());
                            }
                        }

                    }
                });
                 //temporary solution, display ony userUID. friends list size == 0
                 for (String u : usersInChat) {
                     conversationName.append(u + ", ");
                 }

                 members.put(currentUser.getUid(),true);

                final Conversation conversation = new Conversation();
                conversation.setName(conversationName.toString());
                conversation.setLastMessage("");
                conversation.setTimestamp((new Timestamp(System.currentTimeMillis())).getTime());
                conversation.setMembers(members);

                conversationService.addConversation(conversation, new IFirebaseCallback() {
                    @Override
                    public void notifyUI(List data) {
                        List<Conversation> conversations = (List<Conversation>)data;
                        conversationID = conversations.get(0).getId();
                        getMessages(conversationID);
                    }
                });
            }

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Message message = new Message();
            message.setContent(editText.getText().toString());
            message.setSender(currentUser.getUid());
            message.setSenderName(currentUser.getDisplayName());
            message.setTimestamp(timestamp.getTime());
            messageService.addMessage(message, conversationID);
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
            editText.setText("");

        }
    }
}
