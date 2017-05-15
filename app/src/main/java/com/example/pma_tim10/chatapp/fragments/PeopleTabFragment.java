package com.example.pma_tim10.chatapp.fragments;

/**
 * Created by Dorian on 4/25/2017.
 */

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.adapters.MessagesArrayAdapter;
import com.example.pma_tim10.chatapp.adapters.PeopleArrayAdapter;
import com.example.pma_tim10.chatapp.model.Person;

import java.util.ArrayList;

public class PeopleTabFragment extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.people_tab, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<Person> peopleList = new ArrayList<>();
        peopleList.add(new Person("Djuro","Pucar Stari","testing_image.png", 84));
        peopleList.add(new Person("Deda","Ignjat","testing_image.png", 114));
        peopleList.add(new Person("Djed","Djuro","testing_image.png", 78));
        peopleList.add(new Person("Joja","Mali","testing_image.png", 29));
        peopleList.add(new Person("Djuro","Pucar Stari","testing_image.png", 84));
        peopleList.add(new Person("Deda","Ignjat","testing_image.png", 114));
        peopleList.add(new Person("Djed","Djuro","testing_image.png", 78));
        peopleList.add(new Person("Joja","Mali","testing_image.png", 29));
        peopleList.add(new Person("Djuro","Pucar Stari","testing_image.png", 84));
        peopleList.add(new Person("Deda","Ignjat","testing_image.png", 114));
        peopleList.add(new Person("Djed","Djuro","testing_image.png", 78));
        peopleList.add(new Person("Joja","Mali","testing_image.png", 29));

        PeopleArrayAdapter peopleArrayAdapter = new PeopleArrayAdapter(getActivity(),android.R.id.list, peopleList);

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
