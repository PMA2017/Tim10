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
import android.widget.Toast;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.activities.MainActivity;
import com.example.pma_tim10.chatapp.activities.UserDetailsActivity;
import com.example.pma_tim10.chatapp.adapters.FriendsArrayAdapter;
import com.example.pma_tim10.chatapp.adapters.PeopleArrayAdapter;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.service.UserService;
import com.example.pma_tim10.chatapp.service.UserServiceImpl;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PeopleTabFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private List<User> people;
    private PeopleArrayAdapter peopleArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.people_tab, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        people = new ArrayList<>();
        peopleArrayAdapter = new PeopleArrayAdapter(getActivity(), android.R.id.list, people);
        setListAdapter(peopleArrayAdapter);
        getListView().setOnItemClickListener(this);

        populatePeople();
    }

    private void updateUI(){
        peopleArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = (User) parent.getItemAtPosition(position);
        goToUserDetailsActivity(user.getUid());
    }

    private void goToUserDetailsActivity(String uid){
        Intent intent = new Intent(getActivity(),UserDetailsActivity.class);
        intent.putExtra(Constants.IE_USER_ID_KEY, uid);
        startActivity(intent);
        getActivity().finish();
    }

    // GET DATA FROM FIREBASE -- MUST BE IN ACTIVITY/FRAGMENT CLASSES -- UPDATE UI
    private void populatePeople(){
        FirebaseDatabase.getInstance().getReference().child(Constants.USERS).addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                people.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    // exclude current user
                    if(user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        continue;
                    // TO-DO : find and exclude friends
                    people.add(user);
                }

                //update ui
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
