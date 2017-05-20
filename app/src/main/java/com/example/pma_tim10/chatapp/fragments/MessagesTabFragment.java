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
import com.example.pma_tim10.chatapp.dao_layer.UserDAO;
import com.example.pma_tim10.chatapp.model.Person;
import com.example.pma_tim10.chatapp.model.User;

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

        ArrayList<Person> messagesList = new ArrayList<>();
        messagesList.add(new Person("Djuro","Dje si djurooo legendooo...","testing_image.png", 84));
        messagesList.add(new Person("Deda","E deda imas za pljuge?","testing_image.png", 114));
        messagesList.add(new Person("Djed","Djedo djes to ?","testing_image.png", 78));
        messagesList.add(new Person("Joja","Kad idemo da rusimo zid?","testing_image.png", 29));
        messagesList.add(new Person("Djuro","E djuro tuljane jedan...","testing_image.png", 84));
        messagesList.add(new Person("Deda","Dedooo careee","testing_image.png", 114));
        messagesList.add(new Person("Djed","Sta mai?","testing_image.png", 78));
        messagesList.add(new Person("Joja",":D","testing_image.png", 29));
        messagesList.add(new Person("Djuro","Djro hpcmo na pkvo>","testing_image.png", 84));
        messagesList.add(new Person("Deda","Aloo...","testing_image.png", 114));
        messagesList.add(new Person("Djed","Ja","testing_image.png", 78));
        messagesList.add(new Person("Joja","Ajmo krecemo","testing_image.png", 29));

        MessagesArrayAdapter messagesArrayAdapter = new MessagesArrayAdapter(getActivity(),android.R.id.list, messagesList);

        setListAdapter(messagesArrayAdapter);
        getListView().setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(),ConversationActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }
}
