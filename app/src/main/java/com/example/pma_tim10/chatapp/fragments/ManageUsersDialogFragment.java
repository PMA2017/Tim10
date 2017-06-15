package com.example.pma_tim10.chatapp.fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.activities.MainActivity;
import com.example.pma_tim10.chatapp.activities.MessagesActivity;
import com.example.pma_tim10.chatapp.adapters.ManageUsersArrayAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Aleksandar on 6/10/2017.
 */

public class ManageUsersDialogFragment extends DialogFragment {

    private static final String TAG = ManageUsersDialogFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private List<User> people;
    private ManageUsersArrayAdapter adapter;
    private IConversationService conversationService;
    private IUserService userService;
    private Button btnClose;
    private Button btnCreate;
    private EditText etConvName;


    private FirebaseUser currentUser;

    private HashMap<String,Boolean> conversationMembers;

    public ManageUsersDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_users, container, false);
        setCancelable(false);

        people = new ArrayList<>();
        userService = new UserService();
        conversationService = new ConversationService();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        conversationMembers = new HashMap<>();
        conversationMembers.put(currentUser.getUid(), true);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.chat_users_list);
        adapter = new ManageUsersArrayAdapter(people,conversationMembers);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerView.setAdapter(adapter);

        btnClose = (Button) rootView.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnCreate = (Button) rootView.findViewById(R.id.btn_new_chat);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateForm()) {
                    return;
                }
                createConversation();
            }
        });

        etConvName = (EditText) rootView.findViewById(R.id.et_conversation_name);


        populatePeople();

        return rootView;
    }

    private boolean validateForm() {
        boolean isValid = true;

        if(etConvName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getActivity(),"Name is required!",Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if(conversationMembers.size() < 3) {
            Toast.makeText(getActivity(),"This is group chat dialog, add more than 1 user!",Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }

    private void createConversation() {
        final Conversation newConversation = new Conversation();
        newConversation.setId(UUID.randomUUID().toString());
        newConversation.setMembers(conversationMembers);
        newConversation.setName(etConvName.getText().toString().trim());
        newConversation.setGroup(true);
        conversationService.createConversation(newConversation, new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                goToMessageActivity(newConversation.getId());
            }
        });
    }

    private void goToMessageActivity(final String conversationId){
        Log.d(TAG, "Going to chat activity");
        Intent intent = new Intent(getActivity(),MessagesActivity.class);
        intent.putExtra(Constants.IE_CONVERSATION_ID_KEY, conversationId);
        startActivity(intent);
        getActivity().finish();
    }

    private void populatePeople() {
        userService.getPeople(new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                updateUI((List<User>)data);
            }
        });
    }

    private void updateUI(List<User> data) {
        people.clear();
        people.addAll(data);
        adapter.notifyDataSetChanged();
    }


}
