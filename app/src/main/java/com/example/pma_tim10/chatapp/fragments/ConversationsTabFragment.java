package com.example.pma_tim10.chatapp.fragments;

/**
 * Created by Dorian on 4/25/2017.
 */

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.activities.MessagesActivity;
import com.example.pma_tim10.chatapp.adapters.ConversationsArrayAdapter;
import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.service.ConversationService;
import com.example.pma_tim10.chatapp.service.IConversationService;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConversationsTabFragment extends ListFragment implements AdapterView.OnItemClickListener , AdapterView.OnItemLongClickListener , View.OnClickListener {

    private ArrayList<Conversation> conversations;
    private ConversationsArrayAdapter conversationsArrayAdapter;

    private IConversationService conversationService;

    private Conversation selectedConversation;

    private ImageButton btnNewGroupChat;

    private EditText searchField;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.conversations_tab, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        conversationService = new ConversationService();

        searchField = (EditText)getActivity().findViewById(R.id.message_search);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0)
                    updateUI(filterConversationsByCriteria(charSequence.toString(),conversations));
                else
                    populateConversations();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        conversations = new ArrayList<>();
        conversationsArrayAdapter = new ConversationsArrayAdapter(getActivity(),android.R.id.list, conversations);
        setListAdapter(conversationsArrayAdapter);
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);

        registerForContextMenu(getListView());
        btnNewGroupChat = (ImageButton) getActivity().findViewById(R.id.new_message_button);
        btnNewGroupChat.setOnClickListener(this);

        populateConversations();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Conversation conversation = (Conversation)adapterView.getItemAtPosition(i);
        Intent intent = new Intent(getActivity(),MessagesActivity.class);
        intent.putExtra(Constants.IE_CONVERSATION_ID_KEY,conversation.getId());
        intent.putExtra(Constants.IE_CONVERSATION_NAME,conversation.getName());
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    private void populateConversations(){
        conversationService.getConversations(new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                updateUI((List<Conversation>) data);
            }
        });
    }

    private void updateUI(List<Conversation> data){
        conversations.removeAll(conversations);
        conversations.addAll(data);
        Collections.reverse(conversations);
        conversationsArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        selectedConversation = (Conversation)parent.getItemAtPosition(position);
        getListView().showContextMenu();
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_conversation, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete_conversation:
                deleteConversation();
        }
        return true;
    }

    private void deleteConversation() {
        conversationService.deleteConversation(selectedConversation, new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.new_message_button:
                openNewChatDialog();
        }
    }

    private void openNewChatDialog() {
        FragmentManager fm = getActivity().getFragmentManager();
        ManageUsersDialogFragment mudf = new ManageUsersDialogFragment();
        mudf.show(fm,"ManageUsersFragment");
    }

    private List<Conversation> filterConversationsByCriteria(String criteria, List<Conversation> conversations) {
        ArrayList<Conversation> result = new ArrayList<>();
        for (Conversation c : conversations) {
            String conversationName = c.getName().toLowerCase();
            if (conversationName.startsWith(criteria.toLowerCase()))
                result.add(c);
        }
        return result;
    }

}
