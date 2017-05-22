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

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.adapters.PeopleArrayAdapter;
import com.example.pma_tim10.chatapp.service.UserService;
import com.example.pma_tim10.chatapp.service.UserServiceImpl;

import java.util.ArrayList;

public class PeopleTabFragment extends ListFragment {

    private UserService userService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.people_tab, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userService = new UserServiceImpl();

        PeopleArrayAdapter peopleArrayAdapter = new PeopleArrayAdapter(getActivity(),android.R.id.list, userService.getAllUsers());

        setListAdapter(peopleArrayAdapter);

        /*Button button = (Button) getView().findViewById(R.id.button_add);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Toast.makeText(getActivity(), "Not yet implemented!", Toast.LENGTH_SHORT).show();
            }
        });*/

//        getListView().setOnItemClickListener(this);

    }

}
