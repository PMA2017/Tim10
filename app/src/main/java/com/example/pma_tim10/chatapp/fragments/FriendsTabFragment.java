package com.example.pma_tim10.chatapp.fragments;

/**
 * Created by Dorian on 4/25/2017.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.adapters.FriendsArrayAdapter;
import com.example.pma_tim10.chatapp.model.Person;

import java.util.ArrayList;

public class FriendsTabFragment extends ListFragment implements AdapterView.OnItemClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friends_tab, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Hardcoded values, only for testing purpose
        ArrayList<Person> friendsList = new ArrayList<>();
        friendsList.add(new Person("Djuro","Pucar Stari","testing_image.png", 84));
        friendsList.add(new Person("Deda","Ignjat","testing_image.png", 114));
        friendsList.add(new Person("Djed","Djuro","testing_image.png", 78));
        friendsList.add(new Person("Joja","Mali","testing_image.png", 29));
        friendsList.add(new Person("Djuro","Pucar Stari","testing_image.png", 84));
        friendsList.add(new Person("Deda","Ignjat","testing_image.png", 114));
        friendsList.add(new Person("Djed","Djuro","testing_image.png", 78));
        friendsList.add(new Person("Joja","Mali","testing_image.png", 29));
        friendsList.add(new Person("Djuro","Pucar Stari","testing_image.png", 84));
        friendsList.add(new Person("Deda","Ignjat","testing_image.png", 114));
        friendsList.add(new Person("Djed","Djuro","testing_image.png", 78));
        friendsList.add(new Person("Joja","Mali","testing_image.png", 29));

        FriendsArrayAdapter friendsArrayAdapter = new FriendsArrayAdapter(getActivity(),android.R.id.list, friendsList);

        setListAdapter(friendsArrayAdapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(getActivity(), "Not yet implemented!", Toast.LENGTH_SHORT).show();
    }
}
