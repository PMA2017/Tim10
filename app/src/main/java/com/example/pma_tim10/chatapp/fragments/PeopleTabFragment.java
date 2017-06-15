package com.example.pma_tim10.chatapp.fragments;

/**
 * Created by Dorian on 4/25/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pma_tim10.chatapp.R;
import com.example.pma_tim10.chatapp.activities.UserDetailsActivity;
import com.example.pma_tim10.chatapp.adapters.PeopleArrayAdapter;
import com.example.pma_tim10.chatapp.model.User;
import com.example.pma_tim10.chatapp.callback.IFirebaseCallback;
import com.example.pma_tim10.chatapp.service.IUserService;
import com.example.pma_tim10.chatapp.service.UserService;
import com.example.pma_tim10.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class PeopleTabFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private IUserService userService;

    private List<User> people;
    private PeopleArrayAdapter peopleArrayAdapter;
    private FirebaseUser currentUser;

    private EditText searchField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.people_tab, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userService = new UserService();

        searchField = (EditText)getView().findViewById(R.id.search_people);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0)
                    updateUI(filterPeopleByCriteria(charSequence.toString(),people));
                else
                    populatePeople();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        people = new ArrayList<>();
        peopleArrayAdapter = new PeopleArrayAdapter(getActivity(), android.R.id.list, people);
        setListAdapter(peopleArrayAdapter);
        getListView().setOnItemClickListener(this);

        populatePeople();
    }

    private void updateUI(List<User> data){
        people.removeAll(people);
        people.addAll(data);
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

    // GET DATA FROM FIREBASE
    private void populatePeople(){
        userService.getPeople(new IFirebaseCallback() {
            @Override
            public void notifyUI(List data) {
                updateUI((List<User>)data);
            }
        });
    }

    private List<User> filterPeopleByCriteria(String criteria, List<User> people) {
        ArrayList<User> result = new ArrayList<>();
        for (User u : people) {
            String userFullName = u.getFullName().toLowerCase();
            if (userFullName.startsWith(criteria.toLowerCase()))
                result.add(u);
        }
        return result;
    }

}
