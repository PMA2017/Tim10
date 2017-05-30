package com.example.pma_tim10.chatapp.fragments;

/**
 * Created by Dorian on 4/25/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.activities.ConversationActivity;
import com.example.pma_tim10.chatapp.adapters.MessagesArrayAdapter;
import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.service.IConversationService;

import java.util.ArrayList;
import java.util.List;

public class MessagesTabFragment extends ListFragment implements AdapterView.OnItemClickListener {

    ArrayList<Conversation> conversations;
    MessagesArrayAdapter messagesArrayAdapter;

    IConversationService iConversationService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.messages_tab, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        conversations = new ArrayList<>();
        messagesArrayAdapter = new MessagesArrayAdapter(getActivity(),android.R.id.list, conversations);
        setListAdapter(messagesArrayAdapter);
        getListView().setOnItemClickListener(this);

        populateConversations();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(),ConversationActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    private void populateConversations(){
        iConversationService.getConversations(new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                updateUI((List<Conversation>) data);
            }
        });
    }

    private void updateUI(List<Conversation> data){
        conversations.removeAll(conversations);
        conversations.addAll(data);
        messagesArrayAdapter.notifyDataSetChanged();
    }

}
