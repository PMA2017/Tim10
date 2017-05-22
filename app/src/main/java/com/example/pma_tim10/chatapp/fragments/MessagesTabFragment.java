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
import com.example.pma_tim10.chatapp.activities.ConversationActivity;
import com.example.pma_tim10.chatapp.adapters.MessagesArrayAdapter;

import java.util.ArrayList;

public class MessagesTabFragment extends ListFragment implements AdapterView.OnItemClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.messages_tab, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //MessagesArrayAdapter messagesArrayAdapter = new MessagesArrayAdapter(getActivity(),android.R.id.list, messagesList);

//        setListAdapter(messagesArrayAdapter);
//        getListView().setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(),ConversationActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }
}
