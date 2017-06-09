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

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.activities.MessagesActivity;
import com.example.pma_tim10.chatapp.adapters.ConversationsArrayAdapter;
import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.model.Conversation;
import com.example.pma_tim10.chatapp.service.ConversationService;
import com.example.pma_tim10.chatapp.service.IConversationService;
import com.example.pma_tim10.chatapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ConversationsTabFragment extends ListFragment implements AdapterView.OnItemClickListener {

    ArrayList<Conversation> conversations;
    ConversationsArrayAdapter conversationsArrayAdapter;

    IConversationService iConversationService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.conversations_tab, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        iConversationService = new ConversationService();

        conversations = new ArrayList<>();
        conversationsArrayAdapter = new ConversationsArrayAdapter(getActivity(),android.R.id.list, conversations);
        setListAdapter(conversationsArrayAdapter);
        getListView().setOnItemClickListener(this);

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
        conversationsArrayAdapter.notifyDataSetChanged();
    }

}
